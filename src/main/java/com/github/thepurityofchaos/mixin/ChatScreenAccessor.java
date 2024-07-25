package com.github.thepurityofchaos.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;


import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;

/**
 * MIXIN: Injects into ChatScreen to get the current ChatField.
 */
@Mixin(ChatScreen.class)
public interface ChatScreenAccessor {
    
    @Accessor("chatField")
    TextFieldWidget getChatField();
}
