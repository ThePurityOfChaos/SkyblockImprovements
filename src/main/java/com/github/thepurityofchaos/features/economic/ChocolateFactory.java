package com.github.thepurityofchaos.features.economic;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.github.thepurityofchaos.abstract_interfaces.Feature;
import com.github.thepurityofchaos.abstract_interfaces.ScreenInteractor;
import com.github.thepurityofchaos.utils.NbtUtils;
import com.github.thepurityofchaos.utils.Utils;
import com.github.thepurityofchaos.utils.gui.GUIElement;
import com.github.thepurityofchaos.utils.processors.InventoryProcessor;
import com.github.thepurityofchaos.utils.screen.ScreenUtils;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
/**
 * Contains all the information for the Chocolate Factory.
 * <p> {@link #init()}: Initializes the visual element.
 * 
 * <p> {@link #processList(List)}: Processes the ItemStacks in the Chocolate Factory into valid information.
 * 
 * <p> {@link #getChocolateCount()}: Returns the current chocolate that the player has.
 * 
 * <p>{@link #getCPS()}: Returns the current chocolate per second.
 * 
 * <p> {@link #getColorCode()}: Returns the current color code. Currently does not change.
 * 
 * <p> {@link #getRank()}: Returns the player's Rank.
 * 
 * <p> {@link #getTimeToPrestige()}: Returns the current time to prestige.
 * 
 * <p> {@link #mostEfficientUpgrade()}: Returns the most efficient upgrade.
 * 
 * <p> {@link #getTimeToUpgrade()}: Returns the most efficient upgrade's time.
 */
public class ChocolateFactory extends Feature implements ScreenInteractor {
    //INCLUDED IN: None
    private Map<String,Long> factoryInfo = new HashMap<>();
    private int currentBaseProduction = 0;
    private double currentCPS = 0.0;
    private char colorCode = '6';
    private long mEuCost = 0;

    private static ChocolateFactory instance = new ChocolateFactory();

