package com.zephy.zjs.engine

import com.zephy.zjs.api.triggers.ChatTrigger
import com.zephy.zjs.api.triggers.ClassFilterTrigger
import com.zephy.zjs.api.triggers.CommandTrigger
import com.zephy.zjs.api.triggers.CustomTriggerType
import com.zephy.zjs.api.triggers.EventTrigger
import com.zephy.zjs.api.triggers.RegularTrigger
import com.zephy.zjs.api.triggers.RenderBlockEntityTrigger
import com.zephy.zjs.api.triggers.RenderEntityTrigger
import com.zephy.zjs.api.triggers.StepTrigger
import com.zephy.zjs.api.triggers.Trigger
import com.zephy.zjs.api.triggers.TriggerType

@Suppress("unused", "MemberVisibilityCanBePrivate")
object Register {
    private val methodMap = Register::class.java.methods.filter {
        it.name.startsWith("register") && it.name.length > "register".length
    }.associateBy {
        it.name.lowercase().drop("register".length)
    }
    private val customTriggers = mutableSetOf<CustomTriggerType>()

    internal fun clearCustomTriggers() = customTriggers.clear()

    /**
     * Helper method register a trigger.
     *
     * Called by taking the original name of the method, i.e. `registerChat`,
     * removing the word register, and comparing it case-insensitively with
     * the methods below.
     *
     * @param triggerType the type of trigger
     * @param method The name of the method or the actual method to callback when the event is fired
     * @return The trigger for additional modification
     */
    @JvmStatic
    fun register(triggerType: String, method: Any): Trigger {
        val type = triggerType.lowercase()

        methodMap[type]?.let { return it.invoke(this, method) as Trigger }

        val customType = CustomTriggerType(type)
        if (customType in customTriggers) return RegularTrigger(method, customType)

        throw NoSuchMethodException("No trigger type named '$triggerType'")
    }

    @JvmStatic
    fun createCustomTrigger(name: String): Any {
        val customType = CustomTriggerType(name.lowercase())
        require(customType !in customTriggers) { "Cannot register duplicate custom trigger \"$name\"" }
        customTriggers.add(customType)

        return object {
            fun trigger(vararg args: Any?) = customType.triggerAll(*args)
        }
    }

