package com.github.thepurityofchaos.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.github.thepurityofchaos.listeners.ChatListener;
import com.mojang.authlib.GameProfile;

import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;

import net.minecraft.text.Text;

@Mixin(MessageHandler.class)
public class ChatListenerMixin {
    //Inject into message parsing for generic purposes
    @Inject(at = @At("HEAD"), method = "onChatMessage")
    public void onChatMessage(SignedMessage message, GameProfile sender, MessageType.Parameters params, CallbackInfo info){
        ChatListener.parseMessage(message.getContent());
    }

    @Inject(at = @At("HEAD"), method = "onGameMessage")
    public void onGameMessage(Text message, boolean overlay, CallbackInfo info){
        ChatListener.parseMessage(message);
    }


    
}
