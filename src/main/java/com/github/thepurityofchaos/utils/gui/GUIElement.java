package com.github.thepurityofchaos.utils.gui;

import net.minecraft.client.gui.widget.*;

import org.jetbrains.annotations.NotNull;

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
    private final PressAction rightClickAction;

    public GUIElement(int defaultPosX, int defaultPosY, int sizeX, int sizeY, ButtonWidget.PressAction onPress){
        super(defaultPosX, defaultPosY, sizeX, sizeY, Text.of(""), onPress==null?button -> {}:onPress, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
        this.defaultPosX = defaultPosX;
        this.defaultPosY = defaultPosY;
        if(onPress == null){
            defaultBehavior = true;
        }
        rightClickAction = null;
    }
    public GUIElement(int defaultPosX, int defaultPosY, int sizeX, int sizeY, ButtonWidget.PressAction onPress, ButtonWidget.PressAction rightClickAction){
        super(defaultPosX, defaultPosY, sizeX, sizeY, Text.of(""), onPress==null?button -> {}:onPress, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
        this.defaultPosX = defaultPosX;
        this.defaultPosY = defaultPosY;
        if(onPress == null){
            defaultBehavior = true;
        }
        this.rightClickAction = rightClickAction;
    }
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isValidClickButton(button) && this.clicked(mouseX, mouseY)) {
            if (this.active) {
                if (button == 0) { // Left click
                    this.onPress();
                } else if (button == 1) { // Right click
                    this.onRightClick();
                }
                return true;
            }
        }
        return false;
    }
    @Override
    protected boolean isValidClickButton(int button){
        return button == 0 || button == 1;
    }

    protected void onRightClick() {
        if (this.rightClickAction != null) {
            this.rightClickAction.onPress(this);
        }else{ onPress(); }
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

    public void setTooltip(@NotNull Text tooltip){
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
