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
import net.minecraft.component.type.ProfileComponent;

// https://fabricmc.net/wiki/tutorial:mixin_examples 
/**
 * 
 * MIXIN: Injects into HeadFeatureRenderer's render method.
 * 
 * <p> {@link #getModifiedRenderLayer(net.minecraft.block.SkullBlock.SkullType, GameProfile)}: Replaces the invocation of getRenderLayer() in HeadFeatureRenderer with a custom one.
 * 
 * <p> Annoying.
 */
@Mixin(HeadFeatureRenderer.class)
public class SkullRendererMixin {

    @Redirect(method = "render", at = @At(value="INVOKE", target = 
    "Lnet/minecraft/client/render/block/entity/SkullBlockEntityRenderer;getRenderLayer(Lnet/minecraft/block/SkullBlock$SkullType;Lnet/minecraft/component/type/ProfileComponent;)Lnet/minecraft/client/render/RenderLayer;"
    ))
    public RenderLayer getModifiedRenderLayer(SkullBlock.SkullType type, @Nullable ProfileComponent profile){
        return RTRender.getModifiedRenderLayer(type, profile);
}
}
