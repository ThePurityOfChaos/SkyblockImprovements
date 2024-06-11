package com.github.thepurityofchaos.features.economic;

import java.util.Scanner;

import com.github.thepurityofchaos.SkyblockImprovements;
import com.github.thepurityofchaos.interfaces.Feature;
import com.github.thepurityofchaos.interfaces.MessageProcessor;
import com.github.thepurityofchaos.storage.Bazaar;
import com.github.thepurityofchaos.utils.gui.GUIElement;

import net.minecraft.text.Text;
/**
 * Economic Widget for determining the profit of a Bat Firework.
 * 
 * <p> {@link #init()}: Initializes the visual component.
 * 
 * <p> {@link #isMyMessage(Text)}: Determines whether or not the message is a Bat Firework message.
 * 
 * <p> {@link #getProfit()}: Returns the current profit.
 * 
 * <p> {@link #resetProfit()}: Resets the current profit back to 0.
 * 
 * 
 */
public class BatFirework implements MessageProcessor,Feature {
    //INCLUDED IN: None
    private static double currentProfit = 0.0;

    //INCLUDED IN: EcoConfig -> buttons
    private static GUIElement BatVisual = null;

    //INCLUDED IN: EcoConfig -> advanced
    private static boolean isEnabled = false;

    public static void init(){
       BatVisual = new GUIElement(64, 32, 128, 32, null);
    }

    public static boolean isMyMessage(Text message){
        SkyblockImprovements.push("SBI_BatFirework");
        if(message.getString().contains(" Purple Candies")){
            addToProfit(message.getString());
        }
        SkyblockImprovements.pop();
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
