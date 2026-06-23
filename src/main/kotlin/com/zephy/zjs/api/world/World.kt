package com.zephy.zjs.api.world

import com.zephy.zjs.api.client.Client
import com.zephy.zjs.api.entity.ZBlockEntity
import com.zephy.zjs.api.entity.ZEntity
import com.zephy.zjs.api.entity.PlayerMP
import com.zephy.zjs.api.world.block.ZBlock
import com.zephy.zjs.api.world.block.ZBlockPos
import com.zephy.zjs.api.world.block.ZBlockType
import com.zephy.zjs.internal.mixins.ClientChunkCacheAccessor
import com.zephy.zjs.internal.mixins.ClientChunkMapAccessor
import com.zephy.zjs.internal.mixins.ClientLevelAccessor
import com.zephy.zjs.internal.utils.asMixin
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.BlockPos

//#if MC>=12111
//#endif

//#if MC<=12111
//$$import net.minecraft.world.item.ItemStack
//#else

//#endif

object World {
    @JvmStatic
    fun toMC(): ClientLevel? = Client.getMinecraft().level

    /**
     * Gets Minecraft's [ClientWorld] object
     *
     * @return The Minecraft [ClientWorld] object
     */
    @Deprecated("Use toMC", ReplaceWith("toMC()"))
    @JvmStatic
    fun getWorld(): ClientLevel? = toMC()

    @JvmStatic
    fun isLoaded(): Boolean = toMC() != null

    /**
     * Gets the [ZBlock] at a location in the world.
     *
     * @param x the x position
     * @param y the y position
     * @param z the z position
     * @return the [ZBlock] at the location
     */
    @JvmStatic
    fun getBlockAt(x: Number, y: Number, z: Number) = getBlockAt(ZBlockPos(x, y, z))

    /**
     * Gets the [ZBlock] at a location in the world.
     *
     * @param pos The block position
     * @return the [ZBlock] at the location
     */
    @JvmStatic
    fun getBlockAt(pos: ZBlockPos): ZBlock = ZBlock(ZBlockType(getBlockStateAt(pos).block), pos)

    /**
     * Gets the [BlockState] at a location in the world.
     *
     * @param pos The block position
     * @return the [BlockState] at the location
     */
    @JvmStatic
    fun getBlockStateAt(pos: ZBlockPos): BlockState = toMC()!!.getBlockState(pos.toMC())

    /**
     * Gets all of the players in the world, and returns their wrapped versions.
     *
     * @return the players
     */
    @JvmStatic
    fun getAllPlayers(): List<PlayerMP> = toMC()?.players()?.map(::PlayerMP) ?: listOf()

    /**
     * Gets a player by their username, must be in the currently loaded chunks!
     *
     * @param name the username
     * @return the player with said username, or null if they don't exist.
     */
    @JvmStatic
    fun getPlayerByName(name: String) = getAllPlayers().firstOrNull { it.getName() == name }

    @JvmStatic
    fun hasPlayer(name: String) = getPlayerByName(name) != null

    @JvmStatic
    fun getChunk(x: Int, y: Int, z: Int) = ZChunk(toMC()!!.getChunkAt(BlockPos(x, y, z)))

    @JvmStatic
    fun getAllEntities() = toMC()?.entitiesForRendering()?.map(ZEntity::fromMC) ?: listOf()

    /**
     * Gets every entity loaded in the world of a certain class
     *
     * @param clazz the class to filter for (Use `Java.type().class` to get this)
     * @return the entity list
     */
    @JvmStatic
    fun getAllEntitiesOfType(clazz: Class<*>): List<ZEntity> {
        return getAllEntities().filter {
            clazz.isInstance(it.toMC())
        }
    }

    @JvmStatic
    fun getAllBlockEntities(): List<ZBlockEntity> {
        val chunks = toMC()
            ?.asMixin<ClientLevelAccessor>()
            ?.chunkSource
            ?.asMixin<ClientChunkCacheAccessor>()
            ?.storage
            ?.asMixin<ClientChunkMapAccessor>()
            ?.chunks ?: return emptyList()

        val blockEntities = mutableListOf<ZBlockEntity>()

        for (i in 0 until chunks.length()) {
            blockEntities += ZChunk(chunks.getPlain(i) ?: continue).getAllBlockEntities()
        }

        return blockEntities
    }

    @JvmStatic
    fun getAllBlockEntitiesOfType(clazz: Class<*>): List<ZBlockEntity> {
        return getAllBlockEntities().filter {
            clazz.isInstance(it.toMC())
        }
    }
}
