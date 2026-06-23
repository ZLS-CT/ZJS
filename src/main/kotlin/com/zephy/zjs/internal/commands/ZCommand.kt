package com.zephy.zjs.internal.commands

import com.zephy.zjs.ZJS
import com.zephy.zjs.api.Config
import com.zephy.zjs.api.client.Client
import com.zephy.zjs.api.client.FileLib
import com.zephy.zjs.api.message.ChatLib
import com.zephy.zjs.engine.Console
import com.zephy.zjs.engine.printTraceToConsole
import com.zephy.zjs.internal.commands.StaticCommand.Companion.onExecute
import com.zephy.zjs.internal.engine.module.ModulesGui
import com.zephy.zjs.internal.utils.Initializer
import com.mojang.brigadier.CommandDispatcher
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import java.io.IOException

//#if MC<=12111
//$$import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal
//#else
import net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal
//#endif

internal object ZCommand : Initializer {
    override fun init() {
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            register(dispatcher)
        }
    }

    fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        val command = literal("ct")
            .then(literal("load").onExecute { ZJS.reloadModules(asCommand = true) })
            .then(literal("reload").onExecute { ZJS.reloadModules(asCommand = true) })
            .then(literal("unload").onExecute { ZJS.unloadModules(asCommand = true) })
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
            FileLib.open(ZJS.MODULES_FOLDER)
        } catch (exception: IOException) {
            exception.printTraceToConsole()
            ChatLib.chat("&cCould not open file location")
        }
    }
}
