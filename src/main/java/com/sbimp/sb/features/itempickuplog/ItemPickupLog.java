package com.sbimp.sb.features.itempickuplog;

import net.minecraft.text.Text;
import com.sbimp.sb.utils.gui.GUIElement;

public class ItemPickupLog {
    private GUIElement IPLVisual;

    public ItemPickupLog createLog(){
        this.IPLVisual = new GUIElement(64,64,64,64);
        return new ItemPickupLog();
    }
    public GUIElement getIPLVisual(){
        return this.IPLVisual;
    }
    

    public boolean addSackText(Text message){
        this.IPLVisual.setMessage(message);
        return true;
    }
}
