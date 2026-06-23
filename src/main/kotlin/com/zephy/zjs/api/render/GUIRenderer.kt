package com.zephy.zjs.api.render

import com.zephy.zjs.api.render.renderstates.GUIRenderState
import com.zephy.zjs.api.render.renderstates.GradientGUIRenderState
import com.zephy.zjs.api.render.renderstates.TexturedGUIRenderState
import com.zephy.zjs.engine.LogType
import com.zephy.zjs.engine.printToConsole
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.textures.AddressMode
import com.mojang.blaze3d.textures.FilterMode
import com.mojang.blaze3d.vertex.PoseStack
import gg.essential.universal.UMatrixStack
import net.minecraft.client.gui.render.TextureSetup
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.network.chat.Component
import org.joml.Matrix3x2f

//#if MC<=12111
//$$import net.minecraft.client.gui.GuiGraphics
//$$import net.minecraft.client.gui.render.state.GuiTextRenderState
//#else
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.renderer.state.gui.GuiTextRenderState
//#endif

object GUIRenderer : BaseGUIRenderer() {
    @JvmStatic
    var partialTicks = 0f
        internal set

    override fun drawString(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        text: String,
        xPosition: Float,
        yPosition: Float,
        color: Long,
        textScale: Float,
        renderBackground: Boolean,
        textShadow: Boolean,
        maxWidth: Int,
        zOffset: Float
    ) {
        drawText(drawContext, Component.literal(text), xPosition, yPosition, color, textScale, renderBackground, textShadow, maxWidth, zOffset)
    }

