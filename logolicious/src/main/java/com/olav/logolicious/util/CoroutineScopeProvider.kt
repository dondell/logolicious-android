package com.olav.logolicious.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

object CoroutineScopeProvider {
    val scope: CoroutineScope by lazy {
        val supervisorJob = SupervisorJob()
        CoroutineScope(supervisorJob + Dispatchers.Main)
    }
}