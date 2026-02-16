package com.github.thepurityofchaos.mixin;

import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

@Mixin(RenderLayer.class)
public interface RenderLayerAccessor {
    
    @Accessor("ENTITY_CUTOUT")
    static Function<Identifier,RenderLayer> getGuiTexturedFunction(){
        throw new AssertionError("Texture not found, or something. -SBIMP");
    }
}
