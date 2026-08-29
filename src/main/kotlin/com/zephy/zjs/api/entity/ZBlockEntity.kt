package com.zephy.zjs.api.entity

import com.zephy.zjs.api.ZWrapper
import com.zephy.zjs.api.world.block.ZBlock
import com.zephy.zjs.api.world.block.ZBlockPos
import com.zephy.zjs.api.world.block.ZBlockType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.entity.BlockEntity

class ZBlockEntity(override val mcValue: BlockEntity) : ZWrapper<BlockEntity> {
    fun getX(): Int = getBlockPos().x

    fun getY(): Int = getBlockPos().y

    fun getZ(): Int = getBlockPos().z

    fun getBlockType(): ZBlockType = ZBlockType(BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(mcValue.type)!!.toString())

    fun getBlockPos(): ZBlockPos = ZBlockPos(mcValue.blockPos)

    fun getBlock(): ZBlock = ZBlock(getBlockType(), getBlockPos())

    override fun toString(): String = "BlockEntity(type=${getBlockType()}, pos=[${getX()}, ${getY()}, ${getZ()}])"
}
