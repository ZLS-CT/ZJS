package com.zephy.zjs.internal;

import net.minecraft.commands.SharedSuggestionProvider;

import java.util.HashMap;

public interface ClientCommandSource extends SharedSuggestionProvider {
    void setContextValue(String key, Object value);

    HashMap<String, Object> getContextValues();
}
