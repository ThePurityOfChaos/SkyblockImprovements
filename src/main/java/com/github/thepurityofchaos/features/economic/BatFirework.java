package com.github.thepurityofchaos.features.economic;

import java.util.Scanner;

import com.github.thepurityofchaos.SkyblockImprovements;
import com.github.thepurityofchaos.abstract_interfaces.Feature;
import com.github.thepurityofchaos.abstract_interfaces.MessageProcessor;
import com.github.thepurityofchaos.storage.Bazaar;
import com.github.thepurityofchaos.utils.gui.MenuElement;

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
public class BatFirework extends Feature implements MessageProcessor {
    //INCLUDED IN: None
    private double currentProfit = 0.0;
    
    private static BatFirework instance = new BatFirework();

    public void init(){
       visual = new MenuElement(64, 32, 128, 32, null);
    }

    public boolean isMyMessage(Text message){
        SkyblockImprovements.push("SBI_BatFirework");
        if(message.getString().contains(" Purple Candies")){
            addToProfit(message.getString());
        }
        SkyblockImprovements.pop();
        return false;
    }
    private void addToProfit(String message){
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
    public void resetProfit(){
        currentProfit = 0.0;
    }
    public double getProfit(){
        return currentProfit;
    }
    public static BatFirework getInstance(){
        return instance;
    }
}
