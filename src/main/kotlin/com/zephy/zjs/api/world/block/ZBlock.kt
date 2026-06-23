package com.zephy.zjs.api.world.block

import com.zephy.zjs.api.client.ZPlayer
import com.zephy.zjs.api.world.World

/**
 * An immutable reference to a placed block in the world. It
 * has a block type, a position, and optionally a specific face.
 */
open class ZBlock(
    val type: ZBlockType,
    val pos: ZBlockPos,
    val face: BlockFace? = null,
) {
    val x: Int get() = pos.x
    val y: Int get() = pos.y
    val z: Int get() = pos.z

    fun withType(type: ZBlockType) = ZBlock(type, pos, face)

    fun withPos(pos: ZBlockPos) = ZBlock(type, pos, face)

    /**
     * Narrows this block to reference a certain face. Used by
     * [ZPlayer.lookingAt] to specify the block face
     * being looked at.
     */
    fun withFace(face: BlockFace) = ZBlock(type, pos, face)

    fun getState() = World.toMC()?.getBlockState(pos.toMC())

    override fun toString() = "Block{type=$type, pos=($x, $y, $z), face=$face}"
}
