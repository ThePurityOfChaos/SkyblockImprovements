package com.github.thepurityofchaos.listeners;

import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;

public class ChatListener {
    

    //here for later, found while reasearching.
    //Removes all color codes
    //Formatting.strip(message);
    //Gets the siblings of a message
    //message.getSiblings();


    //Used to parse ALL messages. 
    
    public static void parseMessage(Text message) {
        if(message == null)
            return;
        if(message.getStyle().getHoverEvent()!=null){
            System.out.println("HOVER EVENT: "+message.getStyle().getHoverEvent().getValue(HoverEvent.Action.SHOW_TEXT).getString());
            return;
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
            return;
        

        
        
    }
}
