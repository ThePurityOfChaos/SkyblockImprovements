package com.github.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.github.sbimp.listeners.SackListener;
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
        parseMessage(message.getContent());
    }


    //here for later, found while reasearching.
    //Removes all color codes
    //Formatting.strip(message);
    //Gets the siblings of a message
    //message.getSiblings();


    //Used to parse ALL messages. 
    
    private void parseMessage(Text message) {
        if(message == null)
            return;

        //Generic Parse Checks. Should be ordered by probability of appearance.  

        /*Is this a(n) [X] message? If so, hand it off to XListener. 
        This assumes that each message has only one valid listener, 
        so if you need some check in multiple listeners send it to an intermediary class.
        **  if(XListener.isMyMessage(message))
        **      return; 
        */    

        //Is this a [Sacks] message? If so, hand it off to the SackListener.
        if(SackListener.isMyMessage(message))
            return;


        
        
    }

    
}
