package com.github.thepurityofchaos.mixin;


import com.github.thepurityofchaos.features.retexturer.RTRender;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.PlayerSkinCache;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerSkinCache.Entry.class)
public class PlayerSkinCacheMixin {
    @Shadow @Final private GameProfile profile;

    @Inject(method = "getRenderLayer", at = @At("HEAD"), cancellable = true)
    private void modifyRenderLayer(CallbackInfoReturnable<RenderLayer> cir) {
        RenderLayer r = RTRender.getModifiedRenderLayer(profile);
        if (r != null) cir.setReturnValue(r);
    }
}
