package com.github.thepurityofchaos.features.economic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.github.thepurityofchaos.abstract_interfaces.ErrorableFeature;
import com.github.thepurityofchaos.abstract_interfaces.ScreenInteractor;
import com.github.thepurityofchaos.storage.Bazaar;
import com.github.thepurityofchaos.storage.config.EcoConfig;
import com.github.thepurityofchaos.utils.NbtUtils;
import com.github.thepurityofchaos.utils.Utils;
import com.github.thepurityofchaos.utils.gui.MenuElement;
import com.github.thepurityofchaos.utils.processors.InventoryProcessor;
import com.github.thepurityofchaos.utils.screen.ScreenUtils;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
/**
 * IN PROGRESS The Refinery Widget is intended to determine what the most profitable items are, given a relative time efficiency (If you can only play for 8 hours a day, for example, 33%)
 * 
 * <p> {@link #getMostProfitable()}: Returns the most profitable item, given what you have available.
 * 
 * <p> {@link #interact(Screen)}: Draws the most profitable item.
 * 
 */
public class Refinery extends ErrorableFeature implements ScreenInteractor{
    private int timesOnlinePerDay = 1;
    private Map<Text, List<Pair<String,Integer>>> itemRequirements = new HashMap<>();
    private List<Pair<Text,Double>> results = null;
    private static Refinery instance = new Refinery();
    
    public void processRefinery(GenericContainerScreenHandler handler){
        List<ItemStack> refinables = InventoryProcessor.processSlotsToList(handler);
        if(refinables==null){ return; }
        for(ItemStack item : refinables){
            Text name = NbtUtils.getNamefromItemStack(item);
            List<Text> lore = NbtUtils.getLorefromItemStack(item);
            if(itemRequirements.containsKey(name)||lore==null||lore.isEmpty()||name.getString().contains("???")||Utils.ignorable(name.getString())) continue;
            boolean isItems = false;
            List<Pair<String,Integer>> subItems = new ArrayList<>();
            for(Text line : lore){
                if(isItems){
                    String[] splitLine = line.getString().split(" ");
                    if(line.getString().contains("Duration")){
                        int duration = 0;
                        for( String fragment : splitLine){
                            try{
                            duration+=(
                                fragment.contains("s")?1:
                                fragment.contains("m")?60:
                                fragment.contains("h")?3600:
                                fragment.contains("d")?86400:
                            0)*Integer.parseInt(Utils.removeText(fragment));
                            }catch(Exception e){}
                        }
                        subItems.add(new Pair<String,Integer>("Duration",duration));
                        break;
                    }
                    try{
                    int itemAmount = Integer.parseInt(splitLine[splitLine.length-1].replace("x","").strip());
                    String subItem = Utils.stripSpecial(String.join(" ",Arrays.copyOfRange(splitLine,0,splitLine.length-1))).strip();
                    subItems.add(new Pair<String,Integer>(subItem,itemAmount));
                    }catch(Exception e){}
                    
                    continue;
                }
                if(line.getString().contains("Items Required")){
                    isItems = true;
                    continue;
                }
            }
            itemRequirements.put(name,subItems);
        }
    }

    public List<Text> getMostProfitable(GenericContainerScreenHandler handler){
        //processes the base structure of every available item. This is used to ensure that uncraftables (???) aren't shown.
        if(handler!=null){
            processRefinery(handler);
        }
        //results
        List<Text> profits = new ArrayList<>();
        results = new ArrayList<>();
        errors = new HashSet<>();
        //get each item from the beforehand process
        for(Map.Entry<Text,List<Pair<String,Integer>>> reqs : itemRequirements.entrySet()){
            int duration = 1;
            double cost = 0;
            double value = Bazaar.get7dAvg(Utils.stripSpecial(reqs.getKey().getString()).strip());
            if(value==-1){addError(Utils.stripSpecial(reqs.getKey().getString()).strip()); continue;}
            //get each requirement
            for(Pair<String,Integer> req : reqs.getValue()){
                if(req.getLeft()=="Duration"){ 
                    duration = req.getRight();
                    continue;
                }
                double reqValue;
                if(req.getLeft().contains("Crystal")) reqValue=0;
                else reqValue = Bazaar.get7dAvg(req.getLeft());
                if(reqValue==-1){
                    addError(req.getLeft());
                }else{
                    cost+=reqValue*req.getRight();
                }
            }
            results.add(new Pair<Text,Double>(reqs.getKey(),(Double)(value-cost)*Math.min(timesOnlinePerDay,86400/duration)));
        }
        //find the most profitable of each type
        Pair<Text,Double> bestCrystal=new Pair<>(null,0.0);
        Pair<Text,Double> bestOther=new Pair<>(null,0.0);
        for(Pair<Text,Double> result : results){
            if(result.getLeft().getString().contains("Perfect")&&result.getLeft().getString().contains("Gemstone")){
                if(result.getRight()>bestCrystal.getRight()) bestCrystal=result;
            }
            else if(result.getRight()>bestOther.getRight()) bestOther=result;
        }
        if(bestCrystal.getLeft()!=null){
            profits.add(Text.of(Utils.getColorString(EcoConfig.getColorCode())+"Highest Profit Perfect:"));
            profits.add(MutableText.of(Text.of("").getContent()).append(bestCrystal.getLeft()).append(Text.of(Utils.getColorString(EcoConfig.getColorCode())+" ("+Utils.getColorString('6')+Utils.addCommas(bestCrystal.getRight().toString(),0)+Utils.getColorString(EcoConfig.getColorCode())+" coins)")));
        }
        if(bestOther.getLeft()!=null){
            profits.add(Text.of(Utils.getColorString(EcoConfig.getColorCode())+"Highest Profit Other:"));
            profits.add(MutableText.of(Text.of("").getContent()).append(bestOther.getLeft()).append(Text.of(Utils.getColorString(EcoConfig.getColorCode())+" ("+Utils.getColorString('6')+Utils.addCommas(bestOther.getRight().toString(),0)+Utils.getColorString(EcoConfig.getColorCode())+" coins)")));
        }
        if(getErrors().size()!=0){
            profits.add(Text.of(Utils.getColorString('4')+"Unknown Item Prices Detected."));
            profits.add(Text.of(Utils.getColorString('4')+"Check Bazaar for:"));
            profits.addAll(getErrors());
        }
        return profits;
    }
    public void interact(Screen screen){
            ScreenEvents.afterRender(screen).register((currentScreen, drawContext, mouseX, mouseY, delta) -> {
                int x = currentScreen.width/5;
                int xOffset = 0;
                int y = currentScreen.height/5;
                int yOffset = 0;
                ScreenUtils.draw(drawContext, Refinery.getInstance().getMostProfitable(((GenericContainerScreen)screen).getScreenHandler()), x-xOffset, y+yOffset, -1, -1, 1000, -1, -1, -1, false);
            });        
    }
    public static Refinery getInstance() {
        return instance;
    }

    @Override
    public void init() {
        visual = new MenuElement(0,0,96,32,null);
    }
}
