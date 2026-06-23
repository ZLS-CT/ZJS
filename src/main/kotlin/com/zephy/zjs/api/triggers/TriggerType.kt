package com.zephy.zjs.api.triggers

import com.zephy.zjs.internal.engine.JSLoader

sealed interface ITriggerType {
    val name: String

    fun triggerAll(vararg args: Any?) {
        JSLoader.exec(this, args)
    }
}

enum class TriggerType : ITriggerType {
    // client
    CHAT,
    TICK,
    STEP,
    GAME_LOAD,
    GAME_UNLOAD,
    ITEM_TOOLTIP,
    SERVER_CONNECT,
    SERVER_DISCONNECT,

    // gui
    GUI_OPENED,
    GUI_CLOSED,
    CLICKED,
    SCROLLED,
    DRAGGED,
    GUI_KEY,
    GUI_MOUSE_CLICK,
    GUI_MOUSE_DRAG,

    // rendering
    PRE_RENDER_WORLD,
    POST_RENDER_WORLD,
    PRE_RENDER_GUI,
    POST_RENDER_GUI,
    RENDER_BLOCK_HIGHLIGHT,
    RENDER_BLOCK_ENTITY,
    RENDER_ENTITY,
    RENDER_PLAYER_LIST,
    RENDER_HUD_OVERLAY,
    RENDER_SCREEN_OVERLAY,
    RENDER_HIDEABLE_SCREEN_OVERLAY,

    // world
    WORLD_LOAD,
    WORLD_UNLOAD,

    // misc
    COMMAND,
    OTHER,
	;
}

data class CustomTriggerType(override val name: String) : ITriggerType
