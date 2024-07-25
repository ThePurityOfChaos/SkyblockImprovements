package com.github.thepurityofchaos.utils.gui;


import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.*;
import net.minecraft.text.Text;

/**
 * A Screen specifically intended for GUIElements.
 * <p> {@link #addElement(String, GUIElement)}: Adds an element to the element map.
 * <p> {@link #getElement(String)}: Gets the element with that name.
 * <p> {@link #init()}: Initializes the Screen with a close button.
 * <p> {@link #close()}: Closes the screen and opens its parent.
 * <p> {@link #mouseDragged(double, double, int, double, double)}: Allows for moving elements around.
 * <p> {@link #getParent()}: Returns the Screen's parent.
 */
@Environment(EnvType.CLIENT)
public class GUIScreen extends Screen {
    private @Nullable Screen parent;
    protected Map<String,GUIElement> allElements = new HashMap<String,GUIElement>();
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
        this.init();
    }
    @Override
    public void init(){
        //Close Button
        this.addElement("CloseButton",new GUIElement(this.width/2-40,this.height-32,80,32,button ->{this.close();}));
        this.getElement("CloseButton").setMessage(Text.of("Go Back"));
        allElements.forEach((key,element)->{
            addDrawableChild(element);
        });
        super.init();
    }
    @Override
    public void close(){
        allElements.forEach((key,element)->{
            element.notDragging();
        });
        client.setScreen(this.parent);
    }
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY){
        allElements.forEach((key,element) -> {
            if(element.isDragging()){
                element.setPosition((int)(mouseX - element.getWidth()/2), (int)(mouseY - element.getHeight()/2));
            }
        });
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
    public Screen getParent(){
        return parent;
    }
}
    

   
