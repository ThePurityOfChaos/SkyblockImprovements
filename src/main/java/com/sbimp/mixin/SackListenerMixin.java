package com.sbimp.mixin;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

import net.minecraft.client.network.message.MessageHandler;

import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;
import com.sbimp.SkyblockImprovements;
import com.sbimp.sb.features.itempickuplog.ItemPickupLog;

@Mixin(MessageHandler.class)
public class SackListenerMixin {
    //Inject into message parsing, check for [Sacks]
    private static final Logger LOGGER = LoggerFactory.getLogger(SackListenerMixin.class);
    @Inject(at = @At("HEAD"), method = "onChatMessage")
    public void onChatMessage(SignedMessage message, GameProfile sender, MessageType.Parameters params, CallbackInfo info){
        parseMessageForSacks(message.getContent());
    }

    private void parseMessageForSacks(Text message){
        if(message.getString().contains("[Sacks]")){
            List<Text> siblings = message.getSiblings();
            for(Text sibling : siblings){
                LOGGER.info(Formatting.strip(sibling.getString())+" [From Sacks]");
                //ItemPickupLog.addSackText(sibling);
            }
        }
    }
}