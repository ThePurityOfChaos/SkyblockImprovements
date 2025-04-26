package com.github.thepurityofchaos.features.economic;

import java.util.ArrayList;
import java.util.List;

import com.github.thepurityofchaos.abstract_interfaces.Feature;
import com.github.thepurityofchaos.abstract_interfaces.ScreenInteractor;
import com.github.thepurityofchaos.storage.config.EcoConfig;
import com.github.thepurityofchaos.utils.ComponentUtils;
import com.github.thepurityofchaos.utils.Utils;
import com.github.thepurityofchaos.utils.gui.GUIElement;
import com.github.thepurityofchaos.utils.processors.InventoryProcessor;
import com.github.thepurityofchaos.utils.screen.ScreenUtils;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;

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
public class ReforgeHelper extends Feature implements ScreenInteractor {
    //INCLUDED IN: None
    private Text currentReforge = null;
    private char colorCode = 'e';

    private static ReforgeHelper instance = new ReforgeHelper();
    
    public void processList(List<ItemStack> inventory){
        if(inventory==null) return;
        for(ItemStack item : inventory){
            getDataFromItemStack(item);
        }
    }
    public void init() {
        visual = new GUIElement(128, 128, 0, 0, null);
    }
    private void getDataFromItemStack(ItemStack item){
        Text name = ComponentUtils.getNamefromItemStack(item);
        List<Text> lore = ComponentUtils.getLorefromItemStack(item);
        //if the item is 'fake'
        if(lore==null){
            return;
        }
        if(!name.getString().contains("Reforge")&&!Utils.ignorable(name.getString())){
            currentReforge = Text.of(Utils.getColorString(colorCode)+name.getString().split(" ")[0]);
        }
    }
    public Text getReforge(){
        return currentReforge;
    }
    public void setReforge(Text newReforge){
        currentReforge = newReforge;
    }
    public void setColorCode(char c){
        colorCode = c;
    }
    public void interact(Screen screen){
            //process inventory
            ReforgeHelper rh = ReforgeHelper.getInstance();
            Screens.getButtons(screen).add(visual);
            ScreenEvents.afterTick(screen).register(currentScreen -> {
                rh.processList(InventoryProcessor.processSlotsToList(((GenericContainerScreen)screen).getScreenHandler()));
            });
            //show current reforge
            ScreenEvents.afterRender(screen).register((currentScreen, drawContext, mouseX, mouseY, delta)->{ 
                Text temp = rh.getReforge();
                if(temp!=null){
                    List<Text> text = new ArrayList<>();
                    text.add(temp);
                    Pair<Integer,Integer> p = ScreenUtils.draw(drawContext, text, visual.getX()+3, visual.getY()+3, -1, -1, 1000, -1, -1, -1, false);
                    visual.setDimensions(p.getLeft(), p.getRight()); 
                }
            });
            //remove reforge when screen closes
            ScreenEvents.remove(screen).register(currentScreen ->{
                rh.setReforge(null);
                EcoConfig.saveSettings();
            });
                  
    }
    public static ReforgeHelper getInstance() {
        return instance;
    }
    
}
