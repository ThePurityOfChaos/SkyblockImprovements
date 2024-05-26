package com.github.thepurityofchaos.mixin;

import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;

@Mixin(ClientPlayNetworkHandler.class)
public interface TabListAccessor {
    
    @Accessor("listedPlayerListEntries")
    Set<PlayerListEntry> getListedPlayerList();

}
