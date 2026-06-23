package com.zephy.zjs.api.inventory

import com.zephy.zjs.api.ZWrapper
import com.zephy.zjs.api.client.Client
import com.zephy.zjs.api.client.ZPlayer
import com.zephy.zjs.api.message.TextComponent
import com.zephy.zjs.api.render.RenderUtils
import com.zephy.zjs.api.world.World
import com.zephy.zjs.internal.Skippable
import com.zephy.zjs.internal.TooltipOverridable
import com.zephy.zjs.internal.utils.asMixin
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.client.renderer.item.ItemStackRenderState
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.item.Item.TooltipContext
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.ReportedException
import net.minecraft.CrashReport
import net.minecraft.client.renderer.SubmitNodeStorage
import kotlin.jvm.optionals.getOrNull

class ZItem(override val mcValue: ItemStack) : ZWrapper<ItemStack> {
    val type: ItemType = ItemType(mcValue.item)

    init {
        require(!mcValue.isEmpty) {
            "Can not wrap empty ItemStack as an Item"
        }
    }

    constructor(type: ItemType) : this(type.toMC().defaultInstance)

    fun getStackSize(): Int = mcValue.count

    fun setStackSize(size: Int) = apply {
        mcValue.count = size
    }

    fun getEnchantments() = EnchantmentHelper.getEnchantmentsForCrafting(mcValue).keySet().associate {
        it.unwrapKey().getOrNull() to EnchantmentHelper.getItemEnchantmentLevel(it, mcValue)
    }

    fun getDamage() = mcValue.damageValue

    fun getName(): String = TextComponent(mcValue.hoverName).formattedText

    fun setName(name: TextComponent?) = apply {
        mcValue.set(DataComponents.CUSTOM_NAME, name)
    }

    fun resetName() {
        setName(null)
    }

    @JvmOverloads
    fun getLore(advanced: Boolean = false): MutableList<TextComponent> {
        mcValue.asMixin<Skippable>().ctjs_setShouldSkip(true)
        val tooltip = mcValue
            .getTooltipLines(
                TooltipContext.EMPTY,
                ZPlayer.toMC(),
                if (advanced) TooltipFlag.ADVANCED else TooltipFlag.NORMAL,
            ).mapTo(mutableListOf()) {
                TextComponent(it)
            }

        mcValue.asMixin<Skippable>().ctjs_setShouldSkip(false)

        return tooltip
    }

    fun setLore(lore: List<TextComponent>) {
        mcValue.asMixin<TooltipOverridable>().apply {
            ctjs_setTooltip(lore)
            ctjs_setShouldOverrideTooltip(true)
        }
    }

    fun resetLore() {
        mcValue.asMixin<TooltipOverridable>().ctjs_setShouldOverrideTooltip(false)
    }

    // TODO: make a component wrapper?
    fun getComponents() = mcValue.components

    /**
     * Renders the item icon to the client's overlay, with customizable overlay information.
     *
     * @param x the x location
     * @param y the y location
     * @param scale the scale
     * @param z the z level to draw the item at
     */
    @JvmOverloads
    fun draw(x: Float = 0f, y: Float = 0f, scale: Float = 1f, z: Float = 200f) {
        val itemRenderState = ItemStackRenderState()

        RenderUtils
            .pushMatrix()
            .scale(scale, scale, 1f)
            .translate(x / scale, y / scale, z)

        // The item draw method moved to DrawContext in 1.20, which we don't have access
        // to here, so its drawItem method has been copy-pasted here instead
        if (mcValue.isEmpty) return

        Client.getMinecraft().itemModelResolver.updateForTopItem(itemRenderState, mcValue, ItemDisplayContext.GUI, World.toMC(), null, 0)
        RenderUtils
            .pushMatrix()
            .translate(x + 8, y + 8, 150 + z)
        try {
            //#if MC<=12111
            //$$val orderedRender = Client.getMinecraft().gameRenderer.submitNodeStorage
            //#else
            val orderedRender = SubmitNodeStorage()
            //#endif
            //#if MC<26.2
            //$$val vertexConsumers = Client.getMinecraft().renderBuffers().bufferSource()
            //#else
            val vertexConsumers = Client.getMinecraft().gameRenderer.renderBuffers().stagedVertexBuffer()
            //#endif
            RenderUtils.scale(16.0f, -16.0f, 16.0f)
            if (!itemRenderState.usesBlockLight()) {
                //#if MC<=26.2
                //$$vertexConsumers.endBatch()
                //#else
                vertexConsumers.endFrame()
                //#endif
            }

            itemRenderState.submit(
                RenderUtils.matrixStack.toMC(),
                orderedRender,
                15728880,
                OverlayTexture.NO_OVERLAY,
                0,
            )

            RenderUtils.disableDepth()
            //#if MC<=26.2
            //$$vertexConsumers.endBatch()
            //#else
            vertexConsumers.endFrame()
            //#endif
            RenderUtils.enableDepth()
        } catch (e: Throwable) {
            val crashReport = CrashReport.forThrowable(e, "Rendering item")
            val crashReportSection = crashReport.addCategory("Item being rendered")
            crashReportSection.setDetail("Item Type") { mcValue.item.toString() }
            crashReportSection.setDetail("Item Damage") { mcValue.damageValue.toString() }
            crashReportSection.setDetail("Item Components") { mcValue.components.toString() }
            crashReportSection.setDetail("Item Foil") { mcValue.hasFoil().toString() }
            throw ReportedException(crashReport)
        } finally {
            RenderUtils
                .popMatrix()
                .popMatrix()
        }
    }

    override fun toString(): String = "Item{name=${getName()}, type=${type.getRegistryName()}, size=${getStackSize()}}"

    companion object {
        @JvmStatic
        fun fromMC(mcValue: ItemStack): ZItem? {
            return if (mcValue.isEmpty) null
            else ZItem(mcValue)
        }
    }
}
