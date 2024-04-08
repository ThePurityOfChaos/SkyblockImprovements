package com.github.sbimp.utils.gui;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.gui.screen.*;
import net.minecraft.text.Text;


@Environment(EnvType.CLIENT)
public class GUIScreen extends Screen {
    private @Nullable Screen parent;
    private static Map<String,GUIElement> allElements = new HashMap<String,GUIElement>();
    public GUIScreen(){
        super(Text.literal("SkyblockImprovements"));
    }
    
    public boolean addElement(String name, GUIElement e){
        return (allElements.put(name,e))==null;
    }
    public GUIElement getElement(String name){
        return allElements.get(name);
    }
    
    public void init(@Nullable Screen parent){
        this.parent = parent;
        Collection<GUIElement> elements = allElements.values();
        for(GUIElement element: elements){
            addDrawableChild(element.getWidget());
        }
        super.init();
    }
    @Override
    public void close(){
        this.client.setScreen(this.parent);
    }
}
    

   
