package com.olav.logolicious.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

object CoroutineHelper {
    private var activityScope: CoroutineScope? = null

    fun createActivityScope(): CoroutineScope {
        activityScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
        return activityScope!!
    }

    fun cancelActivityScope() {
        activityScope?.cancel()
        activityScope = null
    }
}