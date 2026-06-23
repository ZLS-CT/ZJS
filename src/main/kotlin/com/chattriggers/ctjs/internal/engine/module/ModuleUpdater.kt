package com.chattriggers.ctjs.internal.engine.module

import com.chattriggers.ctjs.api.message.ChatLib
import com.chattriggers.ctjs.internal.engine.CTEvents
import com.chattriggers.ctjs.internal.engine.module.ModuleManager.cachedModules
import com.chattriggers.ctjs.internal.utils.Initializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents

object ModuleUpdater : Initializer {
    private val changelogs = mutableListOf<ModuleMetadata>()
    private var shouldReportChangelog = false

    override fun init() {
        ClientPlayConnectionEvents.JOIN.register { _, _, _ -> shouldReportChangelog = true }

        CTEvents.RENDER_HUD_OVERLAY.register { _, _, _ ->
            if (shouldReportChangelog) {
                changelogs.forEach(::reportChangelog)
                changelogs.clear()
            }
        }
    }

    private fun reportChangelog(module: ModuleMetadata) {
        ChatLib.chat("&a[ChatTriggers] ${module.name} has updated to version ${module.version}")
        ChatLib.chat("&aChangelog: &r${module.changelog}")
    }

    fun importModule(moduleName: String, requiredBy: String? = null): List<Module> {
        val alreadyImported = cachedModules.any {
            if (it.name.equals(moduleName, ignoreCase = true)) {
                if (requiredBy != null) {
                    it.metadata.isRequired = true
                    it.requiredBy.add(requiredBy)
                }

                true
            } else {
                false
            }
        }
        return emptyList()
    }

    data class DownloadResult(val name: String, val modVersion: String)
}
