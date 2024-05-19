package com.github.thepurityofchaos.utils.processors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.thepurityofchaos.SkyblockImprovements;
import com.github.thepurityofchaos.utils.Utils;

import java.util.AbstractMap;

import net.minecraft.client.MinecraftClient;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

//Some assistance from https://github.com/BiscuitDevelopment/SkyblockAddons/blob/main/src/main/java/codes/biscuit/skyblockaddons/utils/InventoryUtils.java#L141, 
//though quite a bit of it was limited due to being based on 1.8.9- it was based on Strings, while Minecraft has moved on to Text.
public class InventoryProcessor {
    
    public static PlayerInventory getPlayerInventory(){
        //client-side accessor
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        //This caused a nullPointerException when the player was not in the game.
        if(player!=null){
            return player.getInventory();
        }
        return null;
    }

    public static List<ItemStack> getPlayerArmor(){
        return getPlayerInventory().armor;
    }
    public static ItemStack getHelmet(){
        return getPlayerArmor().get(3);
    }

    public static List<ItemStack> processSlotsToList(GenericContainerScreenHandler handler){
        try{
            //get all slots. Since a row has 9 slots in it, multiply the rows by 9.
            List<Slot> slots = handler.slots.subList(0, handler.getRows()*9);
            if(slots.get(0).inventory.isEmpty()) return null;
            List<ItemStack> stackList = new ArrayList<>();
            for(Slot slot : slots){
                stackList.add(slot.getStack());
            }
            return stackList;
        }catch(Exception e){

        }
        return null;
    }
    
    public static List<ItemStack> processInventoryToList(Inventory inventory, boolean isPlayerInventory){
        SkyblockImprovements.push("SBI_processInventoryToList");
        List<ItemStack> result = new ArrayList<>(inventory.size());
        for(int i=0; i<inventory.size(); i++){
            //if the inventory is a player inventory, it will have a Skyblock Menu. This menu can change, so swapping it is annoying. Skip it.
            if(isPlayerInventory&&i==8) continue;

            //adds a copy to the result if not empty, otherwise adds null
            result.add(!inventory.getStack(i).isEmpty()?inventory.getStack(i).copy():null);
            
        }
        SkyblockImprovements.pop();
        return result;
    }

    public static Map<Text,AbstractMap.SimpleEntry<Integer,NbtCompound>>processListToMap(List<ItemStack> list){
        SkyblockImprovements.push("SBI_processListToMap");
        Map<Text,AbstractMap.SimpleEntry<Integer,NbtCompound>> map = new HashMap<>();
        for(int i=0; i<list.size(); i++){
            ItemStack item = null;
            try{
                item = list.get(i);
                if(item!=null){
                    int count;
                    count = (map.containsKey(item.getName()))?map.get(item.getName()).getKey() + item.getCount():item.getCount();
                    NbtCompound data = item.getNbt();
                    if(data!=null){
                        data = data.copy();
                    }
                    //prevents merchant issues, probably. Very annoying to get right, considering that it needs to interact with a bunch of different conditions.
                    if(!item.getName().getString().contains(" x"))
                        map.put(item.getName(), new AbstractMap.SimpleEntry<Integer,NbtCompound>(count,data));
                    else{
                        MutableText merchantlessItem = MutableText.of(item.getName().getContent());
                        merchantlessItem.setStyle(item.getName().getStyle());
                        List<Text> siblings = item.getName().getSiblings();

                        for(int j=0; j<siblings.size(); j++){
                            if(j<siblings.size()-2){
                                merchantlessItem.append(siblings.get(j));
                            }
                            else if(!siblings.get(j).getString().contains("x")||!Utils.containsRegex(siblings.get(j).getString(),"[0-9]")){
                                MutableText strippedSibling = MutableText.of(Text.of(siblings.get(j).getString().stripTrailing()).getContent());
                                strippedSibling.setStyle(siblings.get(j).getStyle());
                                merchantlessItem.append(strippedSibling);
                            }
                        }
                        map.put(merchantlessItem, new AbstractMap.SimpleEntry<Integer,NbtCompound>(count,data));
                    }
                }
            } catch(Exception e){
                
            }
        }   
        SkyblockImprovements.pop();
        return map;
    }



}
