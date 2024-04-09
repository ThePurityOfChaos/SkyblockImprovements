package com.github.thepurityofchaos.features.itempickuplog;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;

import com.github.thepurityofchaos.interfaces.Feature;
import com.github.thepurityofchaos.utils.gui.GUIElement;
import com.github.thepurityofchaos.utils.inventory.ChangeInstance;
import com.github.thepurityofchaos.utils.inventory.InventoryProcessor;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.text.Text;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
//Heavily modified from https://github.com/BiscuitDevelopment/SkyblockAddons/blob/main/src/main/java/codes/biscuit/skyblockaddons/utils/InventoryUtils.java#L303. 
public class ItemPickupLog implements Feature {
    //There is only ever ONE Item Pickup Log, so it's safe for every member of the class to be static.
    private static GUIElement IPLVisual;
    private static List<ItemStack> formerInventory;
    private static Multimap<Text,ChangeInstance> log = ArrayListMultimap.create();
    public static void init(){
        IPLVisual = new GUIElement(64,64,128,32,null);
        IPLVisual.setMessage(Text.of("Item Pickup Log"));
    }
    public static GUIElement getFeatureVisual(){
        return IPLVisual;
    }
    public static boolean addSackText(Text message){
        //parse out an int from the sack text
        try (Scanner intParser = new Scanner(message.getString())) {
            int changeAmount = intParser.nextInt();
            //if there's an actual change
            if(changeAmount!=0)
                log.put(message,new ChangeInstance(message, changeAmount, null,true));
            
            intParser.close();
            //it's expected that NoSuchElementException is thrown, since it's guaranteed to include the phrases "Added items:" and "This message can be disabled in the settings."
        }catch(NoSuchElementException e){return false;}
        return true;
    }
    //this will run many, many times.
    public static void determineChanges(){
        //this is what formerInventory will be defined as at the end of each determination.
        List<ItemStack> inventory = InventoryProcessor.processInventoryToList(InventoryProcessor.getPlayerInventory(), true);

        if(formerInventory!=null){
            //map out the inventories from list form.
            Map<Text, AbstractMap.SimpleEntry<Integer,NbtCompound>> formerInventoryMap = InventoryProcessor.processListToMap(formerInventory);
            Map<Text, AbstractMap.SimpleEntry<Integer,NbtCompound>> currentInventoryMap = InventoryProcessor.processListToMap(inventory);
            //recorder for change instances
            List<ChangeInstance> allChangeInstances = new ArrayList<>();

            //all keys in the former (and current) maps
            Set<Text> keys = new HashSet<>(formerInventoryMap.keySet());
            keys.addAll(currentInventoryMap.keySet());
            // -> is the lambda expression, which folds a Predicate() to make functions far smaller & potentially easier to read (with the knowledge of what -> does, of course).
            keys.forEach(key -> {
                //if the key exists already, use what already exists. Otherwise, it's currently 0.
                int formerCount = formerInventoryMap.containsKey(key)?
                    formerInventoryMap.get(key).getKey():
                    0;
                int currentCount = currentInventoryMap.containsKey(key)?
                    currentInventoryMap.get(key).getKey():
                    0;
                
                //if there is a change in the count of these
                if(currentCount-formerCount!=0){
                    allChangeInstances.add(
                        new ChangeInstance(
                            //key = name
                            key, 
                            //change amount
                            currentCount-formerCount, 
                            //get the NBT data associated with the current key, if it exists in the currentInventoryMap. Otherwise, add it from the formerInventoryMap.
                            currentInventoryMap.getOrDefault(
                                key, 
                                formerInventoryMap.get(key)).getValue()
                            ,false));
                }
            });
            //Now that we have all the change instances defined
            for(ChangeInstance instance : allChangeInstances){
                //get the existing instances of this item from the log, to update them if an instance already exists.
                Collection<ChangeInstance> existingInstances = log.get(instance.getName());
                //if there aren't any existing instances, we can ignore this one.
                if(existingInstances.size()<=0){
                    log.put(instance.getName(),instance);
                }else{

                    boolean isAdded = false;
                    //there should generally be VERY FEW of these, so even though it turns it into O(n^2), it's more like m*n with m being some number <= 35.
                    for(ChangeInstance existingInstance : existingInstances){
                        //if the existing instance counts are not zero and are aligned with each other 
                        //negatives and positives should show separately and not annihilate: 
                        //-1x Potato and +1x Potato should both show if a player drops and picks them back up.
                        if((existingInstance.getCount() < 0 &&instance.getCount() < 0) || (existingInstance.getCount() > 0 && instance.getCount() > 0)){
                            existingInstance.addToInstance(instance.getCount());
                            isAdded = true;
                        }
                    }
                    if(!isAdded){
                        log.put(instance.getName(), instance);
                    }
                }
            }
        }
        formerInventory = inventory;

    }

    public static void cleanLog(){
        log.entries().removeIf(entry -> entry.getValue().getCurrentLifespan() > ChangeInstance.maxLifespan);
    }
    public static Collection<ChangeInstance> getLog(){
        return log.values();
    }

}