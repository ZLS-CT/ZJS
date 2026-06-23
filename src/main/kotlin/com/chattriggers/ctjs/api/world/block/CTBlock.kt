package com.chattriggers.ctjs.api.world.block

import com.chattriggers.ctjs.api.client.CTPlayer
import com.chattriggers.ctjs.api.inventory.CTItem
import com.chattriggers.ctjs.api.world.World

/**
 * An immutable reference to a placed block in the world. It
 * has a block type, a position, and optionally a specific face.
 */
open class CTBlock(
    val type: CTBlockType,
    val pos: CTBlockPos,
    val face: BlockFace? = null,
) {
    val x: Int get() = pos.x
    val y: Int get() = pos.y
    val z: Int get() = pos.z

    fun withType(type: CTBlockType) = CTBlock(type, pos, face)

    fun withPos(pos: CTBlockPos) = CTBlock(type, pos, face)

    /**
     * Narrows this block to reference a certain face. Used by
     * [CTPlayer.lookingAt] to specify the block face
     * being looked at.
     */
    fun withFace(face: BlockFace) = CTBlock(type, pos, face)

    fun getState() = World.toMC()?.getBlockState(pos.toMC())

    override fun toString() = "Block{type=$type, pos=($x, $y, $z), face=$face}"
}
