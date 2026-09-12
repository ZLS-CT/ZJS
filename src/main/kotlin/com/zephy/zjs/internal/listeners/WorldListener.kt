package com.zephy.zjs.internal.listeners

import com.zephy.zjs.api.triggers.TriggerType
import com.mojang.blaze3d.systems.RenderSystem

import com.mojang.blaze3d.vertex.PoseStack
import org.joml.Matrix4f
import org.zephy.zrenderlib.RenderUtils

object WorldListener {
    private var matrixStack: PoseStack? = null
    private var deltaTicks: Float = 1f

    //#if MC>=26.2
    var viewMatrix: Matrix4f = Matrix4f()
        private set
    //#endif

    fun setMatrixStack(stack: PoseStack) {
        //#if MC<26.2
        //$$matrixStack = stack
        //#else
        viewMatrix = Matrix4f(RenderSystem.getModelViewStack())
        val copy = PoseStack()
        copy.mulPose(viewMatrix)
        matrixStack = copy
        //#endif
    }

    fun triggerRenderStart(ticks: Float) {
        deltaTicks = ticks
        if (matrixStack == null) return
        RenderUtils.withMatrix(matrixStack, ticks) {
            TriggerType.PRE_RENDER_WORLD.triggerAll(ticks)
        }
    }

    fun triggerRenderLast() {
        if (matrixStack == null) return
        RenderUtils.withMatrix(matrixStack, deltaTicks) {
            TriggerType.POST_RENDER_WORLD.triggerAll(deltaTicks)
        }
    }
}
