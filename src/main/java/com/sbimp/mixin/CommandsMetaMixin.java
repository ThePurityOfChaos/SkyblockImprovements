package com.sbimp.mixin;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

import net.minecraft.text.Text;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.sbimp.SkyblockImprovements;
import com.sbimp.config.ConfigScreen;

@Mixin(SkyblockImprovements.class)
public class CommandsMetaMixin {
    //Inject into the mod's initializer

    @Inject(at = @At("HEAD"), method = "onInitializeClient", remap = false)
    private void onInitializeClient(CallbackInfo info){
        //basic command to access SBI's User Interface 
        ConfigScreen screen = new ConfigScreen();
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(ClientCommandManager.literal("sbi")
        .executes(context -> {
              context.getSource().sendFeedback(Text.literal("Accessing SBI's Config!"));
              screen.init();
              return 1;
            }
        )));
        
    }
}
