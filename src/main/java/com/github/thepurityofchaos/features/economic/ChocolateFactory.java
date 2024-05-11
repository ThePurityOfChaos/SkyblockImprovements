package com.github.thepurityofchaos.features.economic;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.github.thepurityofchaos.interfaces.Feature;
import com.github.thepurityofchaos.utils.Utils;
import com.github.thepurityofchaos.utils.gui.GUIElement;
import com.github.thepurityofchaos.utils.processors.NbtProcessor;

import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class ChocolateFactory implements Feature {
    private static Map<String,Long> factoryInfo = new HashMap<>();
    private static int currentBaseProduction = 0;
    private static double currentCPS = 0.0;
    private static GUIElement CFVisual = null;
    private static char colorCode = '6';
    private static long mEuCost = 0;
    public static void processList(List<ItemStack> list){
        for(ItemStack item : list){
            getDataFromItemStack(item);
        }
        factoryInfo.entrySet().removeIf(entry -> entry.getValue()==-1);
    }
    private static void getDataFromItemStack(ItemStack item){
        List<Text> lore = NbtProcessor.getLorefromItemStack(item);
        Text name = NbtProcessor.getNamefromItemStack(item);
        if(lore==null)
            return;
        boolean willBeCost = false;
        if(name.getString().contains("Chocolate Production")){
            currentBaseProduction = 0;
            for(Text text : lore){
                if(text.getString().contains("Mult")){
                    return;
                }

                if(text.getString().contains("per second")){
                    Scanner doubleScanner = new Scanner(Utils.removeCommas(text.getString()));
                    currentCPS = doubleScanner.nextDouble();
                    doubleScanner.close();
                }
                if(text.getString().contains("+")){
                    Scanner intScanner = new Scanner(Utils.removeCommas(text.getString().replace("+","").strip()));
                    currentBaseProduction+=intScanner.nextInt();
                    intScanner.close();
                }
            }
            return;
        }

        for(Text text : lore){
                //if this is the current chocolate count
                String s = text.getString();
                if(s.contains("awesome")){
                    Scanner intScanner = new Scanner(Utils.removeCommas(name.getString()));
                    long chocolateCount = intScanner.nextLong();
                    intScanner.close();
                    factoryInfo.put("Chocolate Count",chocolateCount);
                    return;
                }

                //if the next value is a Cost
                if(s.contains("Cost")){
                    willBeCost = true;
                    continue;
                }
                if(willBeCost == true){
                    try{
                        Scanner intScanner = new Scanner(Utils.removeCommas(text.getString()));
                        long cost = intScanner.nextLong();
                        intScanner.close();
                        factoryInfo.put(processName(name),cost);
                        return;
                    }catch(Exception e){}
                }
                if(s.contains("corporate ladder")||s.contains("maxed")){
                    factoryInfo.put(processName(name),-1L);
                    return;
                }
                
            }
    }
    //if a member of the Rabbit Family or the Time Tower
    private static String processName(Text name){
        String nameString = name.getString();
        if(nameString.contains("Rabbit")){
            nameString = nameString.split("-")[0].strip();
        }
        if(nameString.contains("Time Tower")){
            nameString = "Time Tower";
        }
        if(nameString.contains("Coach")){
            nameString = "Coach Jackrabbit";
        }
        return nameString;
    }

    public static Text getChocolateCount(){
        Long i = factoryInfo.get("Chocolate Count");
        if(i!=null){
        return Text.of(Utils.getColorString(colorCode)+"Chocolate Count: " + Utils.addCommas(i.toString()));
        }
        return Text.of("");
    }

    public static Text getCPS(){
        return Text.of(Utils.getColorString(colorCode)+"Chocolate Per Second: " + Utils.addCommas(((Integer)((Double)currentCPS).intValue()).toString()));
    }

    public static Text getTimeToUpgrade(){
        try{
        long currentChocolate = factoryInfo.get("Chocolate Count");
        if(mEuCost < currentChocolate)
            return Text.of(Utils.getColorString(colorCode)+"Time to Upgrade: "+Utils.getColorString('a')+"Ready!");
        double timeNeeded = (mEuCost - currentChocolate)/currentCPS;
        return Text.of(Utils.getColorString(colorCode)+"Time to Upgrade: "+
        (timeNeeded>86400?((Double)(timeNeeded/84600)).intValue()+"d ":"")+
        (timeNeeded>3600?((Double)((timeNeeded%86400)/3600)).intValue()+"h ":"")+
        (timeNeeded>60?((Double)((timeNeeded%3600)/60)).intValue()+"m ":"")+
        ((Double)(timeNeeded%60)).intValue() +"s");
        }catch(Exception e){
            return Text.of("");
        }
    }

    public static Text mostEfficientUpgrade(){
        //get all info specific to the upgrades
        List<AbstractMap.SimpleEntry<String,Long>> entries = new ArrayList<>();
        entries.add(new SimpleEntry<>("Rabbit Bro",factoryInfo.get("Rabbit Bro")));
        entries.add(new SimpleEntry<>("Rabbit Cousin",factoryInfo.get("Rabbit Cousin")));
        entries.add(new SimpleEntry<>("Rabbit Sis",factoryInfo.get("Rabbit Sis")));
        entries.add(new SimpleEntry<>("Rabbit Daddy",factoryInfo.get("Rabbit Daddy")));
        entries.add(new SimpleEntry<>("Rabbit Granny",factoryInfo.get("Rabbit Granny")));
        entries.add(new SimpleEntry<>("Time Tower",factoryInfo.get("Time Tower")));
        entries.add(new SimpleEntry<>("Coach Jackrabbit",factoryInfo.get("Coach Jackrabbit")));
        //weight the values based on production increases
        double[] values = new double[7];
        for(int i=0; i<5; i++){
            if(entries.get(i).getValue()!=null)
                if(entries.get(i).getValue()!=-1)
                    values[i] = (entries.get(i).getValue().intValue()/(i+1.0));
                else{
                    values[i] = -1;
                }
            else{
                values[i] = -1;
            }
        }
        try{
            values[5] = (entries.get(5).getValue()/(currentBaseProduction*0.1*3/24));
        }catch(Exception e){
            values[5] = -1;
        }
        try{
            values[6] = (entries.get(6).getValue()/(currentBaseProduction*0.01));
        }catch(Exception e){
            values[6] = -1;
        }
        //find the minimum cost
        try{
            double currentMin = 25000000000L;
            int minLoc = 0;
            for(int i=0; i<values.length; i++){
                if(values[i]<currentMin && values[i]!=-1){
                    currentMin = values[i];
                    minLoc = i;
                }
            }
            mEuCost = entries.get(minLoc).getValue();
            for(int i=0; i<entries.size(); i++){
                try{
                if(mEuCost == entries.get(i).getValue()){
                    return Text.of(Utils.getColorString(colorCode)+"Most Efficient Upgrade: "+entries.get(i).getKey());
                }
                }catch(Exception e){}
            }
        }catch(Exception e){
            mEuCost = -1;
        }
        return Text.of(Utils.getColorString(colorCode)+"Most Efficient Upgrade: "+Utils.getColorString('4')+"Unknown");
    }
    public static void init(){CFVisual = new GUIElement(64,64,128,32, null);}
    public static GUIElement getFeatureVisual(){return CFVisual;}

}
