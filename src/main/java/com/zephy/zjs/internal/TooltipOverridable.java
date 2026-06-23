package com.zephy.zjs.internal;

import net.minecraft.network.chat.Component;

import java.util.List;

public interface TooltipOverridable {
    void zjs_setTooltip(List<Component> tooltip);
    void zjs_setShouldOverrideTooltip(boolean shouldOverrideTooltip);
}
