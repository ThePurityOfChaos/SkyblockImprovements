package com.github.thepurityofchaos.utils.gui;

import net.minecraft.client.gui.widget.*;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;

public class GUIElement extends ButtonWidget {
    private int defaultPosX;
    private int defaultPosY;
    private boolean isDragging = false;
    private boolean defaultBehavior = false;

    public GUIElement(int defaultPosX, int defaultPosY, int sizeX, int sizeY, ButtonWidget.PressAction onPress){
        super(defaultPosX, defaultPosY, sizeX, sizeY, Text.of(""), onPress==null?button -> {}:onPress, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
        this.defaultPosX = defaultPosX;
        this.defaultPosY = defaultPosY;
        if(onPress == null){
            defaultBehavior = true;
        }
        
    }
    @Override
    public void onPress(){
        if(defaultBehavior){
            this.isDragging = true;
        }
        super.onPress();
    }

    @Override
    public void onRelease(double mouseX, double mouseY){
        this.isDragging = false;
        super.onRelease(mouseX, mouseY);
    }

    public void setTooltip(Text tooltip){
        this.setTooltip(Tooltip.of(tooltip));
    }

    public void reset(){
        this.setPosition(defaultPosX,defaultPosY);
    }

    public boolean isDragging(){
        return isDragging;
    }
    public void notDragging(){
        isDragging = false;
    }
}
