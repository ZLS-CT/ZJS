package com.zephy.zjs.api.world.block

import com.zephy.zjs.api.ZWrapper
import com.zephy.zjs.api.inventory.ZItem
import com.zephy.zjs.api.inventory.ItemType
import com.zephy.zjs.api.message.TextComponent
import com.zephy.zjs.internal.utils.toIdentifier
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block

/**
 * An immutable wrapper around Minecraft's Block object. Note
 * that this references a block "type", and not an actual block
 * in the world. If a reference to a particular block is needed,
 * use [ZBlock]
 */
class ZBlockType(override val mcValue: Block) : ZWrapper<Block> {
    constructor(block: ZBlockType) : this(block.mcValue)

    constructor(blockName: String) : this(BuiltInRegistries.BLOCK[blockName.toIdentifier()].get().value())

    constructor(blockID: Int) : this(ItemType(Item.byId(blockID)).getRegistryName())

    constructor(item: ZItem) : this(Block.byItem(item.mcValue.item))

    /**
     * Returns a [ZBlock] based on this block and the
     * provided BlockPos
     *
     * @param blockPos the block position
     * @return a [ZBlock] object
     */
    fun withBlockPos(blockPos: ZBlockPos) = ZBlock(this, blockPos)

    fun getID(): Int = BuiltInRegistries.BLOCK.indexOf(mcValue)

    fun getDefaultState() = mcValue.defaultBlockState()

    /**
     * Gets the block's registry name.
     * Example: minecraft:oak_planks
     *
     * @return the block's registry name
     */
    fun getRegistryName(): String = BuiltInRegistries.BLOCK.getKey(mcValue).toString()

    /**
     * Gets the block's translation key.
     * Example: block.minecraft.oak_planks
     *
     * @return the block's translation key
     */
    fun getTranslationKey(): String = mcValue.descriptionId

    /**
     * Gets the block's localized name.
     * Example: Wooden Planks
     *
     * @return the block's localized name
     */
    fun getName() = TextComponent(mcValue.name).formattedText

    override fun toString(): String = "BlockType{${getRegistryName()}}"
}
