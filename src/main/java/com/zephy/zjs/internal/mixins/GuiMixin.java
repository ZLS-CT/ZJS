package com.zephy.zjs.internal.mixins;

import com.zephy.zjs.internal.engine.ZEvents;
import gg.essential.universal.UMatrixStack;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.DeltaTracker;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC<=12111
//$$import net.minecraft.client.gui.GuiGraphics;
//#else
import net.minecraft.client.gui.GuiGraphicsExtractor;
//#endif

//#if MC>=26.2
import net.minecraft.client.gui.screens.Screen;
import com.zephy.zjs.api.triggers.TriggerType;
import com.llamalad7.mixinextras.sugar.Local;
//#endif

@Mixin(Gui.class)
public class GuiMixin {
    @Inject(
        //#if MC<=12111
        //$$method = "render",
        //#else
        method = "extractRenderState",
        //#endif
		at = @At(
            value = "TAIL"
		)
    )
    private void injectRenderOverlay(
        //#if MC<=12111
        //$$GuiGraphics drawContext,
        //#elseif MC<26.2
        //$$GuiGraphicsExtractor drawContext,
        //#endif
        DeltaTracker tickCounter,
        //#if MC>=26.2
        boolean shouldRenderLevel,
        boolean resourcesLoaded,
        //#endif
        CallbackInfo ci
        //#if MC>=26.2
        ,@Local GuiGraphicsExtractor drawContext
        //#endif
    ) {
        ZEvents.RENDER_HUD_OVERLAY.invoker().render(drawContext, new UMatrixStack(drawContext.pose()).toMC(), tickCounter.getGameTimeDeltaTicks());
    }

    //#if MC>=26.2
    @Inject(
        method = "setScreen",
        at = @At(
            value = "HEAD"
        )
    )
    private void injectScreenOpened(Screen screen, CallbackInfo ci) {
        if (screen != null) {
            TriggerType.GUI_OPENED.triggerAll(screen, ci);
        }
    }
    //#endif
}
