package com.zephy.zjs.api.client

import com.zephy.zjs.api.client.MathLib.wrapAngleTo180
import com.zephy.zjs.api.entity.ZEntity
import com.zephy.zjs.api.entity.PlayerMP
import com.zephy.zjs.api.entity.ZTeam
import com.zephy.zjs.api.inventory.Inventory
import com.zephy.zjs.api.inventory.ZItem
import com.zephy.zjs.api.message.TextComponent
import com.zephy.zjs.api.render.GUIRenderer
import com.zephy.zjs.api.world.Scoreboard
import com.zephy.zjs.api.world.World
import com.zephy.zjs.api.world.block.BlockFace
import com.zephy.zjs.api.world.block.ZBlockPos
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.InteractionHand
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3
import java.util.UUID

object ZPlayer {
    @JvmStatic
    fun toMC() = Client.getMinecraft().player

    @JvmField
    val armor = ArmorWrapper()

    /**
     * Gets Minecraft's EntityPlayerSP object representing the user
     *
     * @return The Minecraft EntityPlayerSP object representing the user
     */
    @Deprecated("Use toMC", ReplaceWith("toMC()"))
    @JvmStatic
    fun getPlayer() = toMC()

    @JvmStatic
    fun getTeam(): ZTeam? = Scoreboard.toMC()?.getPlayerTeam(getName())?.let(::ZTeam)

    @JvmStatic
    fun asPlayerMP(): PlayerMP? = toMC()?.let(::PlayerMP)

    @JvmStatic
    fun getX(): Double = toMC()?.x ?: 0.0

    @JvmStatic
    fun getY(): Double = toMC()?.y ?: 0.0

    @JvmStatic
    fun getZ(): Double = toMC()?.z ?: 0.0

    @JvmStatic
    fun getBlockPos(): ZBlockPos = ZBlockPos(getX(), getY(), getZ())

    @JvmStatic
    fun getPos(): Vec3 = Vec3(getX(), getY(), getZ())

    @JvmStatic
    fun getRotation() = toMC()?.rotationVector ?: Vec2(0f, 0f)

    @JvmStatic
    fun getLastX(): Double = toMC()?.xOld ?: 0.0

    @JvmStatic
    fun getLastY(): Double = toMC()?.yOld ?: 0.0

    @JvmStatic
    fun getLastZ(): Double = toMC()?.zOld ?: 0.0

    @JvmStatic
    fun getLastPos(): Vec3 = Vec3(getLastX(), getLastY(), getLastZ())

    @JvmStatic
    fun getRenderX(): Double = getLastX() + (getX() - getLastX()) * GUIRenderer.partialTicks

    @JvmStatic
    fun getRenderY(): Double = getLastY() + (getY() - getLastY()) * GUIRenderer.partialTicks

    @JvmStatic
    fun getRenderZ(): Double = getLastZ() + (getZ() - getLastZ()) * GUIRenderer.partialTicks

    @JvmStatic
    fun getRenderPos(): Vec3 = Vec3(getRenderX(), getRenderY(), getRenderZ())

    /**
     * Gets the player's x motion.
     * This is the amount the player will move in the x direction next tick.
     *
     * @return the player's x motion
     */
    @JvmStatic
    fun getMotionX(): Double = toMC()?.deltaMovement?.x ?: 0.0

    /**
     * Gets the player's y motion.
     * This is the amount the player will move in the y direction next tick.
     *
     * @return the player's y motion
     */
    @JvmStatic
    fun getMotionY(): Double = toMC()?.deltaMovement?.y ?: 0.0

    /**
     * Gets the player's z motion.
     * This is the amount the player will move in the z direction next tick.
     *
     * @return the player's z motion
     */
    @JvmStatic
    fun getMotionZ(): Double = toMC()?.deltaMovement?.z ?: 0.0

    /**
     * Gets the player's motion as a [Vec3d].
     * This is the amount the player will move in the x, y, and z directions next tick.
     *
     * @return the player's motion as a [Vec3d]
     */
    @JvmStatic
    fun getMotion(): Vec3 = Vec3(getMotionX(), getMotionY(), getMotionZ())

    /**
     * Gets the player's camera pitch.
     *
     * @return the player's camera pitch
     */
    @JvmStatic
    fun getPitch(): Double = wrapAngleTo180(toMC()?.xRot?.toDouble() ?: 0.0)

    /**
     * Gets the player's camera yaw.
     *
     * @return the player's camera yaw
     */
    @JvmStatic
    fun getYaw(): Double = wrapAngleTo180(toMC()?.yRot?.toDouble() ?: 0.0)

