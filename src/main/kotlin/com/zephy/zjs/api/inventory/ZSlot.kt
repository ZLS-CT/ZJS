package com.zephy.zjs.api.inventory

import com.zephy.zjs.api.ZWrapper
import net.minecraft.world.inventory.Slot

class ZSlot(override val mcValue: Slot) : ZWrapper<Slot> {
    val index by mcValue::index

    val displayX by mcValue::x

    val displayY by mcValue::y

    val inventory get() = Inventory(mcValue.container)

    val item get(): ZItem? = ZItem.fromMC(mcValue.item)

    val isEnabled get() = mcValue.isActive

    override fun toString() = "Slot(inventory=$inventory, index=$index, item=$item)"
}
