package com.github.thepurityofchaos.mixin;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.text.Text;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.github.thepurityofchaos.SkyblockImprovements;
import com.github.thepurityofchaos.config.ConfigScreen;

@Mixin(SkyblockImprovements.class)
public class CommandsMetaMixin {
    //Inject into the mod's initializer

    @SuppressWarnings("resource")
    @Inject(at = @At("HEAD"), method = "onInitializeClient", remap = false)
    private void onInitializeClient(CallbackInfo info){
        //basic command to access SBI's User Interface 
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(ClientCommandManager.literal("sbi")
        .executes(context -> {
            //https://docproject.github.io/fabricmc_fabric/net/fabricmc/fabric/api/client/screen/v1/ScreenEvents.html 
            //
            ConfigScreen screen = new ConfigScreen();
            screen.init(null);
            //this specific portion took me about 9 hours. It's ridiculous. I tried MinecraftClient.getInstance().setScreen(), context.getSource().getClient().setScreen(), 
            //looked anywhere and everywhere online. until finally THIS worked.
            context.getSource().getClient().send(() -> context.getSource().getClient().setScreen(screen)); 
            context.getSource().sendFeedback(Text.literal("Accessing SBI's Config!"));
            return 1;
            }
        )));
        
    }
}
