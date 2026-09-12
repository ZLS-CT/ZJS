package com.zephy.zjs.internal.mixins;

import com.zephy.zjs.internal.listeners.WorldListener;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.LevelRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;

import org.joml.Matrix4fc;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.renderer.state.level.CameraRenderState;

//#if MC<26.2
//$$import net.minecraft.client.renderer.state.level.LevelRenderState;
//$$import net.minecraft.util.profiling.ProfilerFiller;
//$$import org.joml.Matrix4f;
//$$import com.mojang.blaze3d.resource.ResourceHandle;
//$$import net.minecraft.client.Camera;
//$$import net.minecraft.client.renderer.MultiBufferSource;
//$$import net.minecraft.client.renderer.chunk.ChunkSectionsToRender;
//#endif

//#if MC<26.3
//$$import com.mojang.blaze3d.buffers.GpuBufferSlice;
//#else
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Final;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import net.minecraft.client.renderer.state.level.LevelRenderState;
//#endif

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
    //#if MC>=26.3
    @Shadow
    @Final
    private LevelRenderState levelRenderState;
    //#endif

    @ModifyExpressionValue(
        //#if MC<26.2
        //$$method = "lambda$addMainPass$0",
        //#else
        method = "submitFeatures",
        //#endif
        at = @At(
            value = "NEW",
            target = "()Lcom/mojang/blaze3d/vertex/PoseStack;"
        )
    )
    private PoseStack onMatrixStack(PoseStack original) {
        WorldListener.INSTANCE.setMatrixStack(original);
        return original;
    }

    @Inject(
        //#if MC<26.2
        //$$method = "renderLevel",
        //#else
        method = "render",
        //#endif
        at = @At("HEAD")
    )
    private void beforeRender(
        GraphicsResourceAllocator allocator,
        //#if MC<26.3
        //$$DeltaTracker tickCounter,
        //#endif
        boolean renderBlockOutline,
        CameraRenderState cameraState,
        //#if MC<26.3
        //$$Matrix4fc modelViewMatrix,
        //#endif
        GpuBufferSlice fogBuffer,
        Vector4f fogColor,
        boolean renderSky,
        //#if MC>=26.3
        boolean consistentDepthRequired,
        //#endif
        //#if MC>=26.1 && MC<26.2
        //$$ChunkSectionsToRender chunkSectionsToRender,
        //#endif
        CallbackInfo ci
    ) {
        WorldListener.INSTANCE.triggerRenderStart(
            //#if MC<26.3
            //$$tickCounter.getGameTimeDeltaTicks()
            //#else
            this.levelRenderState.worldPartialTicks
            //#endif
        );
    }

    @Inject(
        //#if MC<26.2
        //$$method = "lambda$addMainPass$0",
        //#else
        method = "render",
        //#endif
        at = @At("RETURN")
    )
    private void afterRender(
        //#if MC<26.2
        //$$GpuBufferSlice gpuBufferSlice,
        //$$LevelRenderState worldRenderState,
        //$$ProfilerFiller profiler,
        //#if MC>=26.1 && MC<26.2
        //$$ChunkSectionsToRender chunkSectionsToRender,
        //$$ResourceHandle entityOutlineTarget,
        //$$ResourceHandle translucentTarget,
        //$$ResourceHandle mainTarget,
        //$$ResourceHandle itemEntityTarget,
        //$$ResourceHandle particleTarget,
        //$$boolean renderOutline,
        //$$Matrix4fc modelViewMatrix,
        //#else
        //$$Matrix4f matrix4f,
        //$$ResourceHandle handle,
        //$$ResourceHandle handle2,
        //$$boolean bl,
        //$$ResourceHandle handle3,
        //$$ResourceHandle handle4,
        //#endif
        //#else
        GraphicsResourceAllocator resourceAllocator,
        //#if MC<26.3
        //$$DeltaTracker tickCounter,
        //#endif
        boolean renderOutline,
        CameraRenderState cameraState,
        //#if MC<26.3
        //$$Matrix4fc modelViewMatrix,
        //#endif
        GpuBufferSlice terrainFog,
        Vector4f fogColor,
        boolean shouldRenderSky,
        //#if MC>=26.3
        boolean consistentDepthRequired,
        //#endif
        //#endif
        CallbackInfo ci
    ) {
        WorldListener.INSTANCE.triggerRenderLast();
    }
}
