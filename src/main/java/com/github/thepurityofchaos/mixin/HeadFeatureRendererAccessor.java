package com.github.thepurityofchaos.mixin;


import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.texture.PlayerSkinCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HeadFeatureRenderer.class)
public interface HeadFeatureRendererAccessor {

    @Accessor("skinCache")
    PlayerSkinCache getSkinCache();
}
