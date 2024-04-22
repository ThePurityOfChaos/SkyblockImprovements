package com.github.thepurityofchaos.utils.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.thepurityofchaos.features.economic.ChocolateFactory;
import com.github.thepurityofchaos.features.economic.Refinery;
import com.github.thepurityofchaos.storage.Sacks;
import com.github.thepurityofchaos.utils.Utils;

import java.util.AbstractMap;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;

//Some assistance from https://github.com/BiscuitDevelopment/SkyblockAddons/blob/main/src/main/java/codes/biscuit/skyblockaddons/utils/InventoryUtils.java#L141, 
//though quite a bit of it was limited due to being based on 1.8.9- it was based on Strings, while Minecraft has moved on to Text.
public class InventoryProcessor {
    
    
    public static void init(){
        ScreenEvents.AFTER_INIT.register((client,screen,w,h) ->{
            if(screen instanceof GenericContainerScreen){
                //determine type
                //Sack
                if(screen.getTitle().getString().contains("Sack")&&!screen.getTitle().getString().contains("Sack of Sacks")){
                    Screens.getButtons(screen).add(ButtonWidget.builder(Text.literal(Utils.getColorString(ChangeInstance.getColorCode())+"Record Contents"), 
                    buttonWidget -> {
                        Screens.getButtons(screen).remove(buttonWidget);
                        Sacks.processListToSacks(processSlotsToList(((GenericContainerScreen)screen).getScreenHandler()));
                        Sacks.saveSettings();
                    })
                    .dimensions(64,64,96,16)
                    .build()
                    );
                } 
                //Chocolate Factory
                if(screen.getTitle().getString().contains("Chocolate Factory")){
                    ChocolateFactory.processList(processSlotsToList(((GenericContainerScreen)screen).getScreenHandler()));
                }
                //Refinery
                if(screen.getTitle().getString().contains("Refine")){
                    Refinery.processList(processSlotsToList(((GenericContainerScreen)screen).getScreenHandler()));
                }
                
            }
        });
    }
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
        List<ItemStack> result = new ArrayList<>(inventory.size());
        for(int i=0; i<inventory.size(); i++){
            //if the inventory is a player inventory, it will have a Skyblock Menu. This menu can change, so swapping it is annoying. Skip it.
            if(isPlayerInventory&&i==8) continue;

            //adds a copy to the result if not empty, otherwise adds null
            result.add(!inventory.getStack(i).isEmpty()?inventory.getStack(i).copy():null);
            
        }
        return result;
    }
    public static Map<Text,AbstractMap.SimpleEntry<Integer,NbtCompound>>processListToMap(List<ItemStack> list){
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
                    map.put(item.getName(),new AbstractMap.SimpleEntry<Integer,NbtCompound>(count,data));
                }
            } catch(Exception e){
                
            }
        }   
        return map;
    }



}
