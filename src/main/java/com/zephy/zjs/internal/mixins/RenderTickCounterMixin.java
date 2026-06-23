package com.zephy.zjs.internal.mixins;

import com.zephy.zjs.internal.engine.ZEvents;
import net.minecraft.client.DeltaTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DeltaTracker.Timer.class)
public class RenderTickCounterMixin {
    @Inject(
        method = "advanceGameTime(J)I",
        at = @At(
            value = "HEAD"
        )
    )
    private void injectBeginRenderTick(long timeMillis, CallbackInfoReturnable<Integer> cir) {
        ZEvents.RENDER_TICK.invoker().invoke();
    }
}
