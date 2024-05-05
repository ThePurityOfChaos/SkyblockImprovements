package com.github.thepurityofchaos.listeners;

import com.github.thepurityofchaos.features.economic.BatFirework;

import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.Text;

public class ChatListener {
    

    //here for later, found while reasearching.
    //Removes all color codes
    //Formatting.strip(message);
    //Gets the siblings of a message
    //message.getSiblings();
    //Used to parse Chat messages specifically, in the event that they need to be removed.
    public static void parseMessage(SignedMessage message){ 
        parseMessage(message.getContent());
    }

    //Used to parse ALL messages, including game messages. 
    public static Text parseMessage(Text message) {
        if(message == null)
            return null;
        if(message.getStyle().getHoverEvent()!=null){
            return null;
        }
        //Generic Parse Checks. Should be ordered by probability of appearance.  

        /*Is this a(n) [X] message? If so, hand it off to XListener. 
        This assumes that each message has only one valid listener, 
        so if you need some check in multiple listeners send it to an intermediary class.
        **  if(XListener.isMyMessage(message))
        **      return; 
        */    

        //Is this a [Sacks] message? If so, hand it off to the SackListener.
        if(SackListener.isMyMessage(message))
            return null;
        if(BatFirework.isMyMessage(message)){
            return null;
        }

        return SpecialListener.isMyMessage(message);
        
    }
}
