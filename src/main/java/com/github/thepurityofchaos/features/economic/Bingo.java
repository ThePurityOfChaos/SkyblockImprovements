package com.github.thepurityofchaos.features.economic;

import java.util.ArrayList;
import java.util.List;

import com.github.thepurityofchaos.interfaces.Feature;
import com.github.thepurityofchaos.interfaces.MessageProcessor;
import com.github.thepurityofchaos.interfaces.ScreenInteractor;
import com.github.thepurityofchaos.storage.config.EcoConfig;
import com.github.thepurityofchaos.utils.NbtUtils;
import com.github.thepurityofchaos.utils.Utils;
import com.github.thepurityofchaos.utils.gui.GUIElement;
import com.github.thepurityofchaos.utils.processors.InventoryProcessor;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

/**
 * Task recorder for the Bingo Event.
 * 
 * <p> {@link #init()}: Initializes the visual element.
 * 
 * <p> {@link #interact(Screen)}: Processes the current screen's data to get the tasks. Requires a GenericContainerScreen.
 * 
 * <p> {@link #isMyMessage(Text)}: Checks whether or not a message is owned by this, to delete completed goals.
 * <p>
 * Getters:
 * <p> {@link #getTasks()}: Returns task list.
 * <p> {@link #getFeatureVisual()}: Returns visual component.
 * <p> {@link #getFeatureEnabled()}: Returns isEnabled.
 * <p> {@link #toggleFeature()}: Toggles visibility.
 * <p> {@link #toggleCommunity()}: Toggles whether to include Community Goals.
 * <p> {@link #setTasks(List)}: Sets the task list to the input.
 * 
 */
public class Bingo implements MessageProcessor,Feature,ScreenInteractor{
    //INCLUDED IN: EcoConfig -> buttons
    private static GUIElement bingoVisual = null;
    //INCLUDED IN: EcoConfig -> advanced
    private static List<Text> tasks = null;
    private static boolean isEnabled = false;
    private static boolean showCommunity = false;

    private static final String[] incorrectStrings = {"Row","Column", "Diagonal", "Item Transfer","Bingo Shop","Go Back","Close"}; 

    public static void init(){
        bingoVisual = new GUIElement(0, 0, 128, 32, null);
    }

    public static void interact(Screen screen){
            ScreenEvents.afterTick(screen).register(currentScreen -> {
                Bingo.processList(InventoryProcessor.processSlotsToList(((GenericContainerScreen)screen).getScreenHandler()));
            }); 
    }
    /**
     * Gets the goals from a List of ItemStacks.
     * @param list
     */
    private static void processList(List<ItemStack> list) {
        tasks = new ArrayList<>();
        for(ItemStack item : list){
            String name = NbtUtils.getNamefromItemStack(item).getString();
            if(Utils.containsAny(name, incorrectStrings)) continue;
            List<Text> lore = NbtUtils.getLorefromItemStack(item);
            if(lore==null||lore.get(lore.size()-1).getString().contains("GOAL REACHED")) continue;
            if(!showCommunity&&lore.get(0).getString().contains("Community")) continue;
            MutableText temp = MutableText.of(Text.of(Utils.getColorString(EcoConfig.getColorCode())+name+(lore.get(0).getString().contains("Community")?" ("+Utils.getColorString('8')+"Community"+Utils.getColorString(EcoConfig.getColorCode())+"):":":")).getContent());
            boolean record = false;
            for(Text text : lore){
                if(text.getString().strip().equals("")){
                    record = !record;
                    if(record==false) break;
                }
                if(record){ 
                    temp.append(text);
                    temp.append(Text.of(" "));
                }  
            } 
            tasks.add(temp);
        }
    }
    public static boolean isMyMessage(Text message){
        if(message.getString().contains("BINGO GOAL COMPLETE!")){
            for(Text task : tasks){
                if(task.getString().contains(message.getString().replace("BINGO GOAL COMPLETE!","").strip())){
                    tasks.remove(task);
                    break;
                }
            }
            return true;
        }
        return false;
    }

    //getters and toggles
    public static List<Text> getTasks(){ return tasks;}
    public static GUIElement getFeatureVisual(){return bingoVisual;}
    public static boolean getFeatureEnabled(){return isEnabled;}
    public static boolean showCommunity(){return showCommunity;}
    public static void toggleCommunity(){showCommunity = !showCommunity;}
    public static void toggleFeature(){isEnabled = !isEnabled;}
    public static void setTasks(List<Text> newTasks) {tasks = newTasks;}

}
