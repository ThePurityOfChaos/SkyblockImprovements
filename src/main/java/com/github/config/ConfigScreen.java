package com.github.config;

import org.jetbrains.annotations.Nullable;

import com.github.sbimp.features.itempickuplog.ItemPickupLog;
import com.github.sbimp.utils.gui.GUIElement;
import com.github.sbimp.utils.gui.GUIScreen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ConfigScreen extends GUIScreen {

    public void init(@Nullable Screen parent){
        //Item Pickup Log
        this.addElement("ItemPickupLog",ItemPickupLog.getFeatureVisual());
        this.getElement("ItemPickupLog").setMessage(Text.of("Item Pickup Log"));

        //this.addElement("name"),Feature.getFeatureVisual();


        //Close Button
        GUIElement closeButton = new GUIElement(420,480,80,32,button ->{this.close();});
        closeButton.setMessage(Text.of("Close Screen"));
        this.addElement("Close Button",closeButton);

        //do the rest
        super.init(parent);
    }

}
