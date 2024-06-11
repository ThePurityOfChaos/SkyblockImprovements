package com.github.thepurityofchaos.utils.gui;

import net.minecraft.client.gui.widget.*;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;
/**
 * One of the main elements for the system.
 * <p> {@link #GUIElement(int, int, int, int, PressAction)}: Creates a new GUIElement with a default position x,y, a size x,y, and a PressAction. If PressAction is null, pressing will drag the button.
 * <p> {@link #onPress()}: Performs the press action.
 * <p> {@link #onRelease(double, double)}: Stops dragging.
 * <p> {@link #getCenteredX()}: Returns the x position of the center of the button.
 * <p> {@link #getCenteredY()}: Returns the y position of the center of the button.
 * <p> {@link #setTooltip(Text)}: Sets the current tooltip to the text.
 * <p> {@link #reset()}: Changes the position to the default.
 * <p> {@link #isDragging()}: Is the button being dragged?
 * <p> {@link #notDragging()}: isDragging = false.
 */
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
    public int getCenteredX(){
        return this.getX()+this.getWidth()/2;
    }
    public int getCenteredY(){
        return this.getY()+this.getHeight()/2;
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
