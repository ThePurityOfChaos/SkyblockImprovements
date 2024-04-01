package com.sbimp.sb.utils.gui;

import net.minecraft.client.gui.widget.*;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;

public class GUIElement {
    protected ButtonWidget widget;
    private int defaultPosX;
    private int defaultPosY;
    
    public GUIElement(int defaultPosX, int defaultPosY, int sizeX, int sizeY){
        this.defaultPosX = defaultPosX;
        this.defaultPosY = defaultPosY;
        this.widget = ButtonWidget.builder(Text.literal(""), button -> {
            //should be cursor pos
        this.widget.setPosition(100,100);
    })
        .dimensions(0, 0, sizeX, sizeY)
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
}
