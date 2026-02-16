package com.github.thepurityofchaos.utils.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;

public class TextFieldElement extends GUIElement {
    protected TextFieldWidget textField = null;
    private PressAction onPress = null;
    public TextFieldElement(int defaultPosX, int defaultPosY, int sizeX, int sizeY, Text message) {
        super(defaultPosX,defaultPosY,sizeX,sizeY,button->{}, null);
        MinecraftClient client = MinecraftClient.getInstance();
        textField = new TextFieldWidget(client.textRenderer, sizeX, sizeY, message);
        textField.setPosition(defaultPosX,defaultPosY);
        textField.setMaxLength(Integer.MAX_VALUE);
    }
    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta){
        textField.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled){
        if(this.textField.isMouseOver(click.x(), click.y())){
            this.setFocused(true);
            this.textField.setFocused(true);
            if(this.onPress!=null){
                this.onPress.onPress(this);
            }
            return true;
        }
        this.setFocused(false);
        this.textField.setFocused(false);
        if(this.onPress!=null){
            this.onPress.onPress(this);
        }
        return false;
    }
    @Override
    public boolean charTyped(CharInput input){
        return this.textField.charTyped(input);
    }
    @Override
    public boolean keyPressed(KeyInput input){
        return this.textField.keyPressed(input);
    }
    public String getText(){
        return this.textField.getText();
    }
    public void setText(String text){
        this.textField.setText(text);
    }
    public void setPressAction(PressAction action){
        this.onPress = action;
    }
    @Override
    public void setPosition(int x, int y){
        super.setPosition(x,y);
        textField.setPosition(x,y);
    }
    public void click(){
        this.textField.setFocused(true);
        if(this.onPress!=null){
            this.onPress.onPress(this);
        }
        this.textField.setFocused(false);
    }
    public void setEditable(boolean b) {
        this.textField.setEditable(b);
    }
    public void setMaxLength(int newLength){
        this.textField.setMaxLength(newLength);
    }
    @Override
    public void setMessage(Text message){
        this.textField.setMessage(message);
    }
    @Override
    public Text getMessage(){
        return this.textField.getMessage();
    }
    
}
