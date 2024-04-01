package com.sbimp.sb.features.itempickuplog;

import net.minecraft.text.Text;
import com.sbimp.sb.utils.gui.GUIElement;

public class ItemPickupLog {
    private static GUIElement IPLVisual;

    public ItemPickupLog createLog(){
        IPLVisual = new GUIElement(64,64,64,64);
        return new ItemPickupLog();
    }
    public GUIElement getIPLVisual(){
        return IPLVisual;
    }
    

    public static boolean addSackText(Text message){
        IPLVisual.setMessage(message);
        return true;
    }
}
