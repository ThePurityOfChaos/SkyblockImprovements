package com.github.thepurityofchaos.utils.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class MenuElement extends GUIElement {
    private boolean renderMenu = false;
    private boolean record = false;
    private boolean rightJustified = false;
    private boolean goUp = false;
    private List<GUIElement> subElements = null;
    private GUIElement toggleElement = null;
    public MenuElement(int defaultPosX, int defaultPosY, int sizeX, int sizeY, PressAction action) {
        super(defaultPosX, defaultPosY, sizeX, sizeY, action);
        toggleElement = new GUIElement(defaultPosX+sizeX, defaultPosY, 12, 12, button -> {
                renderMenu = !renderMenu;
                toggleElement.setMessage(renderMenu?goUp?Text.of("▲"):Text.of("▼"):Text.of("▶"));
                toggleElement.setTooltip(renderMenu?Text.of("Close Settings"):Text.of("Open Settings"));
            }
        );
        toggleElement.setMessage(Text.of("▶"));
        this.subElements = new ArrayList<>();
    }
    
    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta){
        super.renderWidget(context, mouseX, mouseY, delta);
        determineLocations();
    }

    private void determineLocations(){
        int location = 0;
        toggleElement.setPosition(rightJustified?this.getX()-12:this.getX()+this.getWidth(), goUp?this.getY():this.getY()+this.getHeight()-12);
        for(GUIElement element : subElements){
                element.setPosition(rightJustified?this.getX()-element.getWidth():this.getX()+this.getWidth(),goUp?this.getY()-element.getHeight()-location:this.getY()+this.getHeight()+location);
            location+=element.getHeight();
        }
    }
    public GUIElement getToggler(){
        return toggleElement;
    }
    public List<GUIElement> getSubElements(){
        return subElements;
    }
    public boolean renderMenu(){
        return renderMenu;
    }
    public void addSubElement(GUIElement element){
        subElements.add(element);
    }
    public void disableMenu(){
        subElements.forEach(clickable -> {
            if(clickable instanceof TextFieldElement){
                ((TextFieldElement)clickable).click();
            }
        });
        renderMenu = false;
        toggleElement.setMessage(Text.of("▶"));
        toggleElement.setTooltip(Text.of("Open Settings"));
    }
    public boolean recorded(){
        return record;
    }
    public void record(boolean bool){
        record = bool;
    }
    public void redirectHorizontal(boolean b){
        rightJustified = b;
    }
    public void redirectVertical(boolean b){
        goUp = b;
        toggleElement.setMessage(renderMenu?goUp?Text.of("▲"):Text.of("▼"):Text.of("▶"));
    }


    
}
