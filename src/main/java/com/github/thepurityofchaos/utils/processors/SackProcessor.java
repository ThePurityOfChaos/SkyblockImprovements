package com.github.thepurityofchaos.utils.processors;


import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;

import com.github.thepurityofchaos.abstract_interfaces.MessageProcessor;
import com.github.thepurityofchaos.features.itempickuplog.ItemPickupLog;

public class SackProcessor implements MessageProcessor{

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
                //somewhat hacky but it works really, really well for this specific instance, and can be easily modified. It's just checking for a new line without newline characters.
                if(sibling.getString().contains(")")){
                    ItemPickupLog.getInstance().addSackText(Text.of(temp));
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
            try{            
            //negatives, generally. May cause an ArrayIndexOutOfBounds exception in unintended cases, in which case nothing should happen.
            if(message.getSiblings().get(3)!=null&&
            message.getSiblings().get(3).getStyle()!=null&&
            message.getSiblings().get(3).getStyle().getHoverEvent()!=null)
                parseHoverEvent(message.getSiblings().get(3).getStyle().getHoverEvent());
            }catch(Exception e){}
        }
    }
    
}