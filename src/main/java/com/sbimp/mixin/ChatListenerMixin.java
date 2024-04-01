package com.sbimp.mixin;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.Text;

@Mixin(MessageHandler.class)
public class ChatListenerMixin {
    private List<String> Listeners = new ArrayList<String>();

    @Inject(at = @At("HEAD"), method = "onChatMessage")
    public void onChatMessage(SignedMessage message, GameProfile sender, MessageType.Parameters params, CallbackInfo info){
        parseMessage(message.getContent());
    }

    public void init(){
        Listeners.add("[Sacks]");

    }
    //here for later, found while reasearching.
    //Removes all color codes
    //Formatting.strip(message);
    //Gets the siblings of a message
    //message.getSiblings();

    private void parseMessage(Text message) {
        //Listeners
        for(int i= 0; i<Listeners.size(); i++){
            if(message.getString().contains(Listeners.get(i))){
                
            }
        }
    }

    
}
