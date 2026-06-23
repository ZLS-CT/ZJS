package com.zephy.zjs.api.entity

import com.zephy.zjs.api.ZWrapper
import com.zephy.zjs.api.message.TextComponent
import com.zephy.zjs.api.render.GUIRenderer
import com.zephy.zjs.api.world.ZChunk
import com.zephy.zjs.api.world.block.ZBlockPos
import net.minecraft.world.entity.player.Player
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3
import java.util.UUID
import kotlin.math.sqrt

open class ZEntity(override val mcValue: Entity) : ZWrapper<Entity> {
    fun getX() = mcValue.position().x
    fun getY() = mcValue.position().y
    fun getZ() = mcValue.position().z

    fun getBlockPos() = ZBlockPos(getX(), getY(), getZ())

    fun getPos() = Vec3(getX(), getY(), getZ())

    fun getRotation() = mcValue.rotationVector

    fun getLastX() = mcValue.xOld

    fun getLastY() = mcValue.yOld

    fun getLastZ() = mcValue.zOld

    fun getLastPos() = Vec3(getLastX(), getLastY(), getLastZ())

    fun getRenderX() = getLastX() + (getX() - getLastX()) * GUIRenderer.partialTicks

    fun getRenderY() = getLastY() + (getY() - getLastY()) * GUIRenderer.partialTicks

    fun getRenderZ() = getLastZ() + (getZ() - getLastZ()) * GUIRenderer.partialTicks

    fun getRenderPos() = Vec3(getRenderX(), getRenderY(), getRenderZ())

    /**
     * Gets the pitch, the horizontal direction the entity is facing towards.
     * This has a range of -180 to 180.
     *
     * @return the entity's pitch
     */
    fun getPitch() = Mth.wrapDegrees(mcValue.getViewXRot(GUIRenderer.partialTicks))

    /**
     * Gets the yaw, the vertical direction the entity is facing towards.
     * This has a range of -180 to 180.
     *
     * @return the entity's yaw
     */
    fun getYaw() = Mth.wrapDegrees(mcValue.getViewYRot(GUIRenderer.partialTicks))

    /**
     * Gets the entity's x motion.
     * This is the amount the entity will move in the x direction next tick.
     *
     * @return the entity's x motion
     */
    fun getMotionX(): Double = mcValue.deltaMovement.x

    /**
     * Gets the entity's y motion.
     * This is the amount the entity will move in the y direction next tick.
     *
     * @return the entity's y motion
     */
    fun getMotionY(): Double = mcValue.deltaMovement.y

    /**
     * Gets the entity's z motion.
     * This is the amount the entity will move in the z direction next tick.
     *
     * @return the entity's z motion
     */
    fun getMotionZ(): Double = mcValue.deltaMovement.z

    fun getMotion(): Vec3 = mcValue.deltaMovement

    /**
     * Returns the entity this entity is riding, if one exists
     *
     * @return an Entity or null
     */
    fun getRiding(): ZEntity? = mcValue.vehicle?.let(::fromMC)

    /**
     * Returns a list of all entity riding this entity
     *
     * @return List of entities, empty if there are no riders
     */
    fun getRiders() = mcValue.passengers.map(::fromMC).orEmpty()

    /**
     * Checks whether the entity is dead.
     * This is a fairly loose term, dead for a particle could mean it has faded,
     * while dead for an entity means it has no health.
     *
     * @return whether an entity is dead
     */
    fun isDead(): Boolean = !mcValue.isAlive

    /**
     * Gets the entire width of the entity's hitbox
     *
     * @return the entity's width
     */
    fun getWidth(): Float = mcValue.bbWidth

    /**
     * Gets the entire height of the entity's hitbox
     *
     * @return the entity's height
     */
    fun getHeight(): Float = mcValue.bbHeight

    /**
     * Gets the height of the eyes on the entity,
     * can be added to its Y coordinate to get the actual Y location of the eyes.
     * This value defaults to 85% of an entity's height, however is different for some entities.
     *
     * @return the height of the entity's eyes
     */
    fun getEyeHeight(): Float = mcValue.eyeHeight

    /**
     * Gets the name of the entity, could be "Villager",
     * or, if the entity has a custom name, it returns that.
     *
     * @return the (custom) name of the entity as a String
     */
    fun getName(): String = getNameComponent().unformattedText

    /**
     * Gets the name of the entity, could be "Villager",
     * or, if the entity has a custom name, it returns that.
     *
     * @return the (custom) name of the entity as a [TextComponent]
     */
    fun getNameComponent(): TextComponent = TextComponent(mcValue.name)

    /**
     * Gets the Java class name of the entity, for example "EntityVillager"
     *
     * @return the entity's class name
     */
    fun getClassName(): String = mcValue.javaClass.simpleName

    /**
     * Gets the Java UUID object of this entity.
     * Use of [UUID.toString] in conjunction is recommended.
     *
     * @return the entity's uuid
     */
    fun getUUID(): UUID = mcValue.uuid

    fun distanceTo(other: ZEntity): Float = distanceTo(other.mcValue)

    fun distanceTo(other: Entity): Float = mcValue.distanceTo(other)

    fun distanceTo(blockPos: ZBlockPos): Double = distanceTo(
        blockPos.x.toDouble(),
        blockPos.y.toDouble(),
        blockPos.z.toDouble(),
    )

    fun distanceTo(x: Double, y: Double, z: Double): Double = sqrt(mcValue.distanceToSqr(x, y, z))

    fun isOnGround() = mcValue.onGround()

    @JvmOverloads
    fun getLookVector(partialTicks: Float = GUIRenderer.partialTicks) = mcValue.getViewVector(partialTicks)

    @JvmOverloads
    fun getEyePosition(partialTicks: Float = GUIRenderer.partialTicks) = mcValue.eyePosition

    fun isSneaking() = mcValue.isCrouching

    fun isSprinting() = mcValue.isSprinting

    fun getWorld() = mcValue.level()

    fun getChunk(): ZChunk = ZChunk(getWorld().getChunkAt(mcValue.onPos))

    override fun toString(): String {
        val coordStrings = listOf(getX(), getY(), getZ()).map { "%.3f".format(it) }
        return "${this::class.simpleName}(name=${getName()}, pos=[${coordStrings.joinToString()}])"
    }

    companion object {
        @JvmStatic
        fun fromMC(entity: Entity): ZEntity = when (entity) {
            is Player -> PlayerMP(entity)
            else -> ZEntity(entity)
        }
    }
}
