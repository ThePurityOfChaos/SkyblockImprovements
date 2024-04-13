package com.github.thepurityofchaos.listeners;


import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;

import com.github.thepurityofchaos.features.itempickuplog.ItemPickupLog;
import com.github.thepurityofchaos.interfaces.Listener;

public class SackListener implements Listener{

    public static boolean isMyMessage(Text message){
        if((message.getString().contains("[Sacks]")&&message.getStyle()!=null)){
            parseMessage(message);
            return true;
        } 
        return false;
    }
    private static void parseHoverEvent(HoverEvent e){
        if(e.getAction()==HoverEvent.Action.SHOW_TEXT){
            Text tooltip = e.getValue(HoverEvent.Action.SHOW_TEXT);
            String temp = "";
            for(Text sibling : tooltip.getSiblings()){
                temp+=sibling.getString();
                //somewhat hacky but it works really, really well for this specific instance, and can be easily modified. It's just checking for a new line.
                if(sibling.getString().contains(")")){
                    ItemPickupLog.addSackText(Text.of(temp));
                    temp = "";
                }
            }
        }
    }

    private static void parseMessage(Text message){
        //why, oh why doesn't message?.getSiblings() work? and why is Hypixel's placement of its hover events so... strange?
        //this could have been two lines.

        if(message!=null&& message.getSiblings()!=null){
            //positives, generally
            if(message.getSiblings().get(0)!=null&&
            message.getSiblings().get(0).getStyle()!=null&&
            message.getSiblings().get(0).getStyle().getHoverEvent()!=null)
                parseHoverEvent(message.getSiblings().get(0).getStyle().getHoverEvent());      
                            
            //negatives, generally        
            if(message.getSiblings().get(3)!=null&&
            message.getSiblings().get(3).getStyle()!=null&&
            message.getSiblings().get(3).getStyle().getHoverEvent()!=null)
                parseHoverEvent(message.getSiblings().get(3).getStyle().getHoverEvent());
        }
    }
}