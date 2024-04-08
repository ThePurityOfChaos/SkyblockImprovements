package com.github.thepurityofchaos.sbimp.utils.gui;

import net.minecraft.client.gui.widget.*;

import org.jetbrains.annotations.ApiStatus.OverrideOnly;

import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;

public class GUIElement {
    protected ButtonWidget widget;
    private int defaultPosX;
    private int defaultPosY;
    private boolean isDragging = false;

    public GUIElement(int defaultPosX, int defaultPosY, int sizeX, int sizeY, ButtonWidget.PressAction onPress){
        this.defaultPosX = defaultPosX;
        this.defaultPosY = defaultPosY;
        this.widget = ButtonWidget.builder(Text.literal(""), onPress==null?
        //default functionality, this should change to follow the mouse
        button -> {this.isDragging = true;}
        :onPress)
        //dimensions should have a transform of some kind based on the current GUI scale, but that is not implemented at this time.
        .dimensions(defaultPosX, defaultPosY, sizeX, sizeY)
        .tooltip(Tooltip.of(Text.literal("")))
        .build();
    }

    public void setMessage(Text message){
        this.widget.setMessage(message);
    }

    public void setTooltip(Text tooltip){
        this.widget.setTooltip(Tooltip.of(tooltip));
    }

    public void reset(){
        this.widget.setPosition(defaultPosX,defaultPosY);
    }

    public ButtonWidget getWidget(){
        return this.widget;
    }
    public boolean isDragging(){
        return isDragging;
    }
    public void notDragging(){
        isDragging = false;
    }
}
