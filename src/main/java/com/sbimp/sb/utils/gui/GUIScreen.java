package com.sbimp.sb.utils.gui;

import java.util.ArrayList;
import java.util.List;

import com.sbimp.sb.features.itempickuplog.ItemPickupLog;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.gui.screen.*;
import net.minecraft.text.Text;


@Environment(EnvType.CLIENT)
public class GUIScreen extends Screen {
    private static List<GUIElement> allElements = new ArrayList<GUIElement>();
    public GUIScreen(){
        super(Text.literal("SkyblockImprovements"));
    }
    
    public boolean addElement(GUIElement e){
        return allElements.add(e);
    }

    @Override
    public void init(){
        for(GUIElement element : allElements){
            addDrawableChild(element.getWidget());
        }
    }
}
    

   
