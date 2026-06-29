# Feature Implementation Guide

> **Read this before implementing ANY new feature.** Every feature must mirror the

> structure, conventions, and rules below so the codebase stays consistent.
>
> **Living reference:** `feature/chats/chatroom/src/main/java/sharechat/feature/chatroom/consultation/onboarding/`
> (the FriendZone onboarding feature). Whenever this document is ambiguous, open that
> folder and follow what the code actually does.

---

## 1. Architecture in one paragraph

A feature is a **single Activity** (`@AndroidEntryPoint`) that hosts Compose via
`setContent { SCLiveTheme { ... } }`. UI state is driven by an **Orbit MVI ViewModel**
(`BaseViewModel<State, SideEffect>`) using `intent { }`, `reduce { }`, and
`postSideEffect(...)`. Screens are wired through **Compose Navigation** (`NavHost`,
`composable`, `bottomSheet`) behind a typed `NavigationAction` provided via a
`CompositionLocal`. Dependencies are injected with **Hilt**; data flows through
**UseCases** returning `ApiResult.Success/Error`; reusable side-capabilities (audio
record/play, timers) are **Delegates** mixed into the ViewModel with Kotlin `by`
delegation. Composables are **stateless** and communicate only through passed lambdas.

---

## 2. Package / file layout

Replace `Xxx` with the feature name. Create exactly this structure:

```
feature/.../<feature>/
  XxxActivity.kt           # Activity entry point, intent factory, Compose host
  XxxState.kt              # @[Stable Keep] state + nested @Stable state + sealed statuses
  XxxViewModel.kt          # Orbit MVI ViewModel: intent()/reduce()/postSideEffect()
  XxxConstants.kt          # all event/page/action/status strings + dimens
  navigation/
    XxxIntent.kt           # sealed interface of user intents
    XxxSideEffect.kt       # sealed class of one-shot side effects
    XxxNavGraph.kt         # NavHost + side-effect handler + BackHandler + bottom sheets
    XxxNavigationAction.kt # nav action interface + impl + CompositionLocal
    XxxNavigationRoutes.kt # sealed route objects with navigate helpers
    XxxRoutesPath.kt       # @StringDef route path constants
  ui/                      # stateless @Composable screens/sheets + @StringDef screen/tab markers
  utils/                   # injected delegates (interface + @Inject impl) + pure util objects
```

---

## 3. Conventions (extracted from the reference)

### Activity
- Annotate `@AndroidEntryPoint`; extend `AppCompatActivity`.
- Expose a `companion object` with extra keys and an intent factory — never construct the
  `Intent` at the call site.
  ```kotlin
  companion object {
      const val CHATROOM_ID = "chatroom_id"
      const val REFERRER = "referrer"
      fun getActivityIntent(context: Context, chatRoomId: String?, referrer: String?): Intent =
          Intent(context, FriendZoneOnboardingActivity::class.java).apply {
              putExtra(CHATROOM_ID, chatRoomId)
              putExtra(REFERRER, referrer)
          }
  }
  ```
- Obtain the ViewModel with `by viewModels<XxxViewModel>()`.
- Host Compose inside the theme; pass the ViewModel **as a lambda** and pass result
  callbacks down to the NavGraph:
  ```kotlin
  setContent {
      SCLiveTheme(theme = LiveThemes.DARK) {
          XxxNavGraph(viewModel = { viewModel }, closeActivity = { finish() }, ...)
      }
  }
  ```
- Return data to the caller via `setResult(RESULT_OK, intent)` then `finish()`.
- Release resources in `onPause` by dispatching reset intents.

### State (`XxxState.kt`)
- Root state is `@[Stable Keep] data class` with a `companion object { fun getDefault() }`.
- Split large state into **nested `@Stable` data classes**, each with its own `getDefault()`.
- Model transient statuses as `sealed class` (e.g. `VoiceRecordingStatus`), using `object`
  for stateless cases and `data class` for parameterised ones.
- Use `emptyString()` for default strings; keep every field immutable (`val`).

### ViewModel (`XxxViewModel.kt`)
- `@HiltViewModel`, extend `BaseViewModel<XxxState, XxxSideEffect>(savedStateHandle)`.
- Constructor-inject UseCases, Delegates, `Gson`, `AnalyticsManager`.
- Mix in Delegates with `by`:
  ```kotlin
  class XxxViewModel @Inject constructor(...) :
      BaseViewModel<XxxState, XxxSideEffect>(savedStateHandle),
      VoiceRecorderDelegate by voiceRecorderDelegateImpl,
      AudioMediaPlayerDelegate by audioMediaPlayerDelegateImpl {
  ```
- Read Activity args with `argumentNullable(KEY)`:
  ```kotlin
  private val chatRoomId: String? by argumentNullable(XxxActivity.CHATROOM_ID)
  ```
- Override `initialState()` → `XxxState.getDefault()`, and do startup work in `initData()`
  (register delegate flow collectors, fetch details).
- **All state mutation happens inside `intent { reduce { state.copy(...) } }`.** Never
  mutate state anywhere else.
- **All navigation / toasts / one-shot effects go through `postSideEffect(...)`.**
- Expose a single intent entry point with an exhaustive `when`:
  ```kotlin
  fun handleXxxIntent(intent: XxxIntent) = intent {
      when (intent) {
          is XxxIntent.HandleClickEvent -> onClickCta(intent.ctaType)
          ...
      }
  }
  ```
- UseCase calls return `ApiResult`; branch on `Success`/`Error`:
  ```kotlin
  when (val result = createXxxUseCase(request)) {
      is ApiResult.Success -> postSideEffect(XxxSideEffect.CreatedSuccessfully(result.data))
      is ApiResult.Error   -> result.exception?.handleError()
  }
  ```
