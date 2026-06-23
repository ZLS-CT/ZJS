package com.zephy.zjs.api.entity

import com.zephy.zjs.api.ZWrapper
import com.zephy.zjs.api.message.TextComponent
import com.zephy.zjs.internal.utils.toChatFormatting
import com.zephy.zjs.internal.utils.toLegacyFormatting
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.TextColor
import net.minecraft.world.scores.PlayerTeam
import kotlin.jvm.optionals.getOrElse

//#if MC>=26.2
import net.minecraft.world.scores.TeamColor
//#endif

class ZTeam(override val mcValue: PlayerTeam) : ZWrapper<PlayerTeam> {
    /**
     * Gets the registered name of the team
     */
    fun getRegisteredName(): String = mcValue.name

    /**
     * Gets the display name of the team
     */
    fun getName() = TextComponent(mcValue.displayName).formattedText

    /**
     * Gets the list of names on the team
     */
    fun getMembers(): List<String> = mcValue.players.toList()

    /**
     * Gets the team prefix
     */
    fun getPrefix() = TextComponent(mcValue.playerPrefix).formattedText

    /**
     * Gets the team suffix
     */
    fun getSuffix() = TextComponent(mcValue.playerSuffix).formattedText

    //#if MC<26.2
    //$$fun getLegacyColor(): String = mcValue.color.toString()
    //$$fun getChatFormatting(): String = getLegacyColor()
    //#else
    fun getTeamColor(): TeamColor = mcValue.color.getOrElse { TeamColor.WHITE }
    fun getTextColor(): TextColor = getTeamColor().textColor()
    fun getLegacyColor(): String = getTeamColor().toLegacyFormatting()
    fun getChatFormatting(): ChatFormatting = getTeamColor().toChatFormatting()
    //#endif
}
