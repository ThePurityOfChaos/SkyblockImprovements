package com.github.mixin;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;

import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.github.SkyblockImprovements;
import com.github.sbimp.features.itempickuplog.ItemPickupLog;
import com.github.sbimp.utils.gui.GUIElement;
import com.mojang.blaze3d.systems.RenderSystem;

//https://fabric.moddedmc.wiki/rendering/ helps here

@Mixin(SkyblockImprovements.class)
public class RenderMetaMixin {
    
    //Inject into the mod's initializer
    @Inject(at = @At("TAIL"), method = "onInitializeClient", remap = false)
    private void onInitializeClient(CallbackInfo info){
        HudRenderCallback.EVENT.register((drawContext, tickDelta)->{
			Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            Matrix4f positionMatrix = drawContext.getMatrices().peek().getPositionMatrix();
            buffer.begin(DrawMode.QUADS,VertexFormats.POSITION);
            int [] pos = new int[4];
            if(ItemPickupLog.getFeatureVisual()!=null){
                ButtonWidget location = ItemPickupLog.getFeatureVisual().getWidget();
                pos[0] = location.getX();
                pos[1] = location.getY();
                pos[2] = location.getWidth();
                pos[3] = location.getHeight();
            }
        
            buffer.vertex(positionMatrix, pos[0], pos[1], 0).color(0f, 0f, 0f, 1f).next();
            buffer.vertex(positionMatrix, pos[0], pos[1]+pos[3], 0).color(0f, 0f, 0f, 1f).next();
            buffer.vertex(positionMatrix, pos[0]+pos[2], pos[1]+pos[3], 0).color(0f, 0f, 0f, 1f).next();
            buffer.vertex(positionMatrix, pos[0]+pos[2], pos[1], 0).color(0f, 0f, 0f, 1f).next();

            RenderSystem.setShader(GameRenderer::getPositionProgram);
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            tessellator.draw();
        });
    }   
}
