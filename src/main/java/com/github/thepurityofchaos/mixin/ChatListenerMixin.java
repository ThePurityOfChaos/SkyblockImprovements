package com.github.thepurityofchaos.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.github.thepurityofchaos.listeners.ChatListener;
import com.mojang.authlib.GameProfile;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;

import net.minecraft.text.Text;
/**
 * MIXIN: Injects into onChatMessage() and onGameMessage() to listen to all messages and remove some messages.
 */
@Mixin(MessageHandler.class)
public class ChatListenerMixin {
    //Inject into message parsing for generic purposes
    @Inject(at = @At("HEAD"), method = "onChatMessage")
    public void onChatMessage(SignedMessage message, GameProfile sender, MessageType.Parameters params, CallbackInfo info){
        ChatListener.parseMessage(message);
    }
    //
    @Inject(at = @At("HEAD"), method = "onGameMessage", cancellable = true)
    public void onGameMessage(Text message, boolean overlay, CallbackInfo info){
        Text newMessage = ChatListener.parseMessage(message);
        if(newMessage!=null){
            info.cancel();
            if(newMessage.equals(Text.of(""))) return;
            MinecraftClient client = MinecraftClient.getInstance();
            client.inGameHud.getChatHud().addMessage(newMessage);
        }
    }


    
}
