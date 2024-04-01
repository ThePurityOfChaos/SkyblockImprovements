package com.sbimp.sb.features.listeners;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sbimp.sb.interfaces.Listener;

public class SackListener implements Listener{

    private static final Logger LOGGER = LoggerFactory.getLogger(SackListener.class);

    public static boolean isMyMessage(Text message){
        if(message.getString().contains("[Sacks]")){
            parseMessage(message);
            return true;
        } 
        return false;
    }

    private static void parseMessage(Text message){
        List<Text> siblings = message.getSiblings();
        System.out.println(Formatting.strip(message.getString())+"[From Sacks]");
        for(Text sibling : siblings){
            LOGGER.info(Formatting.strip(sibling.getString())+"[From Sacks]");
            //ItemPickupLog.addSackText(sibling);
        }
    }
}