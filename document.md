# Notification Tap → Deeplink Trampoline Refactor

**Branch:** `feature/notification-impl`
**Date:** 2026-07-03

## Why

Our reminder notification previously launched the host activity directly via the package
launch intent (`PackageManager.getLaunchIntentForPackage`), with the "Reset" action riding
on a `SINGLE_TOP` intent. We refactored this to route notification taps, so the app has a single, stable notification entry point that is
independent of the host's launch mode or current task state — and a ready-made home for
external deeplinks later.

route every
notification tap through a **trampoline activity**:

1. `NotificationIntentFactory` (`feature/notification`) builds a `PendingIntent.getActivity`
   targeting **`DeeplinkHandlerActivity`**, with the notification id / referrer / action data
   as extras. The intent is obtained through a Hilt seam (`NotificationNavigationHelper`)
   because the notification module cannot see the home module's activity class.
2. `DeeplinkHandlerActivity` (manifest `singleTask`, no UI) retargets the incoming intent at
   `HomeActivity` with `FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK`, adds a
   `FROM_DEEPLINK_ACTIVITY` extra, starts it with zero animation, and calls
   `finishAndRemoveTask()`. This activity also owns their external deeplink intent-filters
   (AppsFlyer OneLink etc.).
3. `HomeActivity.handleIntent()` (from `onCreate`/`onNewIntent`) parses the extras and
   navigates to the destination screen **in-app**.

### Decision: no separate session activity

Because the reference mainline lands on the host activity and navigates internally —
exactly what our `ReminderActionStore` → `HomeViewModel` path already does — the reset
(meditation) session **stays a state step inside the Home feature**. 

## What changed

### New files

| File | Purpose |
| --- | --- |
| `app/src/main/java/com/reset/app/DeeplinkHandlerActivity.kt` | The trampoline. Retargets the incoming intent at `MainActivity` with `NEW_TASK \| CLEAR_TOP \| SINGLE_TOP` (a running host receives it via `onNewIntent` instead of being destroyed), preserves the action/extras, adds `EXTRA_FROM_DEEPLINK_ACTIVITY`, strips the action on recents relaunches, `overridePendingTransition(0, 0)`, `finish()`. Exposes `getDeeplinkHandlerActivityIntent(context)` in its companion, mirroring the reference. |
| `repository/src/main/java/com/reset/repository/notification/NotificationNavigationHelper.kt` | Interface seam: `:repository` builds the notification but cannot see `:app`'s activity classes. Mirrors Sharechat's `NotificationNavigationHelper`. |
| `app/src/main/java/com/reset/app/NotificationNavigationHelperImpl.kt` | App-side impl returning the trampoline intent + `@Binds` Hilt module (`NotificationNavigationModule`, `SingletonComponent`). |

### Modified files

| File | Change |
| --- | --- |
| `repository/.../notification/ReminderNotificationUtilImpl.kt` | Injects `NotificationNavigationHelper`. Both the notification **body tap** (`launchAppIntent()`) and the **"Reset" action** (`startResetIntent()`) now target the trampoline instead of the package launch intent. "Reset" still carries `ACTION_START_RESET`. Removed the `SINGLE_TOP` flags (the trampoline owns task setup) and corrected a stale comment — the API 31+ trampoline ban covers broadcast-receiver/service trampolines only; activity trampolines are allowed. |
| `repository/.../notification/NotificationConstants.kt` | Added `EXTRA_FROM_DEEPLINK_ACTIVITY` — set by the trampoline so the host can tell the launch came through it. |
| `app/src/main/AndroidManifest.xml` | Registered `DeeplinkHandlerActivity`: `singleTask`, `exported="false"`, app theme. When external deeplinks arrive, flip `exported` to `true` and add the intent-filters here. |

### Unchanged (by design)

- `MainActivity.handleReminderAction()` — still consumes `ACTION_START_RESET`, cancels the
  notification, and dispatches `ReminderAction.StartReset` into the buffered
  `ReminderActionStore` that `HomeViewModel` collects.
- `ReminderDismissReceiver` — the "Later" action stays a broadcast; it never opens the app.

## Flow after the refactor

