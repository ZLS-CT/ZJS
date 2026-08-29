package com.zephy.zjs.internal.engine.module

import com.zephy.zjs.api.message.ChatLib
import com.zephy.zjs.api.render.Text
import java.io.File

import net.minecraft.client.gui.GuiGraphicsExtractor
import org.zephy.zrenderlib.RenderUtils

class Module(val name: String, var metadata: ModuleMetadata, val folder: File) {
    var requiredBy = mutableSetOf<String>()

    private val gui = object {
        var collapsed = true
        var x = 0
        var y = 0
        var description = Text(metadata.description ?: "No description provided in the metadata")
    }

    fun draw(
        drawContext: GuiGraphicsExtractor,
        x: Int,
        y: Int,
        width: Int
    ): Int {
        gui.x = x
        gui.y = y

        drawContext.pose().pushMatrix()

        drawContext.fill(x, y, x + width, y + 13, 0xaa000000.toInt())
        drawContext.text(
            RenderUtils.getTextRenderer(),
            metadata.name ?: name,
            x + 3, y + 3, -1
        )

        return if (gui.collapsed) {
            drawContext.pose().pushMatrix()
            drawContext.pose().translate(x + width - 5f, y + 8f)
            drawContext.pose().rotate(Math.PI.toFloat())
            drawContext.text(RenderUtils.getTextRenderer(), "^", 0, 0, -1, false)
            drawContext.pose().popMatrix()
            16
        } else {
            gui.description.setMaxWidth(width - 5)

            drawContext.fill(x, y + 13, x + width, y + (gui.description.getHeight() + 25), 0x50000000)
            drawContext.text(RenderUtils.getTextRenderer(), "^", x + width - 10, y + 5, -1, false)

            gui.description.draw(drawContext, x + 3, y + 15)

            if (metadata.version != null) {
                drawContext.text(
                    RenderUtils.getTextRenderer(),
                    ChatLib.addColor("&8v${metadata.version}"),
                    x + width - RenderUtils.getStringWidth(ChatLib.addColor("&8v${metadata.version}")),
                    y + gui.description.getHeight() + 15,
                    -1
                )
            }

            drawContext.text(
                RenderUtils.getTextRenderer(),
                ChatLib.addColor(
                    if (metadata.isRequired && requiredBy.isNotEmpty()) {
                        "&8required by $requiredBy"
                    } else {
                        "&4[delete]"
                    },
                ),
                x + 3,
                y + gui.description.getHeight() + 15,
                -1
            )

            drawContext.pose().popMatrix()
            gui.description.getHeight() + 27
        }
    }

    override fun toString() = "Module{name=$name,version=${metadata.version}}"
}
