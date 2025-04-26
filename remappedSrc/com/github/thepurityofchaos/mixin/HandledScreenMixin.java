package com.github.thepurityofchaos.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.github.thepurityofchaos.SkyblockImprovements;
import com.github.thepurityofchaos.utils.gui.TextFieldElement;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;

@Mixin(HandledScreen.class)
public class HandledScreenMixin {

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void onKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> info) {
        HandledScreen<?> screen = (HandledScreen<?>)(Object)this;
        if (SkyblockImprovements.onScreenKeyPressed((HandledScreen<?>) (Object) this, keyCode, scanCode, modifiers)) {
            info.setReturnValue(false);
        }
        if(screen.getFocused() instanceof TextFieldElement){
            MinecraftClient client = MinecraftClient.getInstance();
            if(client.options.inventoryKey.matchesKey(keyCode, scanCode)){
                info.setReturnValue(true);
            }
        }
    }
    
    
}
