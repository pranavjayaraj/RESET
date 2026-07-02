package com.reset.repository.notification

import com.reset.model.domain.ReminderActionStore
import com.reset.model.domain.ReminderScheduler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
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

    @Binds
    @Singleton
    abstract fun bindReminderActionStore(impl: ReminderActionStoreImpl): ReminderActionStore
}
