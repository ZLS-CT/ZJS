package com.zephy.zjs.internal.mixins;

//#if MC>=26.1
import net.minecraft.network.chat.TextColor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TextColor.class)
public interface TextColorAccessor {
    @Accessor("name")
    @Nullable String getName();
}
//#endif
