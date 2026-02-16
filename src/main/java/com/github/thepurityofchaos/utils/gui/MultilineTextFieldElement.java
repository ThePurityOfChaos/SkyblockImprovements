package com.github.thepurityofchaos.utils.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.text.Text;

public class MultilineTextFieldElement extends TextFieldElement {

    public MultilineTextFieldElement(int defaultPosX, int defaultPosY, int sizeX, int sizeY, Text message) {
        super(defaultPosX, defaultPosY, sizeX, sizeY, message);
        MinecraftClient client = MinecraftClient.getInstance();
        textField = new MultilineTextFieldWidget(client.textRenderer, defaultPosX, defaultPosY, sizeX, sizeY, message);
    }
    @Override
    public void setText(String text){
        super.setText(text);
        ((MultilineTextFieldWidget) textField).updateLines();
    }
    @Override
    public boolean mouseClicked(Click click, boolean doubled){
        return ((MultilineTextFieldWidget)textField).mouseClicked(click, doubled);
    }
    
}
