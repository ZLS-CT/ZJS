package com.chattriggers.ctjs.internal.listeners

import com.chattriggers.ctjs.api.client.Client
import com.chattriggers.ctjs.api.entity.CTBlockEntity
import com.chattriggers.ctjs.api.entity.CTEntity
import com.chattriggers.ctjs.api.inventory.CTItem
import com.chattriggers.ctjs.api.message.TextComponent
import com.chattriggers.ctjs.api.render.GUIRenderer
import com.chattriggers.ctjs.api.render.HudRenderLayer
import com.chattriggers.ctjs.api.triggers.CancellableEvent
import com.chattriggers.ctjs.api.triggers.ChatTrigger
import com.chattriggers.ctjs.api.triggers.TriggerType
import com.chattriggers.ctjs.api.world.Scoreboard
import com.chattriggers.ctjs.api.world.TabList
import com.chattriggers.ctjs.api.world.World
import com.chattriggers.ctjs.api.world.block.BlockFace
import com.chattriggers.ctjs.api.world.block.CTBlockPos
import com.chattriggers.ctjs.internal.engine.CTEvents
import com.chattriggers.ctjs.internal.engine.JSContextFactory
import com.chattriggers.ctjs.internal.engine.JSLoader
import com.chattriggers.ctjs.internal.utils.Initializer
import gg.essential.universal.UMatrixStack
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents
import net.fabricmc.fabric.api.event.player.AttackBlockCallback
import net.fabricmc.fabric.api.event.player.AttackEntityCallback
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.fabricmc.fabric.api.event.player.UseItemCallback
import net.minecraft.client.DeltaTracker
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionResult
import net.minecraft.resources.Identifier
import org.lwjgl.glfw.GLFW
import org.mozilla.javascript.Context

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry

//#if MC<=12111
//$$import net.minecraft.client.gui.GuiGraphics
//#else
import net.minecraft.client.gui.GuiGraphicsExtractor
//#endif

object ClientListener : Initializer {
    private var ticksPassed: Int = 0
    val chatHistory = mutableListOf<TextComponent>()
    val actionBarHistory = mutableListOf<TextComponent>()
    private val tasks = mutableListOf<Task>()
    private lateinit var packetContext: Context

    class Task(var delay: Int, val callback: () -> Unit)

