package com.github.thepurityofchaos.features.packswapper;

import com.github.thepurityofchaos.interfaces.Feature;
import com.github.thepurityofchaos.utils.gui.GUIElement;


public class PackSwapper implements Feature {
    //Used to show current Region and number of Packs associated with it. if desired.
    private static GUIElement PSVisual;
    private static char regionColor = 'e';
    private static boolean packHelper = false;
    public static void init(){
        //default location
        PSVisual = new GUIElement(64,96,128,32,null);
    }
    public static GUIElement getFeatureVisual(){
        return PSVisual;
    }
    public static char getRegionColor(){
        return regionColor;
    }
    public static void setRegionColor(char c){
        regionColor = c;
    }
    public static void togglePackHelper(){
        packHelper = !packHelper;
    }
    public static boolean showPackHelper(){
        return packHelper;
    }
}
