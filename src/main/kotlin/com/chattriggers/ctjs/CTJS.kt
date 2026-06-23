package com.chattriggers.ctjs

import com.chattriggers.ctjs.api.Config
import com.chattriggers.ctjs.api.client.Client
import com.chattriggers.ctjs.api.client.KeyBind
import com.chattriggers.ctjs.api.client.CTSound
import com.chattriggers.ctjs.api.commands.DynamicCommands
import com.chattriggers.ctjs.api.message.ChatLib
import com.chattriggers.ctjs.api.render.Image
import com.chattriggers.ctjs.api.triggers.TriggerType
import com.chattriggers.ctjs.api.world.World
import com.chattriggers.ctjs.engine.Console
import com.chattriggers.ctjs.engine.Register
import com.chattriggers.ctjs.internal.commands.StaticCommand
import com.chattriggers.ctjs.internal.engine.module.ModuleManager
import com.chattriggers.ctjs.internal.utils.Initializer
import kotlinx.serialization.json.Json
import net.fabricmc.api.ClientModInitializer
import java.io.File
import java.net.URI
import java.net.URLConnection
import kotlin.concurrent.thread

class CTJS : ClientModInitializer {
    override fun onInitializeClient() {
        Client.referenceSystemTime = System.nanoTime()
        Initializer.initializers.forEach(Initializer::init)

        Runtime.getRuntime().addShutdownHook(Thread {
            TriggerType.GAME_UNLOAD.triggerAll()
            Console.close()
        })
    }

    companion object {
        const val MOD_ID = "ctjs"
        const val MOD_NAME = "ChatTriggers"
        const val MOD_VERSION = "3.0.6b"

        const val CONFIG_FOLDER_PATH = "./config"
        const val MOD_FOLDER_PATH = "${CONFIG_FOLDER_PATH}/${MOD_NAME}"
        const val MODULES_FOLDER_PATH = "${MOD_FOLDER_PATH}/modules"
        const val ASSETS_FOLDER_PATH = "${MOD_FOLDER_PATH}/assets"
        val CONFIG_FOLDER = File(CONFIG_FOLDER_PATH)
        val MOD_FOLDER = File(MOD_FOLDER_PATH)
        val MODULES_FOLDER = File(MODULES_FOLDER_PATH).apply {
            mkdirs()
        }
        val ASSETS_FOLDER = File(ASSETS_FOLDER_PATH).apply {
            mkdirs()
        }

        @JvmStatic
        var isLoaded = true
            private set

        internal val images = mutableListOf<Image>()
        internal val sounds = mutableListOf<CTSound>()

        internal val json = Json {
            useAlternativeNames = true
            ignoreUnknownKeys = true
        }

        @JvmOverloads
        internal fun makeWebRequest(url: String, userAgent: String? = "Mozilla/5.0 (ChatTriggers)"): URLConnection =
            URI(url).toURL().openConnection().apply {
                setRequestProperty("User-Agent", userAgent)
                connectTimeout = 3000
                readTimeout = 3000
            }

        @JvmStatic
        fun unloadModules(asCommand: Boolean = true) {
            TriggerType.WORLD_UNLOAD.triggerAll()
            TriggerType.GAME_UNLOAD.triggerAll()

            isLoaded = false

            ModuleManager.teardown()
            KeyBind.clearKeyBinds()
            Register.clearCustomTriggers()
            StaticCommand.unregisterAll()
            DynamicCommands.unregisterAll()

            if (Config.clearConsoleOnLoad) {
                Console.clear()
            }

            Client.scheduleTask {
                images.forEach(Image::destroy)
                sounds.forEach(CTSound::destroy)

                images.clear()
                sounds.clear()
            }

            if (asCommand) {
                ChatLib.chat("&7Unloaded ChatTriggers")
            }
        }

        @JvmStatic
        fun reloadModules(asCommand: Boolean = true) {
            Client.getMinecraft().options.save()
            unloadModules(asCommand = false)

            if (asCommand) {
                ChatLib.chat("&cReloading ChatTriggers...")
            }

            thread {
                ModuleManager.setup()
                Client.getMinecraft().options.load()

                // Need to set isLoaded to true before running modules, otherwise custom triggers
                // activated at the top level will not work
                isLoaded = true

                ModuleManager.entryPass()

                if (asCommand) {
                    ChatLib.chat("&aDone reloading!")
                }

                TriggerType.GAME_LOAD.triggerAll()
                if (World.isLoaded()) {
                    TriggerType.WORLD_LOAD.triggerAll()
                }
            }
        }
    }
}
