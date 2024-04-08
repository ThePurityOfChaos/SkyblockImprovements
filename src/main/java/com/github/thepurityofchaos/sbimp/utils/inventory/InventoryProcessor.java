package com.github.thepurityofchaos.sbimp.utils.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.AbstractMap;


import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

//Some assistance from https://github.com/BiscuitDevelopment/SkyblockAddons/blob/main/src/main/java/codes/biscuit/skyblockaddons/utils/InventoryUtils.java#L141, though quite a bit of it was limited due to being based on 1.8.9.
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
    
    public static List<ItemStack> processInventoryToList(Inventory inventory, boolean isPlayerInventory){
        List<ItemStack> result = new ArrayList<>(inventory.size());
        for(int i=0; i<inventory.size(); i++){
            //if the inventory is a player inventory, it will have a Skyblock Menu. This menu can change, so swapping it is annoying. Skip it.
            if(isPlayerInventory&&i==8) continue;

            //adds a copy to the result if not empty, otherwise adds null
            result.add(!inventory.getStack(i).isEmpty()?inventory.getStack(i).copy():null);
            
        }
        return result;
    }
    public static Map<String,AbstractMap.SimpleEntry<Integer,NbtCompound>>processListToMap(List<ItemStack> list){
        Map<String,AbstractMap.SimpleEntry<Integer,NbtCompound>> map = new HashMap<>();
        for(int i=0; i<list.size(); i++){
            ItemStack item = null;
            try{
                item = list.get(i);
                if(item!=null){
                    int count;
                    count = (map.containsKey(item.getName().getString()))?map.get(item.getName().getString()).getKey() + item.getCount():item.getCount();
                    NbtCompound data = item.getNbt();
                    if(data!=null){
                        data = data.copy();
                    }
                    map.put(item.getName().getString(),new AbstractMap.SimpleEntry<Integer,NbtCompound>(count,data));
                }
            } catch(Exception e){
                
            }
        }   
        return map;
    }



}