    override fun init() {
        packetContext = JSContextFactory.enterContext()
        Context.exit()

        ClientReceiveMessageEvents.ALLOW_CHAT.register { message, _, _, _, _ ->
            handleChatMessage(message)
        }
        ClientReceiveMessageEvents.ALLOW_GAME.register { message, overlay ->
            handleChatMessage(message)
        }

        ClientTickEvents.START_CLIENT_TICK.register {
            synchronized(tasks) {
                tasks.removeAll {
                    if (it.delay-- <= 0) {
                        Client.getMinecraft().submit(it.callback)
                        true
                    } else false
                }
            }

            if (World.isLoaded() && World.toMC()?.tickRateManager()?.runsNormally() == true) {
                TriggerType.TICK.triggerAll(ticksPassed)
                ticksPassed++
            }
        }

        // Sleep layer isn't affected by screen hiding (F1)
        HudElementRegistry.attachElementAfter(
            HudRenderLayer.SLEEP.toMC(),
            Identifier.fromNamespaceAndPath("ctjs", "screen_overlay"))
        {
            //#if MC<=12111
            //$$drawContext: GuiGraphics,
            //#else
            drawContext: GuiGraphicsExtractor,
            //#endif
            tickCounter: DeltaTracker ->
            // Don't render if a screen is open, calls trigger twice otherwise
            if (Client.currentGui.get() != null) return@attachElementAfter

            val partialTicks = tickCounter.gameTimeDeltaTicks
            GUIRenderer.withMatrix(UMatrixStack(drawContext.pose()).toMC(), partialTicks) {
                TriggerType.RENDER_SCREEN_OVERLAY.triggerAll(drawContext, partialTicks)
            }
        }

        // Subtitles is last HUD layer to render
        HudElementRegistry.attachElementAfter(
            HudRenderLayer.SUBTITLES.toMC(),
            Identifier.fromNamespaceAndPath("ctjs", "hideable_screen_overlay"))
        {
            //#if MC<=12111
            //$$drawContext: GuiGraphics,
            //#else
            drawContext: GuiGraphicsExtractor,
            //#endif
            tickCounter: DeltaTracker ->
            // Don't render if a screen is open, calls trigger twice otherwise
            if (Client.currentGui.get() != null) return@attachElementAfter

            val partialTicks = tickCounter.gameTimeDeltaTicks
            GUIRenderer.withMatrix(UMatrixStack(drawContext.pose()).toMC(), partialTicks) {
                TriggerType.RENDER_HIDEABLE_SCREEN_OVERLAY.triggerAll(drawContext, partialTicks)
            }
        }

        ScreenEvents.BEFORE_INIT.register { _, screen, _, _ ->
            ScreenKeyboardEvents.allowKeyPress(screen).register { _, input ->
                val event = CancellableEvent()
                TriggerType.GUI_KEY.triggerAll(GLFW.glfwGetKeyName(input.key, input.scancode), input.key, screen, event)
                !event.isCancelled()
            }

            // Only ran while a screen is open (e.g. inventory, chat, etc.)
            //#if MC<=12111
            //$$ScreenEvents.beforeRender(screen).register { _, drawContext, mouseX, mouseY, partialTicks ->
            //#else
            ScreenEvents.beforeExtract(screen).register { _, drawContext, mouseX, mouseY, partialTicks ->
            //#endif
                GUIRenderer.withMatrix(UMatrixStack(drawContext.pose()).toMC(), partialTicks) {
                    TriggerType.PRE_RENDER_GUI.triggerAll(drawContext, mouseX, mouseY, screen, partialTicks)
                }
            }

            // Only ran while a screen is open (e.g. inventory, chat, etc.)
            //#if MC<=12111
            //$$ScreenEvents.afterRender(screen).register { _, drawContext, mouseX, mouseY, partialTicks ->
            //#else
            ScreenEvents.afterExtract(screen).register { _, drawContext, mouseX, mouseY, partialTicks ->
            //#endif
                GUIRenderer.withMatrix(UMatrixStack(drawContext.pose()).toMC(), partialTicks) {
                    TriggerType.POST_RENDER_GUI.triggerAll(drawContext, mouseX, mouseY, screen, partialTicks, )

                    TriggerType.RENDER_SCREEN_OVERLAY.triggerAll(drawContext, partialTicks)
                    TriggerType.RENDER_HIDEABLE_SCREEN_OVERLAY.triggerAll(drawContext, partialTicks)
                }
            }
        }

        ScreenEvents.AFTER_INIT.register { _, screen, _, _ ->
            ScreenEvents.remove(screen).register {
                TriggerType.GUI_CLOSED.triggerAll(screen)
            }
        }

        CTEvents.RENDER_TICK.register {
            TriggerType.STEP.triggerAll()
        }

        CTEvents.RENDER_HUD_OVERLAY.register { ctx, stack, partialTicks ->
            GUIRenderer.withMatrix(UMatrixStack(stack).toMC(), partialTicks) {
                TriggerType.RENDER_HUD_OVERLAY.triggerAll(ctx, partialTicks)
            }
        }

        CTEvents.RENDER_ENTITY.register { stack, entity, partialTicks, ci ->
            GUIRenderer.withMatrix(UMatrixStack(stack).toMC(), partialTicks) {
                TriggerType.RENDER_ENTITY.triggerAll(CTEntity.fromMC(entity), partialTicks, ci)
            }
        }

        CTEvents.RENDER_BLOCK_ENTITY.register { stack, blockEntity, partialTicks, ci ->
            GUIRenderer.withMatrix(UMatrixStack(stack).toMC(), partialTicks) {
                TriggerType.RENDER_BLOCK_ENTITY.triggerAll(CTBlockEntity(blockEntity), partialTicks, ci)
            }
        }
    }

    fun addTask(delay: Int, callback: () -> Unit) {
        synchronized(tasks) {
            tasks.add(Task(delay, callback))
        }
    }

    private fun handleChatMessage(message: Component): Boolean {
        val textComponent = TextComponent(message)
        val event = ChatTrigger.Event(textComponent)

        chatHistory += textComponent
        if (chatHistory.size > 1000) {
            chatHistory.removeAt(0)
        }

        TriggerType.CHAT.triggerAll(event)

        return !event.isCancelled()
    }
}
