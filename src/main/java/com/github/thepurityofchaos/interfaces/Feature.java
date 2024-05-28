package com.github.thepurityofchaos.interfaces;

import com.github.thepurityofchaos.utils.gui.GUIElement;
/**
 * 
 * Implementing this Interface means that the class 
 * 
 * <p> {@link #xVisual}: The GUIElement associated with this Feature.
 * 
 * <p> {@link #isEnabled}: Whether or not this Feature is enabled.
 * 
 * <p> {@link #getFeatureVisual}: Returns xVisual.
 * 
 * <p> {@link #getFeatureEnabled}: Returns isEnabled.
 * 
 * <p> {@link #toggleFeature}: isEnabled = !isEnabled.
 */
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
