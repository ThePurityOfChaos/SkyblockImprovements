package com.github.thepurityofchaos.listeners;

import com.github.thepurityofchaos.features.economic.ChocolateFactory;
import com.github.thepurityofchaos.features.economic.Refinery;
import com.github.thepurityofchaos.storage.Sacks;
import com.github.thepurityofchaos.utils.processors.InventoryProcessor;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.util.Identifier;

public class ScreenListener {
    public static void init(){
        ScreenEvents.AFTER_INIT.register((client,screen,w,h) ->{
            if(screen instanceof GenericContainerScreen){
                //determine type
                //Sack
                if(screen.getTitle().getString().contains("Sack")&&!screen.getTitle().getString().contains("Sack of Sacks")){
                    ScreenEvents.afterTick(screen).register(currentScreen -> {
                        Sacks.processListToSacks(InventoryProcessor.processSlotsToList(((GenericContainerScreen)screen).getScreenHandler()));
                        Sacks.saveSettings();
                    });
                } 
                //Chocolate Factory
                if(screen.getTitle().getString().contains("Chocolate Factory")){
                    ScreenEvents.afterTick(screen).register(currentScreen -> {
                        ChocolateFactory.processList(InventoryProcessor.processSlotsToList(((GenericContainerScreen)screen).getScreenHandler()));
                    });
                    ScreenEvents.afterRender(screen).register((currentScreen, drawContext, mouseX, mouseY, delta)->{ 
                        Identifier texture = new Identifier("sbimp","textures/border.png");
                        int baseWidth = currentScreen.width/5;
                        int widthMod = currentScreen.width/10;
                        int baseHeight = currentScreen.height/2-currentScreen.height/4;
                        drawContext.drawTexture(texture,
                        //x,y
                        baseWidth-widthMod,baseHeight,
                        //u,v
                        0,0,
                        //width, height
                        currentScreen.width/5,32,
                        //texture width, height
                        currentScreen.width/5,32);
                        drawContext.drawCenteredTextWithShadow(client.textRenderer,ChocolateFactory.getChocolateCount(),baseWidth,baseHeight,1);
                        drawContext.drawCenteredTextWithShadow(client.textRenderer,ChocolateFactory.getCPS(),baseWidth,baseHeight+8,1);
                        drawContext.drawCenteredTextWithShadow(client.textRenderer,ChocolateFactory.mostEfficientUpgrade(),baseWidth,baseHeight+16,1);
                        drawContext.drawCenteredTextWithShadow(client.textRenderer,ChocolateFactory.getTimeToUpgrade(),baseWidth,baseHeight+24,1);
                    });

                }
                //Refinery
                if(screen.getTitle().getString().contains("Refine")){
                    ScreenEvents.afterTick(screen).register(currentScreen -> {
                        Refinery.processList(InventoryProcessor.processSlotsToList(((GenericContainerScreen)screen).getScreenHandler()));
                    });
                    
                }

            }
        });
    }
}