    /**
     * Gets the player's username.
     *
     * @return the player's username
     */
    @JvmStatic
    fun getName(): String = Client.getMinecraft().user.name

    /**
     * Gets the Java UUID object of the player.
     * Use of [UUID.toString] in conjunction is recommended.
     *
     * @return the player's uuid
     */
    @JvmStatic
    fun getUUID(): UUID = Client.getMinecraft().gameProfile.id

    @JvmStatic
    fun isMoving(): Boolean = toMC()?.speed?.let { it != 0f } ?: false

    @JvmStatic
    fun isSneaking(): Boolean = toMC()?.isCrouching ?: false

    @JvmStatic
    fun isSprinting(): Boolean = toMC()?.isSprinting ?: false

    /**
     * Checks if player can be pushed by water.
     *
     * @return true if the player is flying, false otherwise
     */
    @JvmStatic
    fun isFlying(): Boolean = toMC()?.abilities?.flying ?: false

    /**
     * Gets the direction the player is facing.
     * Example: "South West"
     *
     * @return The direction the player is facing, one of the four cardinal directions
     */
    @JvmStatic
    fun facing(): String {
        if (toMC() == null) return ""

        val yaw = getYaw()

        return when {
            yaw in -22.5..22.5 -> "South"
            yaw in 22.5..67.5 -> "South West"
            yaw in 67.5..112.5 -> "West"
            yaw in 112.5..157.5 -> "North West"
            yaw < -157.5 || yaw > 157.5 -> "North"
            yaw in -157.5..-112.5 -> "North East"
            yaw in -112.5..-67.5 -> "East"
            yaw in -67.5..-22.5 -> "South East"
            else -> ""
        }
    }

    /**
     * Gets the current object that the player is looking at,
     * whether that be a block or an entity. Returns null when not looking
     * at anything.
     *
     * @return the [Block] or [ZEntity] being looked at, or null if air
     */
    @JvmStatic
    fun lookingAt(): Any? {
        val target = Client.getMinecraft().hitResult

        return when (target?.type) {
            HitResult.Type.MISS -> null
            HitResult.Type.BLOCK -> {
                val block = target as BlockHitResult
                World.getBlockAt(ZBlockPos(block.blockPos)).withFace(BlockFace.fromMC(block.direction))
            }
            HitResult.Type.ENTITY -> {
                ZEntity.fromMC((target as EntityHitResult).entity)
            }
            null -> null
        }
    }

    /**
     * Gets the current item in the player's hand.
     *
     * @param hand the hand of the item
     * @return the current held [ZItem]
     */
    @JvmOverloads
    @JvmStatic
    fun getHeldItem(hand: InteractionHand = InteractionHand.MAIN_HAND): ZItem? {
        return toMC()?.getItemInHand(hand)?.let(ZItem::fromMC)
    }

    /**
     * Gets the current index of the held item.
     *
     * @return the current index
     */
    @JvmStatic
    fun getHeldItemIndex(): Int = toMC()?.inventory?.selectedSlot ?: -1

    /**
     * Gets the inventory of the player, i.e. the inventory accessed by 'e'.
     *
     * @return the player's inventory
     */
    @JvmStatic
    fun getInventory(): Inventory? = toMC()?.inventory?.let(::Inventory)

    /**
     * Gets the display name for the player,
     * i.e. the name shown in tab list and in the player's nametag.
     * @return the display name
     */
    @JvmStatic
    fun getDisplayName(): TextComponent = asPlayerMP()?.getDisplayName() ?: TextComponent("")

    /**
     * Gets the container the user currently has open, i.e. a chest.
     *
     * @return the currently opened container
     */
    @JvmStatic
    fun getContainer(): Inventory? = (Client.currentGui.get() as? AbstractContainerScreen<*>)?.let(::Inventory)

    class ArmorWrapper {
        /**
         * @return the [ZItem] in the player's helmet slot or null if the slot is empty
         */
        fun getHelmet(): ZItem? = getInventory()?.getStackInSlot(39)

        /**
         * @return the [ZItem] in the player's chestplate slot or null if the slot is empty
         */
        fun getChestplate(): ZItem? = getInventory()?.getStackInSlot(38)

        /**
         * @return the [ZItem] in the player's leggings slot or null if the slot is empty
         */
        fun getLeggings(): ZItem? = getInventory()?.getStackInSlot(37)

        /**
         * @return the [ZItem] in the player's boots slot or null if the slot is empty
         */
        fun getBoots(): ZItem? = getInventory()?.getStackInSlot(36)
    }
}
