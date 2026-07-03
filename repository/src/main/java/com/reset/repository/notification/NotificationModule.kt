package com.reset.repository.notification

import com.reset.model.domain.ReminderActionStore
import com.reset.model.domain.ReminderScheduler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationModule {

    @Binds
    @Singleton
    abstract fun bindReminderScheduler(impl: ReminderSchedulerImpl): ReminderScheduler

    @Binds
    @Singleton
    abstract fun bindReminderNotificationUtil(impl: ReminderNotificationUtilImpl): ReminderNotificationUtil
}

/**
 * The action store is deliberately narrower than the rest: it models "a tap to act on
 * right now", so its lifetime is the activity session, not the process (see
 * [ReminderActionStoreImpl]).
 */
@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class ReminderActionModule {

    @Binds
    @ActivityRetainedScoped
    abstract fun bindReminderActionStore(impl: ReminderActionStoreImpl): ReminderActionStore
}
