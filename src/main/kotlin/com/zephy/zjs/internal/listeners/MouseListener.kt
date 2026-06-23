package com.zephy.zjs.internal.listeners

import com.zephy.zjs.api.client.Client
import com.zephy.zjs.api.triggers.CancellableEvent
import com.zephy.zjs.api.triggers.TriggerType
import com.zephy.zjs.api.world.World
import com.zephy.zjs.internal.engine.ZEvents
import com.zephy.zjs.internal.utils.Initializer
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents
import org.lwjgl.glfw.GLFW

internal object MouseListener : Initializer {
    private val mouseState = mutableMapOf<Int, Int>()
    private val draggedState = mutableMapOf<Int, State>()

    private class State(val x: Double, val y: Double)

    override fun init() {
        ZEvents.RENDER_TICK.register {
            if (!World.isLoaded()) return@register

            for (button in 0..4) {
                if (button !in draggedState) continue

                val x = Client.getMouseX()
                val y = Client.getMouseY()

                if (x == draggedState[button]?.x && y == draggedState[button]?.y) continue

                ZEvents.MOUSE_DRAGGED.invoker().process(
                    x - (draggedState[button]?.x ?: 0.0),
                    y - (draggedState[button]?.y ?: 0.0),
                    x,
                    y,
                    button,
                )

                // update dragged
                draggedState[button] = State(x, y)
            }
        }

        ZEvents.MOUSE_CLICKED.register(TriggerType.CLICKED::triggerAll)
        ZEvents.MOUSE_SCROLLED.register(TriggerType.SCROLLED::triggerAll)
        ZEvents.MOUSE_DRAGGED.register(TriggerType.DRAGGED::triggerAll)
        ZEvents.GUI_MOUSE_DRAG.register(TriggerType.GUI_MOUSE_DRAG::triggerAll)

        ScreenEvents.BEFORE_INIT.register { _, screen, _, _ ->
            ScreenMouseEvents.allowMouseClick(screen).register { _, click ->
                val event = CancellableEvent()
                TriggerType.GUI_MOUSE_CLICK.triggerAll(click.x, click.y, click.button(), true, screen, event)

                !event.isCanceled()
            }

            ScreenMouseEvents.allowMouseRelease(screen).register { _, click ->
                val event = CancellableEvent()
                TriggerType.GUI_MOUSE_CLICK.triggerAll(click.x, click.y, click.button(), false, screen, event)

                !event.isCanceled()
            }
        }
    }

    @JvmStatic
    fun onRawMouseInput(button: Int, action: Int) {
        if (!World.isLoaded()) {
            mouseState.clear()
            draggedState.clear()
            return
        }

        if (button == -1 || action == mouseState[button]) return

        val x = Client.getMouseX()
        val y = Client.getMouseY()

        ZEvents.MOUSE_CLICKED.invoker().process(x, y, button, action == GLFW.GLFW_PRESS)
        mouseState[button] = action

        if (action == GLFW.GLFW_PRESS) {
            draggedState[button] = State(x, y)
        } else {
            draggedState.remove(button)
        }
    }

    @JvmStatic
    fun onRawMouseScroll(dy: Double) {
        ZEvents.MOUSE_SCROLLED.invoker().process(Client.getMouseX(), Client.getMouseY(), dy)
    }
}