- Centralise error handling in one `Throwable?.handleError()` that distinguishes
  `NoNetworkException`, parses `HttpException` 4xx bodies with `gson`, and falls back to a
  generic "something went wrong" side effect.
- Collect delegate `MutableSharedFlow`s in `intent { ... .collect { reduce { ... } } }`.
- Clean up (`timerManager.stopTimer()`, etc.) in `onCleared()`.

### Intents & Side Effects
- `XxxIntent` is a **`sealed interface`** of user actions (`object` / `data class`).
- `XxxSideEffect` is a **`sealed class`** of one-shot effects: open screen, open bottom
  sheet, close activity, show toast, success-with-payload.
  ```kotlin
  sealed interface XxxIntent {
      data class HandleClickEvent(@XxxCtaType val ctaType: String) : XxxIntent
      object HandleBackPress : XxxIntent
  }
  sealed class XxxSideEffect {
      object CloseActivity : XxxSideEffect()
      data class CreatedSuccessfully(val response: ...) : XxxSideEffect()
  }
  ```

### Navigation
- `XxxRoutesPath` — `@StringDef` of route path string constants.
- `XxxNavigationRoutes` — sealed objects holding `route`, each overriding
  `getNavigationRoute()`, plus shared `navigate()` / `navigateSingleTop()` helpers.
- `XxxNavigationAction` — `interface` of `openXxxScreen()` methods + `internal` `Impl`
  wrapping the `NavController`, exposed through a `staticCompositionLocalOf`:
  ```kotlin
  val LocalXxxNavigationAction = staticCompositionLocalOf<XxxNavigationAction> {
      error("No NavigationAction specified")
  }
  ```

### NavGraph (`XxxNavGraph.kt`)
- Build `rememberNavController(bottomSheetNavigator)`; create the `NavigationActionImpl`
  with `remember(navController)`.
- Read state with `viewModel().stateFlow().collectAsStateWithLifecycle()`.
- Provide the nav action via `CompositionLocalProvider(LocalXxxNavigationAction provides ...)`.
- Handle side effects in one place with `LifecycleAwareLaunchedEffect(viewModel.sideFlow())`
  → `when (it) { ... }`, calling `navigation.openXxx()` and the Activity callbacks.
- Use `ModalBottomSheetLayout` + `bottomSheet(route = ...)` for sheets and
  `composable(route = ...)` for full screens.
- Back press is owned by the ViewModel — the UI only dispatches it:
  ```kotlin
  @Composable
  fun HandleBackPress(viewModel: () -> XxxViewModel) {
      BackHandler { viewModel().handleXxxIntent(XxxIntent.HandleBackPress) }
  }
  ```

### UI (`ui/`)
- Composables are **stateless**: they receive `state` and lambdas
  (`onClickCta`, `setAge`, `onBackPress`, ...) — nothing else.
- They never reference the ViewModel directly; all events flow up via lambdas that the
  NavGraph maps to `handleXxxIntent(...)`.
- Identify screens/tabs with `@StringDef` annotations (e.g. `OnboardingScreen`,
  `FriendZoneOnboardingTab`) and put index↔tab mapping / counts in their `companion object`.

### Utils / Delegates (`utils/`)
- Reusable capabilities are `interface` + `@Inject` `Impl` (e.g. `VoiceRecorderDelegate`),
  exposing a `MutableSharedFlow<Status>` that the ViewModel collects and reduces.
- Inject `@ApplicationContext` and `DispatcherProvider`; run blocking work on
  `dispatcherProvider.IO` — never block the main thread.
- Pure helpers live in an `object` (e.g. `OnboardingUtils`) as extension functions.

### Constants & Analytics
- Every analytics page/action/event string, status string, and dimen lives in
  `XxxConstants` — **no magic strings** in ViewModel or UI.
- Wrap analytics in a single private `trackXxx(action, page)` over `AnalyticsManager`,
  called from intent handlers.

---

## 4. New feature checklist (in order)

1. `XxxState.kt` — root `@[Stable Keep]` state + nested `@Stable` states + sealed statuses, each with `getDefault()`.
2. `navigation/XxxIntent.kt` — sealed interface of user intents.
3. `navigation/XxxSideEffect.kt` — sealed class of one-shot effects.
4. `navigation/XxxRoutesPath.kt` → `XxxNavigationRoutes.kt` → `XxxNavigationAction.kt` (+ CompositionLocal).
5. `XxxViewModel.kt` — `BaseViewModel`, inject UseCases/Delegates, `handleXxxIntent`, `reduce`, `postSideEffect`, `handleError`.
6. `navigation/XxxNavGraph.kt` — NavHost, `collectAsStateWithLifecycle`, `LifecycleAwareLaunchedEffect(sideFlow())`, bottom sheets, `HandleBackPress`.
7. `ui/` — stateless composables + `@StringDef` screen/tab markers.
8. `XxxConstants.kt` — all strings/dimens.
9. `XxxActivity.kt` — `@AndroidEntryPoint`, intent factory, Compose host, result callbacks.
10. `utils/` — delegates (interface + `@Inject` impl) and pure util objects as needed.
11. all kinds of api calls will be done in the data module
12. all kinds of local data models will be created in the domain module

---

## 5. DON'Ts

- ❌ No business or back-navigation logic in composables — it lives in the ViewModel.
- ❌ No magic strings — put them in `XxxConstants` or a `@StringDef`.
- ❌ No state mutation outside `reduce { }`.
- ❌ No navigation outside `postSideEffect(...)` + `NavigationAction`.
- ❌ No direct ViewModel access from leaf composables — pass lambdas down.
- ❌ No blocking / IO work off `DispatcherProvider`.
- ❌ No `Intent` construction at call sites — use the Activity's `getActivityIntent(...)` factory.

---

