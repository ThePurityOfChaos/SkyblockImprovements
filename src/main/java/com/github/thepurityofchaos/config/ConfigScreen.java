package com.github.thepurityofchaos.config;

import org.jetbrains.annotations.Nullable;

import com.github.thepurityofchaos.features.itempickuplog.ItemPickupLog;
import com.github.thepurityofchaos.utils.gui.GUIElement;
import com.github.thepurityofchaos.utils.gui.GUIScreen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ConfigScreen extends GUIScreen {

    public void init(@Nullable Screen parent){
        //Restore Config Settings from config.json

        //done restoring config settings

        //Item Pickup Log
        this.addElement("ItemPickupLog",ItemPickupLog.getFeatureVisual());


        //generic
        //this.addElement("name"),Feature.getFeatureVisual();


        //Close Button
        GUIElement closeButton = new GUIElement(420,480,80,32,button ->{this.close();});
        closeButton.setMessage(Text.of("Close Screen"));
        this.addElement("Close Button",closeButton);

        
        super.init(parent);
    }
    
    public void close(){
        //Save settings to config.json

        //done saving config settings

        
        super.close();
    }

}
