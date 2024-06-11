package com.github.thepurityofchaos.features.economic;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.github.thepurityofchaos.storage.Bazaar;
import com.github.thepurityofchaos.utils.Utils;
import com.github.thepurityofchaos.utils.gui.GUIElement;

import net.minecraft.text.Text;
/**
 * Economic Widget used for all Sack profit in a certain time period.
 * 
 * <p> {@link #init()}: Creates the visual element.
 * 
 * <p> {@link #add(String, int)}: Adds an amount of an item to be recorded.
 * 
 * <p> {@link #getProfit()}: Returns the current profit.
 * 
 * <p> {@link #getProfitPerHour()}: Returns the current profit divided by the time taken.
 * 
 * <p> {@link #getTime()}: Returns the time taken.
 * 
 * <p> {@link #useBuy()}: Whether or not to use the Instant Buy (usually higher) price or the Instant Sell price.
 * 
 * <p> {@link #toggleBuy()}: Toggles {@value #useBuy}.
 * 
 * <p> {@link #getFeatureVisual()}: Returns the visual element.
 * 
 * <p> {@link #getFeatureEnabled()}: Returns isEnabled.
 * 
 * <p> {@link #getErrors()}: Returns the list of missing item values, which can be found in the Bazaar.
 * 
 * <p> {@link #resetProfit()}: Resets the current profit.
 * 
 * <p> {@link #toggleFeature()}: Toggles the feature between on and off. Also resets profit.
 */
public class GenericProfit {
    //INCLUDED IN: EcoConfig -> buttons
    private static GUIElement GPVisual = null;
    //INCLUDED IN: EcoConfig -> advanced
    private static boolean useBuy = false;
    private static boolean isEnabled = false;
    //INCLUDED IN: None
    private static Map<String,Integer> currentItems = new HashMap<>();
    private static boolean hasChanged = false;
    private static double currentProfit = 0.0;
    private static long currentTime = -1;
    private static Set<Text> errors = new HashSet<>();

    public static void init(){
        GPVisual = new GUIElement(64, 0, 128, 32, null);
    }

    public static void add(String s, int i){
        if(currentItems.containsKey(s))
            currentItems.put(s,currentItems.get(s)+i);
        else
        currentItems.put(s,i);
        hasChanged = true;
    }
    public static double getProfit(){
        if(hasChanged){
            currentProfit = 0.0;
            errors.clear();
            if(currentTime==-1) currentTime = System.currentTimeMillis();
                for(Map.Entry<String,Integer> entry : currentItems.entrySet()){
                    double value = useBuy?Bazaar.getBuy(entry.getKey()):Bazaar.getSell(entry.getKey());
                    if(value!=-1){
                        currentProfit+=value*entry.getValue();
                    }
                    else
                        errors.add(Text.of(Utils.getColorString('4')+entry.getKey()));
                }
            
            hasChanged = false;
        }
        return currentProfit;
    }
    public static long getProfitPerHour(){
        long duration = (System.currentTimeMillis()-currentTime)/1000;
        return (long)(3600*currentProfit/duration);
    }
    //getters and togglers
    public static boolean useBuy(){return useBuy;}
    public static void toggleBuy(){useBuy = !useBuy;}
    public static GUIElement getFeatureVisual(){return GPVisual;}
    public static boolean getFeatureEnabled(){return isEnabled;}
    public static Set<Text> getErrors(){return errors;}
    public static long getTime(){return currentTime;}

    public static void toggleFeature(){ 
        isEnabled = !isEnabled;
        resetProfit();
    }
    public static void resetProfit(){
        currentProfit = 0.0;
        currentTime = -1;
        currentItems.clear();
    }

}
