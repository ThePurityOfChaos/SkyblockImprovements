package com.github.thepurityofchaos.features.retexturer;

import com.github.thepurityofchaos.utils.processors.InventoryProcessor;

import net.minecraft.client.gui.DrawContext;

public class RTRender {
    
    public static void render(DrawContext context, float tickDelta){
            HelmetRetexturer.getDefaultInstance().retextureHelm(InventoryProcessor.getHelmet());
    }

}
