package com.sbimp.config;

import com.sbimp.sb.utils.gui.GUIElement;
import com.sbimp.sb.utils.gui.GUIScreen;

public class ConfigScreen extends GUIScreen {
    
    public void init(){
        //Item Pickup Log
        this.addElement(new GUIElement(64,64,64,64));
        
        super.init();
    }
}
