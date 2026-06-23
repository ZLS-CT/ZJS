package com.chattriggers.ctjs.api.entity

import com.chattriggers.ctjs.api.client.Client
import com.chattriggers.ctjs.api.message.TextComponent
import net.minecraft.client.multiplayer.PlayerInfo
import net.minecraft.world.entity.player.Player
import net.minecraft.network.chat.Component
import net.minecraft.world.scores.PlayerTeam

class PlayerMP(override val mcValue: Player) : CTEntity(mcValue) {
    fun getPing(): Int {
        return getPlayerInfo()?.latency ?: -1
    }

    fun getTeam(): CTTeam? {
        return getPlayerInfo()?.team?.let(::CTTeam)
    }

    /**
     * Gets the display name for this player,
     * i.e. the name shown in tab list and in the player's nametag.
     * @return the display name
     */
    fun getDisplayName() = getPlayerName(getPlayerInfo())

    private fun getPlayerName(playerListEntry: PlayerInfo?): TextComponent {
        return playerListEntry?.tabListDisplayName?.let { TextComponent(it) }
            ?: TextComponent(
                PlayerTeam.formatNameForTeam(
                    playerListEntry?.team,
                    Component.nullToEmpty(playerListEntry?.profile?.name),
                ),
            )
    }

    private fun getPlayerInfo() = Client.getConnection()?.getPlayerInfo(mcValue.uuid)
}
