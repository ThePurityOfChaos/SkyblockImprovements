package com.github.thepurityofchaos.utils.screen;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;

public class CustomChatScreen extends ChatScreen {

    public CustomChatScreen(String originalChatText) {
        super(originalChatText);
    }
    public TextFieldWidget getChatField(){
        return this.chatField;
    }
    
}
