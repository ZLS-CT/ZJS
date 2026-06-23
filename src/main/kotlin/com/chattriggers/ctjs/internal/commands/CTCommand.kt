package com.chattriggers.ctjs.internal.commands

import com.chattriggers.ctjs.CTJS
import com.chattriggers.ctjs.api.Config
import com.chattriggers.ctjs.api.client.Client
import com.chattriggers.ctjs.api.client.FileLib
import com.chattriggers.ctjs.api.message.ChatLib
import com.chattriggers.ctjs.engine.Console
import com.chattriggers.ctjs.engine.printTraceToConsole
import com.chattriggers.ctjs.internal.commands.StaticCommand.Companion.onExecute
import com.chattriggers.ctjs.internal.engine.module.ModulesGui
import com.chattriggers.ctjs.internal.utils.Initializer
import com.mojang.brigadier.CommandDispatcher
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import java.io.IOException

//#if MC<=12111
//$$import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal
//#else
import net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal
//#endif

internal object CTCommand : Initializer {
    override fun init() {
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            register(dispatcher)
        }
    }

    fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        val command = literal("ct")
            .then(literal("load").onExecute { CTJS.reloadModules(asCommand = true) })
            .then(literal("reload").onExecute { CTJS.reloadModules(asCommand = true) })
            .then(literal("unload").onExecute { CTJS.unloadModules(asCommand = true) })
            .then(literal("files").onExecute { openFileLocation() })
            .then(literal("modules").onExecute { Client.currentGui.set(ModulesGui) })
            .then(literal("console").onExecute { Console.show() })
            .then(literal("config").onExecute { Client.currentGui.set(Config.gui()!!) })
            .onExecute { ChatLib.chat(getUsage()) }

        dispatcher.register(command)
    }

    private fun getUsage() =
        """
        &b&m${ChatLib.getChatBreak()}
        &c/ct reload &7- &oReloads all modules.
        &c/ct unload &7- &oUnloads all modules.
        &c/ct files &7- &oOpens the ChatTriggers folder.
        &c/ct modules &7- &oOpens the modules GUI.
        &c/ct console [language] &7- &oOpens the ChatTriggers console.
        &c/ct config &7- &oOpens the ChatTriggers settings.
        &c/ct &7- &oDisplays this help dialog.
        &b&m${ChatLib.getChatBreak()}
        """.trimIndent()

    private fun openFileLocation() {
        try {
            FileLib.open(CTJS.MODULES_FOLDER)
        } catch (exception: IOException) {
            exception.printTraceToConsole()
            ChatLib.chat("&cCould not open file location")
        }
    }
}
