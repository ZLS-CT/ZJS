package com.zephy.zjs.internal.utils

import com.zephy.zjs.api.client.KeyBind
import com.zephy.zjs.api.commands.DynamicCommands
import com.zephy.zjs.internal.commands.ZCommand
import com.zephy.zjs.internal.commands.StaticCommand
import com.zephy.zjs.internal.console.ConsoleHostProcess
import com.zephy.zjs.internal.listeners.ClientListener
import com.zephy.zjs.internal.listeners.MouseListener

internal interface Initializer {
    fun init()

    companion object {
        internal val initializers = listOf(
            ClientListener,
            ConsoleHostProcess,
            ZCommand,
            DynamicCommands,
            KeyBind,
            MouseListener,
            StaticCommand,
        )
    }
}
