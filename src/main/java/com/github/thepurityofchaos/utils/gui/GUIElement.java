package com.github.thepurityofchaos.utils.gui;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.input.AbstractInput;

import net.minecraft.client.input.MouseInput;
import org.jetbrains.annotations.NotNull;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * One of the main elements for the system.
 * <p> {@link #GUIElement(int, int, int, int, PressAction, PressAction)}: Creates a new GUIElement with a default position x,y, a size x,y, and a PressAction. If PressAction is null, pressing will drag the button.
 * <p> {@link #onPress(AbstractInput)}: Performs the press action.
 * <p> {@link #mouseReleased(Click)}: Stops dragging.
 * <p> {@link #getCenteredX()}: Returns the x position of the center of the button.
 * <p> {@link #getCenteredY()}: Returns the y position of the center of the button.
 * <p> {@link #setTooltip(Text)}: Sets the current tooltip to the text.
 * <p> {@link #reset()}: Changes the position to the default.
 * <p> {@link #isDragging()}: Is the button being dragged?
 * <p> {@link #notDragging()}: isDragging = false.
 */
public class GUIElement extends ButtonWidget {

    private final int defaultPosX, defaultPosY;
    private boolean isDragging, defaultBehavior = false;
    private final AtomicReference<PressAction> leftClickAction = new AtomicReference<>(b -> {});
    private final PressAction rightClickAction;

    public GUIElement(int defaultPosX, int defaultPosY, int sizeX, int sizeY, ButtonWidget.PressAction leftClickAction, @Nullable ButtonWidget.PressAction rightClickAction){
        super(defaultPosX, defaultPosY, sizeX, sizeY, Text.of(""), button -> {
            GUIElement me = (GUIElement) button;
            if(me.defaultBehavior) me.isDragging = !me.isDragging;
            me.leftClickAction.get().onPress(me);
        }, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
        this.defaultPosX = defaultPosX;
        this.defaultPosY = defaultPosY;
        defaultBehavior = (leftClickAction==null);
        if(leftClickAction!=null) this.leftClickAction.set(leftClickAction);
        this.rightClickAction = rightClickAction;
    }
    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (this.isValidClickButton(click.buttonInfo()) && this.isMouseOver(click.x(), click.y())) {
            if (this.active) {
                if (click.button() == 0) { // Left click
                    this.onPress(click);
                } else if (click.button() == 1) { // Right click
                    this.onRightClick();
                }
                return true;
            }
        }
        return false;
    }
    @Override
    public boolean mouseReleased(Click click){
        if (this.isValidClickButton(click.buttonInfo())) {
            this.isDragging = false;
            this.onRelease(click);
            return true;
         } else {
            return false;
         }
    }
    @Override
    protected boolean isValidClickButton(MouseInput input){
        return input.button() == 0 || input.button() == 1;
    }

    protected void onRightClick() {
        if (this.rightClickAction != null) {
            this.rightClickAction.onPress(this);
        }else{ onPress(null); }
    }
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta){
        if(this.isDragging){
            this.setPosition((int) (mouseX - this.getWidth() / 2), (int) (mouseY - this.getHeight() / 2));
        }
        super.renderWidget(context, mouseX, mouseY, delta);
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
    public void setMessage(String s){
        this.setMessage(Text.of(s));
    }
    public void setOnPress(PressAction action) {
        leftClickAction.set(action!=null ? action : button -> {});
    }
    public void changeDefaultBehavior(boolean newBehavior){
        defaultBehavior = newBehavior;
    }
}
