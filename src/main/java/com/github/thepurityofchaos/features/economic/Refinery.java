package com.github.thepurityofchaos.features.economic;

import java.util.ArrayList;
import java.util.List;


import com.github.thepurityofchaos.interfaces.ScreenInteractor;
import com.github.thepurityofchaos.utils.screen.ScreenUtils;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
/**
 * IN PROGRESS The Refinery Widget is intended to determine what the most profitable items are, given a relative time efficiency (If you can only play for 8 hours a day, for example, 33%)
 * 
 * <p> {@link #getMostProfitable()}: Returns the most profitable item, given what you have available.
 * 
 * <p> {@link #interact(Screen)}: Draws the most profitable item.
 */
public class Refinery implements ScreenInteractor{
    private static int timeEfficiency = 100;
    public static List<Text> getMostProfitable(){
        List<Text> textList = new ArrayList<>();
        textList.add(Text.of(""+timeEfficiency));
        return textList;
    }
    @SuppressWarnings("unused")
    public static void interact(Screen screen){
            ScreenEvents.afterRender(screen).register((currentScreen, drawContext, mouseX, mouseY, delta) -> {
                int x = currentScreen.width/3;
                int xOffset = 0;
                int y = currentScreen.height/3;
                int yOffset = 0;
                if(false)
                    ScreenUtils.draw(drawContext, Refinery.getMostProfitable(), x-xOffset, y+yOffset, -1, -1, 1000, -1, -1, -1);
            });        
    }
}
