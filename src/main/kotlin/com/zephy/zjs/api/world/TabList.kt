package com.zephy.zjs.api.world

import com.zephy.zjs.api.ZWrapper
import com.zephy.zjs.api.client.Client
import com.zephy.zjs.api.entity.ZTeam
import com.zephy.zjs.api.message.TextComponent
import gg.essential.elementa.state.BasicState
import net.minecraft.client.multiplayer.PlayerInfo
import net.minecraft.world.scores.DisplaySlot
import net.minecraft.world.scores.Objective

import net.minecraft.world.scores.PlayerTeam

object TabList {
    private var tabListNames = mutableListOf<Name>()
    var customHeader = false
    var customFooter = false
    private var tabListHeader: TextComponent? = null
    private var tabListFooter: TextComponent? = null

    @JvmStatic
    fun toMC() = Client.getTabGui()

    /**
     * Gets the scoreboard objective corresponding to the tab list, or null if it doesn't exist
     */
    @JvmStatic
    fun getObjective(): Objective? = Scoreboard.toMC()?.getDisplayObjective(DisplaySlot.LIST)

    /**
     * Gets the tab list header as a [TextComponent]
     *
     * @return the header
     */
    @JvmStatic
    fun getHeaderComponent(): TextComponent? {
        return tabListHeader
    }

    /**
     * Gets the tab list header as a formatted string.
     *
     * @return the header
     */
    @JvmStatic
    fun getHeader() = getHeaderComponent()?.formattedText

    /**
     * Gets the tab list footer as a [TextComponent]
     *
     * @return the footer
     */
    @JvmStatic
    fun getFooterComponent(): TextComponent? {
        return tabListFooter
    }

    /**
     * Gets the tab list footer as a string.
     * Be aware that this can contain color codes.
     *
     * @return the footer
     */
    @JvmStatic
    fun getFooter() = getFooterComponent()?.formattedText

    /**
     * Gets names set in scoreboard objectives
     *
     * @return The formatted names
     */
    @JvmStatic
    fun getNamesByObjectives(): List<String> {
        val scoreboard = Scoreboard.toMC() ?: return emptyList()
        val tabListObjective = getObjective() ?: return emptyList()

        val scores = scoreboard.listPlayerScores(tabListObjective)

        return scores.map {
            val team = scoreboard.getPlayerTeam(it.owner)
            TextComponent(PlayerTeam.formatNameForTeam(team, TextComponent(it.owner))).formattedText
        }
    }

    /**
     * Get all names on the tab list
     *
     * @return the list of names
     */
    @JvmStatic
    fun getNames(): List<Name> {
        return tabListNames
    }

    /**
     * Gets all names in tabs without formatting
     *
     * @return the unformatted names
     */
    @JvmStatic
    fun getUnformattedNames(): List<String> {
        return tabListNames.map { it.toMC().profile.name }
    }

    class Name(override val mcValue: PlayerInfo) : ZWrapper<PlayerInfo> {
        private val latencyState = BasicState(mcValue.latency)
        private val teamState = BasicState(mcValue.team)
        private val nameState = BasicState(mcValue.tabListDisplayName)

        /**
         * Gets the latency associated with this name
         *
         * @return the latency
         */
        fun getLatency(): Int = latencyState.get()

        /**
         * Gets the team associated with this name, if it exists
         *
         * @return the team, or null if it does not exist
         */
        fun getTeam(): ZTeam? = teamState.get()?.let(::ZTeam)

        /**
         * Gets the display text of this name
         *
         * @return the display name
         */
        fun getName(): TextComponent {
            val name = mcValue.profile.name

            return TextComponent(
                PlayerTeam.formatNameForTeam(
                    getTeam()?.mcValue,
                    TextComponent(nameState.get() ?: name),
                ),
            )
        }

        override fun toString(): String = getName().formattedText
    }
}