    public void processList(List<ItemStack> list){
        if(list==null) return;
        for(ItemStack item : list){
            getDataFromItemStack(item);
        }
        factoryInfo.entrySet().removeIf(entry -> entry.getValue()==-1);
    }
    private void getDataFromItemStack(ItemStack item){
        List<Text> lore = NbtUtils.getLorefromItemStack(item);
        Text name = NbtUtils.getNamefromItemStack(item);
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
        //if this is the prestige checker
        if(name.getString().contains("Chocolate Factory")&&!name.getString().contains("Ranking")){
            for(Text text : lore){
                try{
                    if(text.getString().contains("Requires")){
                        long countMultiplier = text.getString().contains("B")?1000000000:text.getString().contains("M")?1000000:1;
                        Scanner intScanner = new Scanner(Utils.removeCommas(text.getString().replace("B","").replace("M","")));
                        long chocolateCount = 0;
                        while(intScanner.hasNext()){
                            if(intScanner.hasNextLong()){
                            chocolateCount = intScanner.nextLong();
                            break;
                            }
                            intScanner.next();
                        }
                        intScanner.close();
                        factoryInfo.put("Prestige Requirements",chocolateCount*countMultiplier);
                    }
                    else if(text.getString().contains("Chocolate this Prestige")){
                        Scanner intScanner = new Scanner(Utils.removeCommas(text.getString().replace("#","")));
                        long chocolateCount = 0;
                        while(intScanner.hasNext()){
                            if(intScanner.hasNextLong()){
                                chocolateCount = intScanner.nextLong();
                                break;
                            }
                            intScanner.next();
                        }
                        intScanner.close();
                        factoryInfo.put("Chocolate this Prestige",chocolateCount);
                    }
                }catch(Exception e){}
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
                        Scanner longScanner = new Scanner(Utils.removeCommas(text.getString()));
                        long cost = longScanner.nextLong();
                        longScanner.close();
                        factoryInfo.put(processName(name),cost);
                        return;
                    }catch(Exception e){}
                }
                if(s.contains("corporate ladder")||s.contains("max")||s.contains("already taught")){
                    factoryInfo.put(processName(name),-1L);
                    return;
                }
                if(s.contains("You are #")){
                    Scanner intScanner = new Scanner(Utils.removeCommas((s.replace("You are #",""))));
                    int rank = 0;
                    while(intScanner.hasNext()){
                        if(intScanner.hasNextInt()){
                        rank = intScanner.nextInt();
                        break;
                        }
                        intScanner.next();
                    }
                    intScanner.close();
                    factoryInfo.put("Ranking",(long)rank);
                    return;
                }
                
            }
    }
    //if a member of the Rabbit Family or the Time Tower
    private String processName(Text name){
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
        if(nameString.contains("Hand")){
            nameString = "Hand-Baked Chocolate";
        }
        return nameString;
    }

    public Text getChocolateCount(){
        Long i = factoryInfo.get("Chocolate Count");
        if(i!=null){
        return Text.of(Utils.getColorString(colorCode)+"Chocolate Count: " + Utils.addCommas(i.toString(),0));
        }
        return Text.of("");
    }

    public Text getCPS(){
        return Text.of(Utils.getColorString(colorCode)+"Chocolate Per Second: " + Utils.addCommas(((Integer)((Double)currentCPS).intValue()).toString(),1));
    }

    public Text getTimeToUpgrade(){
        try{
        long currentChocolate = factoryInfo.get("Chocolate Count");
        if(mEuCost < currentChocolate)
            return Text.of(Utils.getColorString(colorCode)+"Time to Upgrade: "+Utils.getColorString('a')+"Ready!");
        double timeNeeded = (mEuCost - currentChocolate)/currentCPS+1;
        return Text.of(Utils.getColorString(colorCode)+"Time to Upgrade: "+Utils.getTime(timeNeeded));
        }catch(Exception e){
            return Text.of("");
        }
    }
    public Text getRank(){
        try{
            long currentChocolateRank = factoryInfo.get("Ranking");
            return Text.of(Utils.getColorString(colorCode)+"Ranking: "+Utils.getColorString('8')+"#"+Utils.getColorString('b')+Utils.addCommas(((Long)currentChocolateRank).toString(),0));
        }catch(Exception e){
            return Text.of("");
        }
    }
    public Text getTimeToPrestige(){
        try{
            long currentChocolate = factoryInfo.get("Chocolate this Prestige");
            long chocolateNeeded = factoryInfo.get("Prestige Requirements");
            if(chocolateNeeded==0) return Text.of(Utils.getColorString(colorCode)+"Time to Prestige: "+Utils.getColorString('a')+"None! Well Done!");
            double timeNeeded = (chocolateNeeded - currentChocolate)/currentCPS+1;
        return timeNeeded<0?
            Text.of(Utils.getColorString(colorCode)+"Time to Prestige: "+Utils.getColorString('a')+"Ready!"):
            Text.of(Utils.getColorString(colorCode)+"Time to Prestige: "+Utils.getTime(timeNeeded));
        }catch(Exception e){
            return Text.of("");
        }
    }

    public Text mostEfficientUpgrade(){
        //get all info specific to the upgrades. This used to be a single list, but that was less maintainable 
        //than the current iteration's split.
        //add all employees here
        List<AbstractMap.SimpleEntry<String,Long>> employees = new ArrayList<>();
        employees.add(new SimpleEntry<>("Rabbit Bro",factoryInfo.get("Rabbit Bro")));
        employees.add(new SimpleEntry<>("Rabbit Cousin",factoryInfo.get("Rabbit Cousin")));
        employees.add(new SimpleEntry<>("Rabbit Sis",factoryInfo.get("Rabbit Sis")));
        employees.add(new SimpleEntry<>("Rabbit Daddy",factoryInfo.get("Rabbit Daddy")));
        employees.add(new SimpleEntry<>("Rabbit Granny",factoryInfo.get("Rabbit Granny")));
        employees.add(new SimpleEntry<>("Rabbit Uncle",factoryInfo.get("Rabbit Uncle")));
        employees.add(new SimpleEntry<>("Rabbit Dog",factoryInfo.get("Rabbit Dog")));
        //add all other buff effects here
        List<AbstractMap.SimpleEntry<String,Long>> buffers = new ArrayList<>();
        buffers.add(new SimpleEntry<>("Time Tower",factoryInfo.get("Time Tower")));
        buffers.add(new SimpleEntry<>("Coach Jackrabbit",factoryInfo.get("Coach Jackrabbit")));
        buffers.add(new SimpleEntry<>("Hand-Baked Chocolate",factoryInfo.get("Hand-Baked Chocolate")));
        //weight the values based on production increases by its divisor

        //each employee produces 1 more than the previous one
        double[] values = new double[employees.size()+buffers.size()];
        for(int i=0; i<employees.size(); i++){
            if(employees.get(i).getValue()!=null)
                if(employees.get(i).getValue()!=-1)
                    values[i] = (employees.get(i).getValue().intValue()/(i+1.0));
                else{
                    values[i] = -1;
                }
            else{
                values[i] = -1;
            }
        }
        //add buffers (each may require special production values, so do that manually here)
        try{
            values[employees.size()] = (buffers.get(0).getValue()/(currentBaseProduction*0.1*3/24));
        }catch(Exception e){
            values[employees.size()] = -1;
        }
        try{
            values[employees.size()+1] = (buffers.get(1).getValue()/(currentBaseProduction*0.01));
        }catch(Exception e){
            values[employees.size()+1] = -1;
        }
        try{
            values[employees.size()+2] = (buffers.get(2).getValue()/10);
        }catch(Exception e){
            values[employees.size()+2] = -1;
        }

        //now, find the minimum cost
        try{
            double currentMin = 25000000000L;
            int minLoc = 0;
            for(int i=0; i<values.length; i++){
                if(values[i]<currentMin && values[i]!=-1){
                    currentMin = values[i];
                    minLoc = i;
                }
            }
            //buffers
            boolean isBuffer = minLoc>=employees.size();
            if(isBuffer){
                minLoc-=employees.size();
                mEuCost = buffers.get(minLoc).getValue();
                return Text.of(Utils.getColorString(colorCode)+"Most Efficient Upgrade: "+buffers.get(minLoc).getKey());   
            }
            //employees
            mEuCost = employees.get(minLoc).getValue();
            return Text.of(Utils.getColorString(colorCode)+"Most Efficient Upgrade: "+employees.get(minLoc).getKey());  
        }catch(Exception e){
            mEuCost = -1;
        }
        return Text.of(Utils.getColorString(colorCode)+"Most Efficient Upgrade: "+Utils.getColorString('4')+"Unknown");
    }
    public void init(){visual = new GUIElement(64,64,128,32, null);}
    public void setColorCode(char c){
        colorCode = c;
    }
    public char getColorCode(){
        return colorCode;
    }
    public void interact(Screen screen){
            ScreenEvents.afterTick(screen).register(currentScreen -> {
                ChocolateFactory.getInstance().processList(InventoryProcessor.processSlotsToList(((GenericContainerScreen)screen).getScreenHandler()));
            });
            ScreenEvents.afterRender(screen).register((currentScreen, drawContext, mouseX, mouseY, delta)->{ 
                Identifier texture = new Identifier("sbimp","textures/border.png");
                int x = currentScreen.width/4;
                int y = currentScreen.height/2;
                int yOffset = currentScreen.height/4;
                List<Text> texts = new ArrayList<>();
                ChocolateFactory cf = ChocolateFactory.getInstance();
                texts.add(cf.getChocolateCount());
                texts.add(cf.getCPS());
                texts.add(Text.of(""));
                texts.add(cf.mostEfficientUpgrade());
                texts.add(cf.getTimeToUpgrade());
                texts.add(Text.of(""));
                texts.add(cf.getTimeToPrestige());
                texts.add(cf.getRank());
                ScreenUtils.draw(drawContext, texts, texture, x, y-yOffset,-1,-1,1000,-1,-1,-1, true);
            });        
    }
    public static ChocolateFactory getInstance() {
        return instance;
    }

}
