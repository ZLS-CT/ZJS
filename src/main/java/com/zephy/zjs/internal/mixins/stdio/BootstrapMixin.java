package com.zephy.zjs.internal.mixins.stdio;

import com.zephy.zjs.internal.launch.ZMixinPlugin;
import net.minecraft.server.Bootstrap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Bootstrap.class)
public class BootstrapMixin {
    @Inject(
        method = "wrapStreams",
        at = @At(
            value = "HEAD"
        )
    )
    private static void injectSetOutputStreams(CallbackInfo ci) {
        // MC will re-wrap the output streams, so we restore them to their original state
        // so they don't end up double-wrapped.
        ZMixinPlugin.restoreOutputStreams();
    }
}
