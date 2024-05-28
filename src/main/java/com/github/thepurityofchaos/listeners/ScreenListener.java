package com.github.thepurityofchaos.listeners;

import java.util.ArrayList;
import java.util.List;

import com.github.thepurityofchaos.SkyblockImprovements;
import com.github.thepurityofchaos.config.EcoConfig;
import com.github.thepurityofchaos.features.economic.ChocolateFactory;
import com.github.thepurityofchaos.features.economic.Refinery;
import com.github.thepurityofchaos.features.economic.ReforgeHelper;
import com.github.thepurityofchaos.features.retexturer.Retexturer;
import com.github.thepurityofchaos.mixin.ChatScreenAccessor;
import com.github.thepurityofchaos.storage.Bazaar;
import com.github.thepurityofchaos.storage.Sacks;
import com.github.thepurityofchaos.utils.Utils;
import com.github.thepurityofchaos.utils.math.MathSolutions;
import com.github.thepurityofchaos.utils.screen.ScreenUtils;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.ChatScreen;

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.text.Text;

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
                    Sacks.interact(screen);
                    SkyblockImprovements.pop();
                } 

                //Bazaar, one of the only ones that has the ➜ symbol. I haven't seen any other name with this symbol, so for now it's likely fine.
                //If not, add in a secondary check for the different parts of the Bazaar, which may be more complex.
                if(screenName.contains("➜")){
                    SkyblockImprovements.push("SBI_Bazaar");
                    Bazaar.interact(screen);
                    SkyblockImprovements.pop();
                }

                //Chocolate Factory
                if(screenName.contains("Chocolate Factory")){
                    SkyblockImprovements.push("SBI_ChocolateFactory");
                    ChocolateFactory.interact(screen);
                    SkyblockImprovements.pop();
                }
                //Reforge Station
                if(screenName.contains("Reforge Item")){
                    SkyblockImprovements.push("SBI_ReforgeHelper");
                    ReforgeHelper.interact(screen);
                    SkyblockImprovements.pop();
                }

                //Refinery
                if(screenName.contains("Refine")){
                    SkyblockImprovements.push("SBI_Refinery");
                    Refinery.interact(screen);
                    SkyblockImprovements.pop();
                }

            }
            if(screen instanceof InventoryScreen){
                SkyblockImprovements.push("SBI_HelmetRetexturerInteractables");
                Retexturer.interact(screen);
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
