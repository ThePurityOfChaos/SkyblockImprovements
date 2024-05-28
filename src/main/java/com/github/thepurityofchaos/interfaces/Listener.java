package com.github.thepurityofchaos.interfaces;

import net.minecraft.text.Text;
/**
 * Implementing this Interface means that a class will be checking Text to see if it needs to record data from it or manipulate it in some way.
 * 
 * <p> {@link #isMyMessage(Text message)}: Returns whether or not the message "belongs" to this class. All other manipulations in this class should originate from a successful isMyMessage call.
 */
public interface Listener {
    public static boolean isMyMessage(Text message){return false;}
}
