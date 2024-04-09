package com.github.thepurityofchaos.mixin;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.github.thepurityofchaos.SkyblockImprovements;

import com.github.thepurityofchaos.utils.render.IPLRender;




@Mixin(SkyblockImprovements.class)
public class RenderMetaMixin {
    
    //Inject into the mod's initializer
    @SuppressWarnings("resource")
    @Inject(at = @At("TAIL"), method = "onInitializeClient", remap = false)
    private void onInitializeClient(CallbackInfo info){

        //Item Pickup Log
        HudRenderCallback.EVENT.register((drawContext, tickDelta)->{
            IPLRender.render(drawContext,tickDelta);
        });

    }   
}
