package com.zephy.zjs.engine

import com.zephy.zjs.internal.engine.JSContextFactory
import org.mozilla.javascript.Context

@Suppress("unused")
class WrappedThread(private val task: Runnable) {
    fun start() {
        val thread = Thread {
            try {
                JSContextFactory.enterContext()
                task.run()
            } catch (e: Throwable) {
                e.printTraceToConsole()
            } finally {
                // Always exit context, even if task throws
                Context.exit()
            }
        }
        thread.isDaemon = false
        thread.start()
    }

    // Provide the following methods as no-ops to avoid breaking
    // changes, as this class use to extend Thread
    fun run() { }

    fun stop() { }

    fun interrupt() { }

    fun isInterrupted() = false

    fun destroy() { }

    fun isAlive() = true

    fun suspend() { }

    fun resume() { }

    fun getId() = 0L

    companion object {
        @JvmStatic
        @JvmOverloads
        fun sleep(millis: Long, nanos: Int = 0) = Thread.sleep(millis, nanos)

        @JvmStatic
        fun currentThread(): Thread = Thread.currentThread()
    }
}
