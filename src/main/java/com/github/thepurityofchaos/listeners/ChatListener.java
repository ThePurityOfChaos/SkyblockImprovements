package com.github.thepurityofchaos.listeners;

import com.github.thepurityofchaos.SkyblockImprovements;
import com.github.thepurityofchaos.features.economic.BatFirework;
import com.github.thepurityofchaos.features.economic.Bingo;
import com.github.thepurityofchaos.storage.config.IPLConfig;
import com.github.thepurityofchaos.utils.processors.SackProcessor;
import com.github.thepurityofchaos.utils.processors.SpecialProcessor;

import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.Text;


/**
 * Listens to all chat messages.
 * <p> {@link #parseMessage(SignedMessage)}: See below.
 * 
 * <p> {@link #parseMessage(Text)}: Checks every MessageProcessor's isMyMessage() method.
 */
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
        SkyblockImprovements.push("SBI_ChatListener");
        if(message == null){
            SkyblockImprovements.pop();
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
        if(SackProcessor.isMyMessage(message)){
            if(IPLConfig.removeMessage()){ 
                SkyblockImprovements.pop();
                return Text.of("");
            }
            SkyblockImprovements.pop();
            return null;
        }
            
        if(BatFirework.getInstance().isMyMessage(message)){
            SkyblockImprovements.pop();
            return null;
        }
        if(Bingo.getInstance().isMyMessage(message)){
            SkyblockImprovements.pop();
            return null;
        }
        if(message.getStyle().getHoverEvent()!=null){
            SkyblockImprovements.pop();
            return null;
        }

        SkyblockImprovements.pop();
        return SpecialProcessor.isMyMessage(message);
        
    }
}
