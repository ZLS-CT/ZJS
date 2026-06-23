package com.zephy.zjs.internal.launch

import com.zephy.zjs.engine.LogType
import com.zephy.zjs.engine.printToConsole
import com.zephy.zjs.engine.printTraceToConsole
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint

class ZJSPreLaunch : PreLaunchEntrypoint {
    override fun onPreLaunch() {
        val prevHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
            "Uncaught exception in thread \"${thread.name}\"".printToConsole(LogType.ERROR)
            exception.printTraceToConsole()
            prevHandler.uncaughtException(thread, exception)
        }

        try {
            DynamicMixinManager.applyMixins()
        } catch (e: Throwable) {
            IllegalStateException("Error generating dynamic mixins", e).printTraceToConsole()
        }
    }
}
