package com.github.thepurityofchaos.storage.config;

import org.jetbrains.annotations.Nullable;

import com.github.thepurityofchaos.features.economic.BatFirework;
import com.github.thepurityofchaos.features.economic.Bingo;
import com.github.thepurityofchaos.features.economic.GenericProfit;
import com.github.thepurityofchaos.features.itempickuplog.ItemPickupLog;
import com.github.thepurityofchaos.features.packswapper.PackSwapper;
import com.github.thepurityofchaos.utils.gui.GUIScreen;

import net.minecraft.client.gui.screen.Screen;

/**
 * The Config Screen allows the player to modify the display location of any feature added to this screen.
 * 
 * <p> {@link #init(Nullable Screen)}: Creates the screen with every included feature's visual component. 
 * 
 * <p> {@link #modifyElementLocation(String element, int[] location)} : Sets the element's position to the first two integers in location. Expects an int array of size 2.
 */
public class ConfigScreen extends GUIScreen {
        
    public void init(@Nullable Screen parent){

        //Item Pickup Log
        this.addElement("ItemPickupLog",ItemPickupLog.getFeatureVisual());

        //Pack Swapper
        this.addElement("PackSwapper",PackSwapper.getFeatureVisual());

        //Bat Firework
        this.addElement("BatFirework",BatFirework.getFeatureVisual());

        //Generic Profit
        this.addElement("GenericProfit", GenericProfit.getFeatureVisual());

        //Bingo Tasks
        this.addElement("Bingo", Bingo.getFeatureVisual());

        //generic
        //this.addElement("name"),Feature.getFeatureVisual();
        
        super.init(parent);
    }
    /**
     * Takes in the name of an element and an integer array of at least size 2. Throws an IllegalArgumentException if the integer array contains only a single element.
     * @param element
     * @param location
     * @throws IllegalArgumentException
     */
    public void modifyElementLocation(String element, int[] location) throws IllegalArgumentException{
        this.getElement(element).setPosition(location[0], location[1]);
    }
    /**
     * Closes the Screen, saving the Config's changes.
     */
    public void close(){
        // only call saveSettings when the config screen closes, to minimize writing needed
        Config.saveSettings();
        super.close();
    }

    

}
