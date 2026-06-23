package com.zephy.zjs

import com.zephy.zjs.api.Config
import com.zephy.zjs.api.client.Client
import com.zephy.zjs.api.client.KeyBind
import com.zephy.zjs.api.client.ZSound
import com.zephy.zjs.api.commands.DynamicCommands
import com.zephy.zjs.api.message.ChatLib
import com.zephy.zjs.api.render.Image
import com.zephy.zjs.api.triggers.TriggerType
import com.zephy.zjs.api.world.World
import com.zephy.zjs.engine.Console
import com.zephy.zjs.engine.Register
import com.zephy.zjs.internal.commands.StaticCommand
import com.zephy.zjs.internal.engine.module.ModuleManager
import com.zephy.zjs.internal.utils.Initializer
import kotlinx.serialization.json.Json
import net.fabricmc.api.ClientModInitializer
import java.io.File
import java.net.URI
import java.net.URLConnection
import kotlin.concurrent.thread

class ZJS : ClientModInitializer {
    override fun onInitializeClient() {
        Client.referenceSystemTime = System.nanoTime()
        Initializer.initializers.forEach(Initializer::init)

        Runtime.getRuntime().addShutdownHook(Thread {
            TriggerType.GAME_UNLOAD.triggerAll()
            Console.close()
        })
    }

    companion object {
        const val MOD_ID = "zjs"
        const val MOD_NAME = "ZJS"
        const val COMMAND_PREFIX = MOD_ID
        const val MOD_VERSION = "1.0.0"
        const val USER_AGENT = "Mozilla/5.0 (${MOD_NAME})"

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
        internal val sounds = mutableListOf<ZSound>()

        internal val json = Json {
            useAlternativeNames = true
            ignoreUnknownKeys = true
        }

        @JvmOverloads
        internal fun makeWebRequest(url: String, userAgent: String? = USER_AGENT): URLConnection =
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
                sounds.forEach(ZSound::destroy)

                images.clear()
                sounds.clear()
            }

            if (asCommand) {
                ChatLib.chat("&7Unloaded ${MOD_NAME}")
            }
        }

        @JvmStatic
        fun reloadModules(asCommand: Boolean = true) {
            Client.getMinecraft().options.save()
            unloadModules(asCommand = false)

            if (asCommand) {
                ChatLib.chat("&cReloading ${MOD_NAME}...")
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
