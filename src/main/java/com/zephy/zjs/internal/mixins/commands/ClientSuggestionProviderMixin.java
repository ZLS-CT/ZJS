package com.zephy.zjs.internal.mixins.commands;

import com.zephy.zjs.internal.ClientCommandSource;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashMap;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(ClientSuggestionProvider.class)
public abstract class ClientSuggestionProviderMixin implements ClientCommandSource {
    @Unique
    private final HashMap<String, Object> contextValues = new HashMap<>();

    @Override
    public void setContextValue(String key, Object value) {
        contextValues.put(key, value);
    }

    @Override
    public HashMap<String, Object> getContextValues() {
        return contextValues;
    }
}
