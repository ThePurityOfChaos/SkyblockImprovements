package com.github.thepurityofchaos.features.economic;

import java.util.ArrayList;
import java.util.List;

import com.github.thepurityofchaos.interfaces.ScreenInteractor;
import com.github.thepurityofchaos.utils.NbtUtils;
import com.github.thepurityofchaos.utils.Utils;
import com.github.thepurityofchaos.utils.processors.InventoryProcessor;
import com.github.thepurityofchaos.utils.screen.ScreenUtils;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

/**
 * Shows the player the current reforge.
 * 
 * <p> {@link #processList(List)}: Determines the current item to reforge.
 * 
 * <p> {@link #getReforge()}: Returns the current Reforge.
 * 
 * <p> {@link #setReforge(Text)}: Sets the current Reforge to an input value, normally null.
 * 
 * <p> {@link #setColorCode(char)}: Sets the color code.
 * 
 * <p> {@link #interact(Screen)}: Displays the current Reforge.
 */
public class ReforgeHelper implements ScreenInteractor {
    //INCLUDED IN: None
    private static Text currentReforge = null;
    private static char colorCode = 'e';
    public static void processList(List<ItemStack> inventory){
        for(ItemStack item : inventory){
            getDataFromItemStack(item);
        }
    }
    private static void getDataFromItemStack(ItemStack item){
        Text name = NbtUtils.getNamefromItemStack(item);
        List<Text> lore = NbtUtils.getLorefromItemStack(item);
        //if the item is 'fake'
        if(lore==null){
            return;
        }
        if(!name.getString().contains("Reforge")&&!name.getString().contains("Close")){
            currentReforge = Text.of(Utils.getColorString(colorCode)+name.getString().split(" ")[0]);
        }
    }
    public static Text getReforge(){
        return currentReforge;
    }
    public static void setReforge(Text newReforge){
        currentReforge = newReforge;
    }
    public static void setColorCode(char c){
        colorCode = c;
    }
    public static void interact(Screen screen){
            //process inventory
            ScreenEvents.afterTick(screen).register(currentScreen -> {
                ReforgeHelper.processList(InventoryProcessor.processSlotsToList(((GenericContainerScreen)screen).getScreenHandler()));
            });
            //show current reforge
            ScreenEvents.afterRender(screen).register((currentScreen, drawContext, mouseX, mouseY, delta)->{ 
                int x = currentScreen.width/2;
                int xOffset = currentScreen.width/16;
                int y = currentScreen.height/2;
                int yOffset = currentScreen.height/9;
                Text temp = ReforgeHelper.getReforge();
                if(temp!=null){
                    List<Text> text = new ArrayList<>();
                    text.add(temp);
                    ScreenUtils.draw(drawContext, text, x-xOffset, y-yOffset, -1, -1, 1000, -1, -1, -1); 
                }
            });
            //remove reforge when screen closes
            ScreenEvents.remove(screen).register(currentScreen ->{
                ReforgeHelper.setReforge(null);
            });
                  
    }
}
