package com.chattriggers.ctjs.internal.utils

//#if MC>=26.1
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.TextColor

object TextColorUtils {
    private val colorToFormatting: Map<TextColor, ChatFormatting> = buildMap {
        for (format in ChatFormatting.entries) {
            val color = TextColor.fromLegacyFormat(format) ?: continue
            put(color, format)
        }
    }

    fun toChatFormatting(color: TextColor): ChatFormatting? = colorToFormatting[color]
}
//#endif
