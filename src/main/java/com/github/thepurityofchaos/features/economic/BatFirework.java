package com.github.thepurityofchaos.features.economic;

import java.util.Scanner;

import com.github.thepurityofchaos.interfaces.Feature;
import com.github.thepurityofchaos.interfaces.Listener;
import com.github.thepurityofchaos.storage.Bazaar;
import com.github.thepurityofchaos.utils.gui.GUIElement;

import net.minecraft.text.Text;

public class BatFirework implements Listener,Feature {
    private static double currentProfit = 0.0;
    private static GUIElement BatVisual = null;
    private static boolean isEnabled = false;

    public static void init(){
       BatVisual = new GUIElement(64, 32, 128, 32, null);
    }

    public static boolean isMyMessage(Text message){
        if(message.getString().contains(" Purple Candies")){
            addToProfit(message.getString());
        }
        return false;
    }
    private static void addToProfit(String message){
        int candyAmount = 0;
        try{
            double greenCandyPrice = Bazaar.getSell("Green Candy");
            double purpleCandyPrice = Bazaar.getBuy("Purple Candy");
            if(greenCandyPrice==-1||purpleCandyPrice==-1){
                return;
            }
            currentProfit-=100*greenCandyPrice;
            Scanner candyScanner = new Scanner(message);
            while(candyScanner.hasNext()){
                if(candyScanner.hasNextInt()){
                    candyAmount = candyScanner.nextInt();
                }
                candyScanner.next();
            }
            candyScanner.close();
            currentProfit+=candyAmount*purpleCandyPrice;
        }catch(Exception e){

        }
    }
    public static void resetProfit(){
        currentProfit = 0.0;
    }
    public static double getProfit(){
        return currentProfit;
    }
    public static GUIElement getFeatureVisual(){return BatVisual;}
    public static boolean getFeatureEnabled(){return isEnabled;}
    public static void toggleFeature(){ isEnabled = !isEnabled;}
}
