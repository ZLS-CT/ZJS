package com.zephy.zjs.api.client

import net.minecraft.world.entity.player.PlayerModelPart
import net.minecraft.sounds.SoundSource

object Settings {
    @JvmStatic
    fun toMC() = Client.getMinecraft().options

    @JvmStatic
    @Deprecated("Use toMC", ReplaceWith("toMC()"))
    fun getSettings() = toMC()

    @JvmStatic
    fun getFOV(): Int = toMC().fov().get()

    @JvmStatic
    fun setFOV(fov: Int) {
        toMC().fov().set(fov)
    }

    @JvmField
    val skin = SkinWrapper()

    @JvmField
    val sound = SoundWrapper()

    @JvmField
    val chat = ChatWrapper()

    @JvmField
    val video = VideoWrapper()

    class SkinWrapper {
        fun isCapeEnabled() = toMC().isModelPartEnabled(PlayerModelPart.CAPE)

        fun setCapeEnabled(toggled: Boolean) {
            toMC().setModelPart(PlayerModelPart.CAPE, toggled)
        }

        fun isJacketEnabled() = toMC().isModelPartEnabled(PlayerModelPart.JACKET)

        fun setJacketEnabled(toggled: Boolean) {
            toMC().setModelPart(PlayerModelPart.JACKET, toggled)
        }

        fun isLeftSleeveEnabled() = toMC().isModelPartEnabled(PlayerModelPart.LEFT_SLEEVE)

        fun setLeftSleeveEnabled(toggled: Boolean) {
            toMC().setModelPart(PlayerModelPart.LEFT_SLEEVE, toggled)
        }

        fun isRightSleeveEnabled() = toMC().isModelPartEnabled(PlayerModelPart.RIGHT_SLEEVE)

        fun setRightSleeveEnabled(toggled: Boolean) {
            toMC().setModelPart(PlayerModelPart.RIGHT_SLEEVE, toggled)
        }

        fun isLeftPantsLegEnabled() = toMC().isModelPartEnabled(PlayerModelPart.LEFT_PANTS_LEG)

        fun setLeftPantsLegEnabled(toggled: Boolean) {
            toMC().setModelPart(PlayerModelPart.LEFT_PANTS_LEG, toggled)
        }

        fun isRightPantsLegEnabled() = toMC().isModelPartEnabled(PlayerModelPart.RIGHT_PANTS_LEG)

        fun setRightPantsLegEnabled(toggled: Boolean) {
            toMC().setModelPart(PlayerModelPart.RIGHT_PANTS_LEG, toggled)
        }

        fun isHatEnabled() = toMC().isModelPartEnabled(PlayerModelPart.HAT)

        fun setHatEnabled(toggled: Boolean) {
            toMC().setModelPart(PlayerModelPart.HAT, toggled)
        }
    }

    class SoundWrapper {
        fun getMasterVolume() = toMC().getSoundSourceOptionInstance(SoundSource.MASTER).get()

        fun setMasterVolume(level: Double) {
            toMC().getSoundSourceOptionInstance(SoundSource.MASTER).set(level)
        }

        fun getMusicVolume() = toMC().getSoundSourceOptionInstance(SoundSource.MUSIC).get()

        fun setMusicVolume(level: Double) {
            toMC().getSoundSourceOptionInstance(SoundSource.MUSIC).set(level)
        }

        fun getNoteblockVolume() = toMC().getSoundSourceOptionInstance(SoundSource.RECORDS).get()

        fun setNoteblockVolume(level: Double) {
            toMC().getSoundSourceOptionInstance(SoundSource.RECORDS).set(level)
        }

        fun getWeather() = toMC().getSoundSourceOptionInstance(SoundSource.WEATHER).get()

        fun setWeather(level: Double) {
            toMC().getSoundSourceOptionInstance(SoundSource.WEATHER).set(level)
        }

        fun getBlocks() = toMC().getSoundSourceOptionInstance(SoundSource.BLOCKS).get()

        fun setBlocks(level: Double) {
            toMC().getSoundSourceOptionInstance(SoundSource.BLOCKS).set(level)
        }

        fun getHostileCreatures() = toMC().getSoundSourceOptionInstance(SoundSource.HOSTILE).get()

