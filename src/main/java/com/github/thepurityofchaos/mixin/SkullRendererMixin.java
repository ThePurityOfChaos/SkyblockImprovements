package com.github.thepurityofchaos.mixin;



import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


import com.github.thepurityofchaos.features.retexturer.RTRender;

import com.mojang.authlib.GameProfile;


import net.minecraft.block.SkullBlock;
import net.minecraft.client.render.RenderLayer;

import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;

// https://fabricmc.net/wiki/tutorial:mixin_examples 

@Mixin(HeadFeatureRenderer.class)
public class SkullRendererMixin {

    @Redirect(method = "render", at = @At(value="INVOKE", target = 
    "Lnet/minecraft/client/render/block/entity/SkullBlockEntityRenderer;getRenderLayer(Lnet/minecraft/block/SkullBlock$SkullType;Lcom/mojang/authlib/GameProfile;)Lnet/minecraft/client/render/RenderLayer;"
    ))
    public RenderLayer getModifiedRenderLayer(SkullBlock.SkullType type, @Nullable GameProfile profile){
        return RTRender.getModifiedRenderLayer(type, profile);
}
}
