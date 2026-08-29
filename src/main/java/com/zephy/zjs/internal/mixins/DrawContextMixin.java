package com.zephy.zjs.internal.mixins;

import com.zephy.zjs.api.triggers.TriggerType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGraphicsExtractor.class)
abstract class DrawContextMixin {
    @Inject(
        method = "item(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;III)V",
        at = @At("HEAD")
    )
    public void preRenderItem(
        @Nullable LivingEntity entity,
        @Nullable Level world,
        ItemStack itemStack,
        int x,
        int y,
        int seed,
        CallbackInfo callback
    ) {
        if (itemStack.isEmpty()) return;
        TriggerType.PRE_RENDER_ITEM.triggerAll(
            (GuiGraphicsExtractor)(Object)this,
            itemStack,
            x,
            y
        );
    }

    @Inject(
        method = "item(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;III)V",
        at = @At("RETURN")
    )
    public void postRenderItem(
        @Nullable LivingEntity entity,
        @Nullable Level world,
        ItemStack itemStack,
        int x,
        int y,
        int seed,
        CallbackInfo callback
    ) {
        if (itemStack.isEmpty()) return;
        TriggerType.POST_RENDER_ITEM.triggerAll(
            this,
            itemStack,
            x,
            y
        );
    }
}