        fun setHostileCreatures(level: Double) {
            toMC().getSoundSourceOptionInstance(SoundSource.HOSTILE).set(level)
        }

        fun getFriendlyCreatures() = toMC().getSoundSourceOptionInstance(SoundSource.NEUTRAL).get()

        fun setFriendlyCreatures(level: Double) {
            toMC().getSoundSourceOptionInstance(SoundSource.NEUTRAL).set(level)
        }

        fun getPlayers() = toMC().getSoundSourceOptionInstance(SoundSource.PLAYERS).get()

        fun setPlayers(level: Double) {
            toMC().getSoundSourceOptionInstance(SoundSource.PLAYERS).set(level)
        }

        fun getAmbient() = toMC().getSoundSourceOptionInstance(SoundSource.AMBIENT).get()

        fun setAmbient(level: Double) {
            toMC().getSoundSourceOptionInstance(SoundSource.AMBIENT).set(level)
        }
    }

    class VideoWrapper {
        fun getRenderDistance() = toMC().renderDistance().get()

        fun setRenderDistance(distance: Int) {
            toMC().renderDistance().set(distance)
        }

        fun getSmoothLighting() = toMC().ambientOcclusion().get()

        fun setSmoothLighting(enabled: Boolean) {
            toMC().ambientOcclusion().set(enabled)
        }

        fun getMaxFrameRate() = toMC().framerateLimit().get()

        fun setMaxFrameRate(frameRate: Int) {
            toMC().framerateLimit().set(frameRate)
        }

        fun getBobbing() = toMC().bobView().get()

        fun setBobbing(toggled: Boolean) {
            toMC().bobView().set(toggled)
        }

        fun getGuiScale() = toMC().guiScale().get()

        fun setGuiScale(scale: Int) {
            toMC().guiScale().set(scale)
        }

        fun getBrightness() = toMC().gamma().get()

        fun setBrightness(brightness: Double) {
            toMC().gamma().set(brightness)
        }

        fun getFullscreen() = toMC().fullscreen().get()

        fun setFullscreen(toggled: Boolean) {
            toMC().fullscreen().set(toggled)
        }

        fun getVsync() = toMC().enableVsync().get()

        fun setVsync(toggled: Boolean) {
            toMC().enableVsync().set(toggled)
        }

        fun getMipmapLevels() = toMC().mipmapLevels().get()

        fun setMipmapLevels(mipmapLevels: Int) {
            toMC().mipmapLevels().set(mipmapLevels)
        }

        fun getEntityShadows() = toMC().entityShadows().get()

        fun setEntityShadows(toggled: Boolean) {
            toMC().entityShadows().set(toggled)
        }
    }

    class ChatWrapper {
        fun getColors() = toMC().chatColors().get()

        fun setColors(toggled: Boolean) {
            toMC().chatColors().set(toggled)
        }

        fun getWebLinks() = toMC().chatLinks().get()

        fun setWebLinks(toggled: Boolean) {
            toMC().chatLinks().set(toggled)
        }

        fun getOpacity() = toMC().chatOpacity().get()

        fun setOpacity(opacity: Double) {
            toMC().chatOpacity().set(opacity)
        }

        fun getPromptOnWebLinks() = toMC().chatLinksPrompt().get()

        fun setPromptOnWebLinks(toggled: Boolean) {
            toMC().chatLinksPrompt().set(toggled)
        }

        fun getScale() = toMC().chatScale().get()

        fun setScale(scale: Double) {
            toMC().chatScale().set(scale)
        }

        fun getFocusedHeight() = toMC().chatHeightFocused().get()

        fun setFocusedHeight(height: Double) {
            toMC().chatHeightFocused().set(height)
        }

        fun getUnfocusedHeight() = toMC().chatHeightUnfocused().get()

        fun setUnfocusedHeight(height: Double) {
            toMC().chatHeightUnfocused().set(height)
        }

        fun getWidth() = toMC().chatWidth().get()

        fun setWidth(width: Double) {
            toMC().chatWidth().set(width)
        }

        fun getReducedDebugInfo() = toMC().reducedDebugInfo().get()

        fun setReducedDebugInfo(toggled: Boolean) {
            toMC().reducedDebugInfo().set(toggled)
        }
    }
}
