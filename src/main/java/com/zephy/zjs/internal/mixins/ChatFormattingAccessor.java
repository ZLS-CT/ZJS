package com.zephy.zjs.internal.mixins;

//#if MC>=26.1
import net.minecraft.ChatFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChatFormatting.class)
public interface ChatFormattingAccessor {
    @Accessor("code")
    char getCode();
}
//#endif
