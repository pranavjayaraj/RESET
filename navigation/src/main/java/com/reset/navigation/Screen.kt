package com.reset.navigation

/**
 * A navigable destination. Implementations must be `@Serializable` — the type itself is the
 * route (Navigation 2.8 type-safe DSL), so there are no string routes to keep in sync.
 *
 * Destinations live in each feature's `api` submodule (e.g. `:feature:home:api` declares
 * `HomeDestination`), not here: a feature that navigates to another depends only on that
 * feature's tiny `api` module, so implementation churn never recompiles callers and no
 * single shared catalog file fans out to every feature. This module owns only the seam
 * ([Navigator], [NavEvent], this marker) and the host-side [ObserveNavigation].
 */
interface Screen
