package com.reset.app

import android.content.Context
import android.content.Intent
import com.reset.repository.notification.NotificationNavigationHelper
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

/** App-side impl of the seam: only this module knows [DeeplinkHandlerActivity]. */
class NotificationNavigationHelperImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : NotificationNavigationHelper {

    override fun getDeeplinkHandlerActivityIntent(): Intent =
        DeeplinkHandlerActivity.getDeeplinkHandlerActivityIntent(context)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationNavigationModule {

    @Binds
    @Singleton
    abstract fun bindNotificationNavigationHelper(
        impl: NotificationNavigationHelperImpl,
    ): NotificationNavigationHelper
}
