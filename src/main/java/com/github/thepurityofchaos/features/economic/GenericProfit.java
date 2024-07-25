package com.github.thepurityofchaos.features.economic;


import java.util.HashMap;
import java.util.Map;


import com.github.thepurityofchaos.abstract_interfaces.ErrorableFeature;
import com.github.thepurityofchaos.storage.Bazaar;
import com.github.thepurityofchaos.utils.gui.MenuElement;

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
 * <p> {@link #getErrors()}: Returns the list of missing item values, which can be found in the Bazaar.
 * 
 * <p> {@link #resetProfit()}: Resets the current profit.
 * 
 */
public class GenericProfit extends ErrorableFeature {
    //INCLUDED IN: EcoConfig -> advanced
    private boolean useBuy = false;
    //INCLUDED IN: None
    private Map<String,Integer> currentItems = new HashMap<>();
    private boolean hasChanged = false;
    private double currentProfit = 0.0;
    private long currentTime = -1;
    private int precision = 1;
    private static GenericProfit instance = new GenericProfit();

    public void init(){
        visual = new MenuElement(64, 0, 128, 32, null);
        visual.setMessage(Text.of("Generic Profit"));
    }

    public void add(String s, int i){
        if(currentItems.containsKey(s))
            currentItems.put(s,currentItems.get(s)+i);
        else
        currentItems.put(s,i);
        hasChanged = true;
    }
    public double getProfit(){
        if(hasChanged){
            updateProfit();
            hasChanged = false;
        }
        return currentProfit;
    }
    public void updateProfit(){
        currentProfit = 0.0;
        errors.clear();
        if(currentTime==-1) currentTime = System.currentTimeMillis();
            for(Map.Entry<String,Integer> entry : currentItems.entrySet()){
                double value = useBuy?Bazaar.getBuy(entry.getKey()):Bazaar.getSell(entry.getKey());
                if(value!=-1){
                    currentProfit+=value*entry.getValue();
                }
                else
                    addError(entry.getKey());
            }
    }
    public long getProfitPerHour(){
        long duration = (System.currentTimeMillis()-currentTime)/1000;
        return (long)(3600*currentProfit/duration);
    }
    //getters and togglers
    public boolean useBuy(){return useBuy;}
    public void toggleBuy(){useBuy = !useBuy;}
    public long getTime(){return currentTime;}
    public void resetProfit(){
        currentProfit = 0.0;
        currentTime = -1;
        currentItems.clear();
        hasChanged = true;
    }
    @Override
    public void resolveErrors(){
        super.resolveErrors();
        hasChanged = true;
    }

    public static GenericProfit getInstance() {
        return instance;
    }

    public int getPrecision() {
        return precision;
    }
    public void setPrecision(int p){
        precision = p;
    }

}