    @JvmStatic
    @JvmOverloads
    fun drawTextWithShadowRGBA(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        text: Component, xPosition: Float, yPosition: Float, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, textScale: Float = 1f, renderBackground: Boolean = false, maxWidth: Int = 512, zOffset: Float = 0f) {
        drawText(drawContext, text, xPosition, yPosition, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), textScale, renderBackground, true, maxWidth, zOffset)
    }

    @JvmStatic
    @JvmOverloads
    fun drawTextWithShadow(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        text: Component, xPosition: Float, yPosition: Float, color: Long = RenderUtils.colorized ?: RenderUtils.WHITE, textScale: Float = 1f, renderBackground: Boolean = false, maxWidth: Int = 512, zOffset: Float = 0f) {
        drawText(drawContext, text, xPosition, yPosition, color, textScale, renderBackground, true, maxWidth, zOffset)
    }

    @JvmStatic
    @JvmOverloads
    fun drawTextRGBA(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        text: Component, xPosition: Float, yPosition: Float, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, textScale: Float = 1f, renderBackground: Boolean = false, textShadow: Boolean = false, maxWidth: Int = 512, zOffset: Float = 0f) {
        drawText(drawContext, text, xPosition, yPosition, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), textScale, renderBackground, textShadow, maxWidth, zOffset)
    }

    @JvmStatic
    @JvmOverloads
    fun drawTextRGBAArray(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        text: Component, xPosition: Float, yPosition: Float, colorArray: IntArray = intArrayOf(255, 255, 255, 255), textScale: Float = 1f, renderBackground: Boolean = false, textShadow: Boolean = false, maxWidth: Int = 512, zOffset: Float = 0f) {
        drawText(drawContext, text, xPosition, yPosition, RenderUtils.RGBAColor.fromIntArray(colorArray).getLongRGBA(), textScale, renderBackground, textShadow, maxWidth, zOffset)
    }

    @JvmStatic
    @JvmOverloads
    fun drawText(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        text: Component,
        xPosition: Float,
        yPosition: Float,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        textScale: Float = 1f,
        renderBackground: Boolean = false,
        textShadow: Boolean = false,
        maxWidth: Int = 512,
        zOffset: Float = 0f, // Useless in 1.21.6+, text is drawn on top of all elements
    ) {
        val (a, r, g, b) = RenderUtils.RGBAColor.fromLongRGBA(color).getIntComponentsARGB()
        if (a == 0) return
        val safeAlpha = if (a in 1..3) 4 else a
        val safeColorIntARGB = RenderUtils.ARGBColor(r, g, b, safeAlpha).getIntARGB()
        val backgroundColor = if (renderBackground) {
            RenderUtils.ARGBColor(0, 0, 0, 150)
        } else {
            RenderUtils.ARGBColor(0, 0, 0, 0)
        }
        val backgroundColorInt = backgroundColor.getIntARGB()

        val textRenderer = RenderUtils.getTextRenderer()
        var currentY = 0f
        val lines = RenderUtils.splitText(text, maxWidth).lines

        val backgroundColorLong = backgroundColor.getLongRGBA()
        lines.forEach { line ->
            val matrix = Matrix3x2f(drawContext.pose())
            matrix.translate(xPosition, yPosition + currentY)
            matrix.scale(textScale, textScale)

            if (renderBackground) {
                val textWidth = textRenderer.width(line)
                drawRect(
                    drawContext,
                    xPosition - (1f * textScale),
                    yPosition + currentY - (1f * textScale),
                    (textWidth + 1f) * textScale,
                    (textRenderer.lineHeight + 1f) * textScale,
                    backgroundColorLong,
                    0f,
                )
            }

            val textState = GuiTextRenderState(
                textRenderer,
                line.visualOrderText,
                matrix,
                0,
                0,
                safeColorIntARGB,
                backgroundColorInt,
                textShadow,
                //#if MC>=12111
                true,
                //#endif
                drawContext.scissorStack.peek()
            )
            //#if MC<=12111
            //$$drawContext.guiRenderState.submitText(textState)
            //#else
            drawContext.guiRenderState.addText(textState)
            //#endif
            currentY += textRenderer.lineHeight * textScale
        }
    }

    override fun _drawLine(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        vertexList: List<Pair<Float, Float>>,
        color: Long,
        zOffset: Float,
    ) {
        val boundsList = vertexList.toList()
        //#if MC<=12111
        //$$drawContext.guiRenderState.submitGuiElement(
        //#else
        drawContext.guiRenderState.addGuiElement(
            //#endif
            GUIRenderState(
                RenderUtils.matrixStack.to3x2Joml(),
                vertexList,
                boundsList,
                zOffset,
                RenderUtils.RGBAColor.fromLongRGBA(color),
                RenderPipelines.QUADS().build(),
                drawContext.scissorStack.peek(),
            )
        )
    }

    override fun _drawRect(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        vertexList: List<Pair<Float, Float>>,
        color: Long,
        zOffset: Float,
    ) {
        val boundsList = vertexList.toList()
        //#if MC<=12111
        //$$drawContext.guiRenderState.submitGuiElement(
        //#else
        drawContext.guiRenderState.addGuiElement(
            //#endif
            GUIRenderState(
                RenderUtils.matrixStack.to3x2Joml(),
                vertexList,
                boundsList,
                zOffset,
                RenderUtils.RGBAColor.fromLongRGBA(color),
                RenderPipelines.QUADS().build(),
                drawContext.scissorStack.peek(),
            )
        )
    }

    override fun _drawRoundedRect(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float,
        vertexList: List<Pair<Float, Float>>,
        color: Long,
        zOffset: Float,
    ) {
        val boundsList = listOf(
            Pair(x1, y1),
            Pair(x2, y1),
            Pair(x2, y2),
            Pair(x1, y2)
        )

        //#if MC<=12111
        //$$drawContext.guiRenderState.submitGuiElement(
        //#else
        drawContext.guiRenderState.addGuiElement(
            //#endif
            GUIRenderState(
                RenderUtils.matrixStack.to3x2Joml(),
                vertexList,
                boundsList,
                zOffset,
                RenderUtils.RGBAColor.fromLongRGBA(color),
                RenderPipelines.QUADS().build(),
                drawContext.scissorStack.peek(),
            )
        )
    }

    override fun _drawGradient(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        vertexAndColorList: List<Triple<Float, Float, Long>>,
        zOffset: Float,
    ) {
        val boundsList = vertexAndColorList.map { (x, y, _) -> Pair(x, y) }
        //#if MC<=12111
        //$$drawContext.guiRenderState.submitGuiElement(
        //#else
        drawContext.guiRenderState.addGuiElement(
            //#endif
            GradientGUIRenderState(
                GUIRenderState(
                    RenderUtils.matrixStack.to3x2Joml(),
                    listOf(),
                    boundsList,
                    zOffset,
                    RenderUtils.RGBAColor(255, 255, 255, 255),
                    RenderPipelines.QUADS().build(),
                    drawContext.scissorStack.peek(),
                ),
                vertexAndColorList,
            )
        )
    }

    override fun _drawCircle(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        minX: Float,
        maxX: Float,
        minY: Float,
        maxY: Float,
        vertexList: List<Pair<Float, Float>>,
        color: Long,
        zOffset: Float,
    ) {
        val boundsList = listOf(
            Pair(minX, minY),
            Pair(maxX, minY),
            Pair(maxX, maxY),
            Pair(minX, maxY)
        )

        //#if MC<=12111
        //$$drawContext.guiRenderState.submitGuiElement(
        //#else
        drawContext.guiRenderState.addGuiElement(
            //#endif
            GUIRenderState(
                RenderUtils.matrixStack.to3x2Joml(),
                vertexList,
                boundsList,
                zOffset,
                RenderUtils.RGBAColor.fromLongRGBA(color),
                RenderPipelines.QUADS().build(),
                drawContext.scissorStack.peek(),
            )
        )
    }

    override fun _drawImage(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        image: Image,
        texture: DynamicTexture,
        vertexList: List<Pair<Float, Float>>,
        uvList: List<Pair<Float, Float>>,
        color: Long,
        zOffset: Float,
    ) {
        val sampler = RenderSystem.getSamplerCache().getSampler(
            AddressMode.CLAMP_TO_EDGE,
            AddressMode.CLAMP_TO_EDGE,
            FilterMode.LINEAR,
            FilterMode.NEAREST,
            false
        )
        val boundsList = vertexList.toList()
        //#if MC<=12111
        //$$drawContext.guiRenderState.submitGuiElement(
        //#else
        drawContext.guiRenderState.addGuiElement(
            //#endif
            TexturedGUIRenderState(
                GUIRenderState(
                    RenderUtils.matrixStack.to3x2Joml(),
                    vertexList,
                    boundsList,
                    zOffset,
                    RenderUtils.RGBAColor.fromLongRGBA(color),
                    RenderPipelines.TEXTURED_QUADS().build(),
                    drawContext.scissorStack.peek()
                ),
                TextureSetup.singleTexture(texture.textureView, sampler),
                uvList,
            )
        )
    }

    internal fun withMatrix(stack: PoseStack?, partialTicks: Float = GUIRenderer.partialTicks, block: () -> Unit) {
        GUIRenderer.partialTicks = partialTicks
        RenderUtils.matrixPushCounter = 0

        try {
            if (stack != null) RenderUtils.pushMatrix(UMatrixStack(stack))
            block()
        } finally {
            if (stack != null) RenderUtils.popMatrix()
        }

        if (RenderUtils.matrixPushCounter > 0) {
            "Warning: Render function missing a call to RenderUtils.popMatrix()".printToConsole(LogType.WARN)
        } else if (RenderUtils.matrixPushCounter < 0) {
            "Warning: Render function has too many calls to RenderUtils.popMatrix()".printToConsole(LogType.WARN)
        }
    }
}
