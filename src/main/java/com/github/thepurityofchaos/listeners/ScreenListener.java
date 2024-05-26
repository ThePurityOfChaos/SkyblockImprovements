package com.github.thepurityofchaos.listeners;

import java.util.ArrayList;
import java.util.List;

import com.github.thepurityofchaos.SkyblockImprovements;
import com.github.thepurityofchaos.config.EcoConfig;
import com.github.thepurityofchaos.features.economic.ChocolateFactory;
import com.github.thepurityofchaos.features.economic.Refinery;
import com.github.thepurityofchaos.features.economic.ReforgeHelper;
import com.github.thepurityofchaos.features.retexturer.HelmetRetexturer;
import com.github.thepurityofchaos.mixin.ChatScreenAccessor;
import com.github.thepurityofchaos.storage.Bazaar;
import com.github.thepurityofchaos.storage.Sacks;
import com.github.thepurityofchaos.utils.Utils;
import com.github.thepurityofchaos.utils.gui.GUIElement;
import com.github.thepurityofchaos.utils.math.MathSolutions;
import com.github.thepurityofchaos.utils.processors.InventoryProcessor;
import com.github.thepurityofchaos.utils.screen.ScreenUtils;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.screen.ChatScreen;

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ScreenListener {
    /*
     * INFO:
     * bring something to front
        drawContext.getMatrices().push();
        drawContext.getMatrices().translate(0, 0, large number);
        drawContext.drawSomething();
        drawContext.getMatrices().pop();
     * 
     * 
     */





    public static void init(){
        ScreenEvents.AFTER_INIT.register((client,screen,w,h) ->{
            SkyblockImprovements.push("SBI_ScreenListener");
            if(screen instanceof GenericContainerScreen){
                
                
                //determine type
                String screenName = screen.getTitle().getString();

                //Sack
                if(screenName.contains("Sack")&&!screenName.contains("Sack of Sacks")){
                    SkyblockImprovements.push("SBI_Sacks");
                    Sacks.newData();
                    ScreenEvents.afterTick(screen).register(currentScreen -> {
                        if(Sacks.ticksSinceData() < 5 ||!Sacks.dataArrived()){
                            Sacks.processList(InventoryProcessor.processSlotsToList(((GenericContainerScreen)screen).getScreenHandler()));
                            Sacks.saveSettings();
                            Sacks.tickData();
                        }
                    });
                    SkyblockImprovements.pop();
                } 

                //Bazaar, one of the only ones that has the ➜ symbol. I haven't seen any other name with this symbol, so for now it's likely fine.
                //If not, add in a secondary check for the different parts of the Bazaar, which may be more complex.
                if(screenName.contains("➜")){
                    ScreenEvents.afterTick(screen).register(currentScreen ->{
                        SkyblockImprovements.push("SBI_Bazaar");
                        Bazaar.processList(InventoryProcessor.processSlotsToList(((GenericContainerScreen)screen).getScreenHandler()));
                        SkyblockImprovements.pop();
                    });
                }

                //Chocolate Factory
                if(screenName.contains("Chocolate Factory")){
                    SkyblockImprovements.push("SBI_ChocolateFactory");
                    ScreenEvents.afterTick(screen).register(currentScreen -> {
                        ChocolateFactory.processList(InventoryProcessor.processSlotsToList(((GenericContainerScreen)screen).getScreenHandler()));
                    });
                    ScreenEvents.afterRender(screen).register((currentScreen, drawContext, mouseX, mouseY, delta)->{ 
                        Identifier texture = new Identifier("sbimp","textures/border.png");
                        int x = currentScreen.width/4;
                        int y = currentScreen.height/2;
                        int yOffset = currentScreen.height/4;
                        List<Text> texts = new ArrayList<>();
                        texts.add(ChocolateFactory.getChocolateCount());
                        texts.add(ChocolateFactory.getCPS());
                        texts.add(ChocolateFactory.mostEfficientUpgrade());
                        texts.add(ChocolateFactory.getTimeToUpgrade());
                        ScreenUtils.draw(drawContext, texts, texture, x, y-yOffset,-1,-1,1000,-1,-1,-1);
                    });
                    SkyblockImprovements.pop();
                }
                //Reforge Station
                if(screenName.contains("Reforge Item")){
                    //process inventory
                    SkyblockImprovements.push("SBI_ReforgeHelper");
                    ScreenEvents.afterTick(screen).register(currentScreen -> {
                        ReforgeHelper.processList(InventoryProcessor.processSlotsToList(((GenericContainerScreen)screen).getScreenHandler()));
                    });
                    //show current reforge
                    ScreenEvents.afterRender(screen).register((currentScreen, drawContext, mouseX, mouseY, delta)->{ 
                        int x = currentScreen.width/2;
                        int xOffset = currentScreen.width/16;
                        int y = currentScreen.height/2;
                        int yOffset = currentScreen.height/9;
                        List<Text> text = new ArrayList<>();
                        text.add(ReforgeHelper.getReforge());
                        ScreenUtils.draw(drawContext, text, x-xOffset, y-yOffset, -1, -1, 1000, -1, -1, -1); 

                    });
                    //remove reforge when screen closes
                    ScreenEvents.remove(screen).register(currentScreen ->{
                        ReforgeHelper.setReforge(Text.literal(""));
                    });
                    SkyblockImprovements.pop();
                }

                //Refinery
                if(screenName.contains("Refine Ores")){
                    SkyblockImprovements.push("SBI_Refinery");
                    ScreenEvents.afterTick(screen).register(currentScreen -> {
                        Refinery.processList(InventoryProcessor.processSlotsToList(((GenericContainerScreen)screen).getScreenHandler()));
                    });
                    SkyblockImprovements.pop();
                }

            }
            if(screen instanceof InventoryScreen){
                SkyblockImprovements.push("SBI_HelmetRetexturerInteractables");
                GUIElement refreshButton = new GUIElement(screen.width/3, screen.height/3, 16, 16, button ->{
                    HelmetRetexturer.refresh(InventoryProcessor.getHelmet());
                    return;
                });
                refreshButton.setMessage(Text.of("⟳"));
                Screens.getButtons(screen).add(refreshButton);
                SkyblockImprovements.pop();
            }
            if(screen instanceof ChatScreen){
                SkyblockImprovements.push("SBI_MathHelper");
                if(EcoConfig.doMath())
                    ScreenEvents.afterRender(screen).register((currentScreen, drawContext, mouseX, mouseY, delta) -> {
                        String currentMessage = ((ChatScreenAccessor)(ChatScreen)screen).getChatField().getText();
                        int x = currentScreen.width/2;
                        int xOffset = currentScreen.width/3+currentScreen.width/8;
                        int y = currentScreen.height/2;
                        int yOffset = currentScreen.height/3+currentScreen.height/9;
                        List<Text> text = new ArrayList<>();
                        double solution = MathSolutions.doMath(currentMessage);
                        if(solution!=-0.0){
                            text.add(Text.of(Utils.getColorString(EcoConfig.getColorCode())+Utils.addCommas(Utils.normalizeDouble(solution))));
                            ScreenUtils.draw(drawContext, text, x-xOffset, y+yOffset, -1, -1, 1000, -1, -1, -1);
                        }
                });
                SkyblockImprovements.pop();
            }
            SkyblockImprovements.pop();
        });
    }
}