```
Notification tap ("body" or "Reset")
  → PendingIntent → DeeplinkHandlerActivity          (no UI)
  → intent retargeted at MainActivity, NEW_TASK|CLEAR_TOP|SINGLE_TOP, extras preserved
  → MainActivity onCreate (cold) / onNewIntent (warm) → handleReminderAction(intent)
  → ReminderActionStore.dispatch(StartReset)          ("Reset" only)
  → HomeViewModel collects → popTo(Home) → reduces state → session screen
```

## Hardening fixes (applied after review)

1. **Session-safe flags** — the trampoline launches the host with
   `NEW_TASK | CLEAR_TOP | SINGLE_TOP` instead of the reference's `CLEAR_TASK`, so a tap
   while the app is open (e.g. mid-session) delivers via `onNewIntent` and never destroys
   the task. A warm "Reset" tap once again preserves the existing task.
2. **No stale re-dispatch** — three redundant guards: `MainActivity.onCreate` only handles
   the intent when `savedInstanceState == null` (rotation / process-death restore is not a
   tap); the action is nulled after dispatch so the retained intent can't replay it; and
   both the trampoline and the host skip intents flagged
   `FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY` (recents relaunch re-delivers the persisted
   original intent). `onNewIntent` also calls `setIntent()` so the stored intent stays
   current.
3. **Store rescoped to `ActivityRetainedComponent`** — `ReminderActionStore` models "a tap
   to act on right now", so it lives and dies with the activity instance (surviving
   rotation). A pending action can no longer leak into a later app session, and a dying
   activity's collector can never race a new activity's dispatch — each instance has its
   own store. Binding moved from `NotificationModule` (Singleton) to a new
   `ReminderActionModule` (`ActivityRetainedComponent`).
   **Follow-up:** the store was then converted from a buffered `Channel` to a pending-state
   `StateFlow<ReminderAction?>` with an explicit `consume(action)` — pull semantics, like an
   Activity reading its intent (the vibely model ported into our seam). The action stays
   readable until the handler acknowledges it after acting, so delivery doesn't depend on a
   live collector at dispatch time, collector churn can't lose an action mid-handling,
   multiple collectors are safe, and equal re-dispatches coalesce instead of queueing.
4. **Pop-to-Home before starting the session** — `startResetFromReminder` calls the new
   `navigator.popTo(AppDestination.Home)` (added to the `Navigator` seam as
   `NavEvent.PopTo`), so a session started while the user is on Settings is visible instead
   of running behind it. No-op when Home is already on top.
5. **Error escape** — `startResetFromReminder` now waits for the load to settle on
   `Content` **or** `Error` and bails on error, instead of hanging forever on
   `first { Content }` and silently swallowing the tap.

## Behavioral note

Tapping "Reset" while the app is foregrounded arrives via `onNewIntent` and preserves the
existing task and any in-progress session (which no-ops the request). This deliberately
diverges from Sharechat's `clearAndSetNewTask()` — clearing the task suits a content feed,
not an app whose main screen may be running a live meditation countdown.

## Notification variants (added after the hardening)

The reminder now has typed content variants, mirroring vibely's `NotificationType` catalog
at our scale. `ReminderNotificationType` (`:model`) — `ResetNudge` (the original),
`StreakGuard(streakDays)` ("Don't break your streak", eligible only while
`stats.streak > 0`), and `EyeFactNudge(fact)` ("Did you know?" + an eye-science fact).
The pure `ReminderTypeSelector` picks uniformly among the eligible variants at fire time
inside `ReminderNotificationWork`, reading streak/fact fresh each run; the seeded-`Random`
tests live in `:model`. All variants share the channel, the fixed notification id, and the
same Later / Reset actions, so the tap pipeline (trampoline → host → store → ViewModel) is
untouched. The fact texts are duplicated into `:repository` strings
(`reminder_eye_fact_texts`) because the originals live in `:feature:home` resources —
the array is indexed by `EyeFact.index` and must stay sized to `EyeFact.COUNT`.

## Verification

- `./gradlew :app:assembleDebug` — passes (validates the cross-module Hilt binding).
- `./gradlew :repository:testDebugUnitTest :feature:home:testDebugUnitTest` — passes.
