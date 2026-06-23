package com.zephy.zjs.api.world

import com.zephy.zjs.api.ZWrapper
import com.zephy.zjs.api.entity.ZTeam
import com.zephy.zjs.api.message.TextComponent
import com.zephy.zjs.internal.mixins.`Scoreboard$1Accessor`
import com.zephy.zjs.internal.utils.asMixin
import gg.essential.elementa.state.BasicState
import net.minecraft.world.scores.ScoreAccess
import net.minecraft.world.scores.DisplaySlot
import net.minecraft.world.scores.Objective
import net.minecraft.network.chat.numbers.NumberFormat
import net.minecraft.world.scores.PlayerTeam

object Scoreboard {
    private var scoreboardNames = mutableListOf<Score>()
    private var scoreboardTitle = TextComponent("")

    @JvmStatic
    fun toMC() = World.toMC()?.scoreboard

    @Deprecated("Use toMC", ReplaceWith("toMC()"))
    @JvmStatic
    fun getScoreboard() = toMC()

    @JvmStatic
    fun getSidebar(): Objective? = toMC()?.getDisplayObjective(DisplaySlot.SIDEBAR)

    /**
     * Gets the top-most string which is displayed on the scoreboard. (doesn't have a score on the side).
     * Be aware that this can contain color codes.
     *
     * @return the scoreboard title
     */
    @JvmStatic
    fun getTitle(): TextComponent = scoreboardTitle

    /**
     * Get all currently visible strings on the scoreboard. (excluding title)
     * Be aware that this can contain color codes.
     *
     * @return the list of lines
     */
    @JvmStatic
    @JvmOverloads
    fun getLines(descending: Boolean = true): List<Score> = if (descending) scoreboardNames else scoreboardNames.asReversed()

    /**
     * Gets the line at the specified index (0 based)
     * Equivalent to Scoreboard.getLines().get(index)
     *
     * @param index the line index
     * @return the score object at the index
     */
    @JvmStatic
    fun getLineByIndex(index: Int): Score = getLines()[index]

    /**
     * Gets a list of lines that have a certain score,
     * i.e. the numbers shown on the right
     *
     * @param score the score to look for
     * @return a list of actual score objects
     */
    @JvmStatic
    fun getLinesByScore(score: Int): List<Score> = getLines().filter {
        it.getScore() == score
    }

    class Score(override val mcValue: ScoreAccess) : ZWrapper<ScoreAccess> {
        private val scoreState = BasicState(mcValue.get())
        private val nameState = BasicState(mcValue.display())
        private val formatState = BasicState(mcValue.asMixin<`Scoreboard$1Accessor`>().score.numberFormat())
        private val teamState = run {
            val scoreboard = Scoreboard.toMC()!!
            val name = mcValue.asMixin<`Scoreboard$1Accessor`>().holder.scoreboardName

            BasicState(scoreboard.getPlayersTeam(name))
        }

        /**
         * Gets the team associated with this score, if it exists
         *
         * @return the team, or null if it does not exist
         */
        fun getTeam(): ZTeam? = teamState.get()?.let(::ZTeam)

        /**
         * Gets the score value for this score,
         * i.e. the number on the right of the board
         *
         * @return the actual point value
         */
        fun getScore(): Int = scoreState.get()

        /**
         * Gets the display text of this score
         *
         * @return the display name
         */
        fun getName(): TextComponent {
            val name = mcValue.asMixin<`Scoreboard$1Accessor`>().holder.scoreboardName

            return TextComponent(
                PlayerTeam.formatNameForTeam(
                    getTeam()?.mcValue,
                    TextComponent(nameState.get() ?: name),
                ),
            )
        }

        /**
         * Gets the number format of this score
         *
         * @return the number format
         */
        fun getNumberFormat(): NumberFormat? = formatState.get()

        override fun toString(): String = getName().formattedText
    }
}
