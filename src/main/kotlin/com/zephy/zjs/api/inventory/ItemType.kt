package com.zephy.zjs.api.inventory

import com.zephy.zjs.api.ZWrapper
import com.zephy.zjs.api.message.TextComponent
import com.zephy.zjs.api.world.block.ZBlockType
import com.zephy.zjs.internal.utils.toIdentifier
import net.minecraft.world.item.Items
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.Item

class ItemType(override val mcValue: Item) : ZWrapper<Item> {
    init {
        require(mcValue !== Items.AIR) {
            "Can not wrap air as an ItemType"
        }
    }

    constructor(itemName: String) : this(BuiltInRegistries.ITEM.get(itemName.toIdentifier()).get().value())

    constructor(id: Int) : this(BuiltInRegistries.ITEM.byId(id))

    constructor(blockType: ZBlockType) : this(blockType.toMC().asItem())

    fun getName(): String = getNameComponent().formattedText

    fun getNameComponent(): TextComponent = TextComponent(mcValue.toString())

    fun getId(): Int = Item.getId(mcValue)

    fun getTranslationKey(): String = mcValue.descriptionId

    fun getRegistryName(): String = BuiltInRegistries.ITEM.getKey(mcValue).toString()

    fun asItem(): ZItem = ZItem(this)

    companion object {
        @JvmStatic
        fun fromMC(mcValue: Item): ItemType? {
            return if (mcValue === Items.AIR) {
                null
            } else {
                ItemType(mcValue)
            }
        }
    }
}
