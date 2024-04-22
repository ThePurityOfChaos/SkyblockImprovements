package com.github.thepurityofchaos.config;

import org.jetbrains.annotations.Nullable;

import com.github.thepurityofchaos.features.itempickuplog.ItemPickupLog;
import com.github.thepurityofchaos.features.packswapper.PackSwapper;
import com.github.thepurityofchaos.utils.gui.GUIScreen;

import net.minecraft.client.gui.screen.Screen;

/*
 * Config for the mod's systems. 
 */
public class ConfigScreen extends GUIScreen {
        
    public void init(@Nullable Screen parent){

        //Item Pickup Log
        this.addElement("ItemPickupLog",ItemPickupLog.getFeatureVisual());

        //Pack Swapper
        this.addElement("PackSwapper",PackSwapper.getFeatureVisual());

        //generic
        //this.addElement("name"),Feature.getFeatureVisual();


        
        
        super.init(parent);
    }
    public void modifyElementLocation(String element, int[] location){
        this.getElement(element).setPosition(location[0], location[1]);
    }

    public void close(){
        // only call saveSettings when the config screen closes, to minimize writing needed
        Config.saveSettings();
        super.close();
    }

    

}
