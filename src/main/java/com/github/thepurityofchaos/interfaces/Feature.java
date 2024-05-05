package com.github.thepurityofchaos.interfaces;

import com.github.thepurityofchaos.utils.gui.GUIElement;

public interface Feature {
    //X Feature
    public static final GUIElement xVisual = null;
    public static final boolean isEnabled = false;
    public static GUIElement getFeatureVisual(){return xVisual;}
    public static boolean getFeatureEnabled(){return isEnabled;}
    public static void toggleFeature(){
        //isEnabled = !isEnabled
    }

}