    /**
     * Registers a new trigger that runs before a chat message is received.
     *
     * Passes through multiple arguments:
     * - Any number of chat criteria variables
     * - The chat event, which can be cancelled
     *
     * Available modifications:
     * - [ChatTrigger.triggerIfCanceled] Sets if triggered if event is already cancelled
     * - [ChatTrigger.setChatCriteria] Sets the chat criteria
     * - [ChatTrigger.setParameter] Sets the chat parameter
     * - [Trigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    @JvmStatic
    fun registerChat(method: Any): Trigger = ChatTrigger(method, TriggerType.CHAT)

    /**
     * Registers a new trigger that runs before every game tick.
     *
     * Passes through one argument:
     * - Ticks elapsed
     *
     * Available modifications:
     * - [Trigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    @JvmStatic
    fun registerTick(method: Any): Trigger = RegularTrigger(method, TriggerType.TICK)

    /**
     * Registers a new trigger that runs in predictable intervals. (60 per second by default)
     *
     * Passes through one argument:
     * - Steps elapsed
     *
     * Available modifications:
     * - [StepTrigger.setFps] Sets the fps, i.e. how many times this trigger will fire
     *      per second
     * - [StepTrigger.setDelay] Sets the delay in seconds, i.e. how many seconds it takes
     *      to fire. Overrides [StepTrigger.setFps].
     * - [Trigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    @JvmStatic
    fun registerStep(method: Any): Trigger = StepTrigger(method)

    /**
     * Registers a new trigger that runs after the game loads.
     *
     * This runs after the initial loading of the game directly after scripts are
     * loaded and after "/ct load" happens.
     *
     * Available modifications:
     * - [Trigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    @JvmStatic
    fun registerGameLoad(method: Any): Trigger = RegularTrigger(method, TriggerType.GAME_LOAD)

    /**
     * Registers a new trigger that runs before the game unloads.
     *
     * This runs before shutdown of the JVM and before "/ct load" happens.
     *
     * Available modifications:
     * - [Trigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    @JvmStatic
    fun registerGameUnload(method: Any): Trigger = RegularTrigger(method, TriggerType.GAME_UNLOAD)

    /**
     * Registers a new trigger that runs when a tooltip is being rendered.
     * This allows for the user to modify what text is in the tooltip, and even the
     * ability to cancel rendering completely. Note that you must call
     * [com.zephy.zjs.api.inventory.ZItem.setLore] with the modified lore for
     * the changes to take effect.
     *
     * Passes through three arguments:
     * - A list of [com.zephy.zjs.api.message.TextComponent] objects to modify.
     * - The [com.zephy.zjs.api.inventory.ZItem] that this lore is attached to.
     * - The cancellable event.
     *
     * Available modifications:
     * - [Trigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    @JvmStatic
    fun registerItemTooltip(method: Any): Trigger = EventTrigger(method, TriggerType.ITEM_TOOLTIP)

    /**
     * Registers a new trigger that runs whenever the player connects to a server
     *
     * Available modifications:
     * - [Trigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    @JvmStatic
    fun registerServerConnect(method: Any): Trigger = RegularTrigger(method, TriggerType.SERVER_CONNECT)

    /**
     * Registers a new trigger that runs whenever the player disconnects from a server
     *
     * Available modifications:
     * - [Trigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    @JvmStatic
    fun registerServerDisconnect(method: Any): Trigger = RegularTrigger(method, TriggerType.SERVER_DISCONNECT)

    /**
     * Registers a new trigger that runs when a new gui is first opened.
     *
     * Passes through one argument:
     * - The [net.minecraft.client.gui.screen.Screen] that was opened
     * - The gui opened event, which can be cancelled
     *
     * Available modifications:
     * - [Trigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    @JvmStatic
    fun registerGuiOpened(method: Any): Trigger = EventTrigger(method, TriggerType.GUI_OPENED)

    /**
     * Registers a new trigger that runs when a gui is closed.
     *
     * Passes through one argument:
     * - The gui that was closed
     *
     * Available modifications:
     * - [Trigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    @JvmStatic
    fun registerGuiClosed(method: Any): Trigger = RegularTrigger(method, TriggerType.GUI_CLOSED)

    /**
     * Registers a new trigger that runs before a mouse button is being pressed or released.
     *
     * Passes through four arguments:
     * - The mouse x position
     * - The mouse y position
     * - The mouse button
     * - The mouse button state (true if button is pressed, false otherwise)
     *
     * Available modifications:
     * - [Trigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    @JvmStatic
    fun registerClicked(method: Any): Trigger = RegularTrigger(method, TriggerType.CLICKED)

    /**
     * Registers a new trigger that runs before the mouse is scrolled.
     *
     * Passes through three arguments:
     * - The mouse x position
     * - The mouse y position
     * - The scroll amount
     *
     * Available modifications:
     * - [Trigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    @JvmStatic
    fun registerScrolled(method: Any): Trigger = RegularTrigger(method, TriggerType.SCROLLED)

    /**
     * Registers a new trigger that runs while a mouse button is being held down.
     *
     * Passes through five arguments:
     * - The mouse delta x position (relative to last frame)
     * - The mouse delta y position (relative to last frame)
     * - The mouse x position
     * - The mouse y position
     * - The mouse button
     *
     * Available modifications:
     * - [Trigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    @JvmStatic
    fun registerDragged(method: Any): Trigger = RegularTrigger(method, TriggerType.DRAGGED)

    /**
     * Registers a new trigger that runs whenever a key is typed with a gui open
     *
     * Passes through four arguments:
     * - The character pressed (e.g. 'd')
     * - The key code pressed (e.g. 41)
     * - The gui
     * - The event, which can be cancelled
     *
     * Available modifications:
     * - [Trigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    @JvmStatic
    fun registerGuiKey(method: Any): Trigger = EventTrigger(method, TriggerType.GUI_KEY)

    /**
     * Registers a new trigger that runs whenever the mouse is clicked with a
     * gui open
     *
     * Passes through five arguments:
     * - The mouse x position
     * - The mouse y position
     * - The mouse button
     * - The mouse button state (true if button is pressed, false otherwise)
     * - The gui
     * - The event, which can be cancelled
     *
     * Available modifications:
     * - [Trigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    @JvmStatic
    fun registerGuiMouseClick(method: Any): Trigger = EventTrigger(method, TriggerType.GUI_MOUSE_CLICK)

    /**
     * Registers a new trigger that runs whenever a mouse button held and dragged
     * with a gui open
     *
     * Passes through seven arguments:
     * - The mouse delta x position (relative to last frame)
     * - The mouse delta y position (relative to last frame)
     * - The mouse x position
     * - The mouse y position
     * - The mouse button
     * - The gui
     * - The event, which can be cancelled
     *
     * Available modifications:
     * - [Trigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    @JvmStatic
    fun registerGuiMouseDrag(method: Any): Trigger = EventTrigger(method, TriggerType.GUI_MOUSE_DRAG)

    @Deprecated("Use postRenderWorld", ReplaceWith("registerPostRenderWorld"))
    @JvmStatic
    fun registerRenderWorld(method: Any) = registerPostRenderWorld(method)

    // I'm not sure if this works anymore, maybe replace with postRender?
    /**
     * Registers a new trigger that runs before the world is drawn.
     *
     * Passes through one argument:
     * - Partial ticks elapsed
     *
     * Available modifications:
     * - [Trigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    @JvmStatic
    fun registerPreRenderWorld(method: Any): Trigger = RegularTrigger(method, TriggerType.PRE_RENDER_WORLD)

    /**
     * Registers a new trigger that runs after the world is drawn.
     *
     * Passes through one argument:
     * - Partial ticks elapsed
     *
     * Available modifications:
     * - [Trigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    @JvmStatic
    fun registerPostRenderWorld(method: Any): Trigger = RegularTrigger(method, TriggerType.POST_RENDER_WORLD)

    /**
     * Registers a new trigger that runs as a gui is rendered
     *
     * Passes through five arguments:
     * - The mouseX
     * - The mouseY
     * - The GuiScreen
     * - The partial ticks
     * - The [net.minecraft.client.gui.DrawContext]
     *
     * Available modifications:
     * - [Trigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    @JvmStatic
    fun registerPreRenderGui(method: Any): Trigger = RegularTrigger(method, TriggerType.PRE_RENDER_GUI)

    /**
     * Registers a new trigger that runs after the current screen is rendered
     *
     * Passes through five arguments:
     * - The mouseX
     * - The mouseY
     * - The GuiScreen
     * - The partial ticks
     * - The [net.minecraft.client.gui.DrawContext]
     *
     * Available modifications:
     * - [Trigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    @JvmStatic
    fun registerPostRenderGui(method: Any): Trigger = RegularTrigger(method, TriggerType.POST_RENDER_GUI)

    /**
     * Registers a new trigger that runs before the block highlight box is drawn.
     *
     * Passes through two arguments:
     * - The draw block highlight event's position
     * - The draw block highlight event, which can be cancelled
     *
     * Available modifications:
     * - [Trigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    @JvmStatic
    fun registerDrawBlockHighlight(method: Any): Trigger = EventTrigger(method, TriggerType.RENDER_BLOCK_HIGHLIGHT)

    /**
     * Registers a new trigger that runs whenever a block entity is rendered
     *
     * Passes through three arguments:
     * - The [com.zephy.zjs.api.entity.ZBlockEntity]
     * - The partial ticks
     * - The event, which can be cancelled
     *
     * Available modifications:
     * - [Trigger.setPriority] Sets the priority
     * - [ClassFilterTrigger.setFilteredClasses] Sets the tile entity classes which this trigger
     *   gets fired for
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    @JvmStatic
    fun registerRenderBlockEntity(method: Any): Trigger = RenderBlockEntityTrigger(method)

    /**
     * Registers a new trigger that runs whenever an entity is rendered
     *
     * Passes through three arguments:
     * - The [com.zephy.zjs.api.entity.ZEntity]
     * - The partial ticks
     * - The event, which can be cancelled
     *
     * Available modifications:
     * - [Trigger.setPriority] Sets the priority
     * - [ClassFilterTrigger.setFilteredClasses] Sets the entity classes which this trigger
     *   gets fired for
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    @JvmStatic
    fun registerRenderEntity(method: Any): Trigger = RenderEntityTrigger(method)

    /**
     * Registers a new trigger that runs before the player list is being drawn.
     *
     * Passes through one argument:
     * - The render event, which can be cancelled
     *
     * Available modifications:
     * - [EventTrigger.triggerIfCanceled] Sets if triggered if event is already cancelled
     * - [Trigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    @JvmStatic
    fun registerRenderPlayerList(method: Any): Trigger = EventTrigger(method, TriggerType.RENDER_PLAYER_LIST)

    /**
     * Registers a new trigger that runs before the hud overlay is drawn.
     *
     * Available modifications:
     * - [Trigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    @JvmStatic
    fun registerRenderHudOverlay(method: Any): Trigger = RegularTrigger(method, TriggerType.RENDER_HUD_OVERLAY)

    /**
     * Registers a new trigger that runs after the screen is drawn.
     * Not hidden when pressing F1
     *
     * Passes through one argument:
     * - The [net.minecraft.client.gui.DrawContext]
     * - Partial ticks elapsed
     *
     * Available modifications:
     * - [Trigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    @JvmStatic
    fun registerRenderScreenOverlay(method: Any): Trigger = RegularTrigger(method, TriggerType.RENDER_SCREEN_OVERLAY)

    /**
     * Registers a new trigger that runs after the screen is drawn.
     * This gets hidden when pressing F1
     *
     * Passes through one argument:
     * - The [net.minecraft.client.gui.DrawContext]
     * - Partial ticks elapsed
     *
     * Available modifications:
     * - [Trigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    @JvmStatic
    fun registerRenderHideableScreenOverlay(method: Any): Trigger = RegularTrigger(method, TriggerType.RENDER_HIDEABLE_SCREEN_OVERLAY)

    /**
     * Registers a trigger that runs before the world loads.
     *
     * Available modifications:
     * - [Trigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    @JvmStatic
    fun registerWorldLoad(method: Any): Trigger = RegularTrigger(method, TriggerType.WORLD_LOAD)

    /**
     * Registers a new trigger that runs before the world unloads.
     *
     * Available modifications:
     * - [Trigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    @JvmStatic
    fun registerWorldUnload(method: Any): Trigger = RegularTrigger(method, TriggerType.WORLD_UNLOAD)

    /**
     * Registers a new command that will run the method provided.
     *
     * Passes through multiple arguments:
     * - The arguments supplied to the command by the user
     *
     * Available modifications:
     * - [CommandTrigger.setCommandName] Sets the command name
     * - [Trigger.setPriority] Sets the priority
     *
     * @param method The method to call when the event is fired
     * @return The trigger for additional modification
     */
    @JvmStatic
    fun registerCommand(method: Any): Trigger = CommandTrigger(method)
}
