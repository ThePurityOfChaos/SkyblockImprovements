package com.github.thepurityofchaos.config;

import com.github.thepurityofchaos.interfaces.Filer;

public class EcoConfig implements Filer {

    public static void init(){}
    public static void createFile(){}
    public static void saveSettings(){}
    public static boolean getFeatureEnabled(){
        return true;
    }
    public static void toggleFeature(){}
}
