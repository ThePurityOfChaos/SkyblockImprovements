package com.github.thepurityofchaos.storage;

import java.util.Map;
import java.util.Scanner;
import java.util.HashMap;
import java.util.List;
import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.thepurityofchaos.SkyblockImprovements;
import com.github.thepurityofchaos.interfaces.Filer;
import com.github.thepurityofchaos.utils.Utils;
import com.github.thepurityofchaos.utils.inventory.ChangeInstance;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class Sacks implements Filer{
    private static final Logger LOGGER = LoggerFactory.getLogger(Sacks.class);
    private static Map<String,Integer> allSackContents = null;
    private static boolean featureEnabled = false;
    public static void init(){
        createFile();
        try{
            BufferedReader reader = Files.newBufferedReader(SkyblockImprovements.FILE_LOCATION.resolve("sacks.json"));
            JsonObject parser = JsonParser.parseReader(reader).getAsJsonObject();
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String,Integer>>(){}.getType();
            if(parser.getAsJsonObject("allRegions")==null) throw new Exception();
            allSackContents = gson.fromJson(parser.getAsJsonObject("allSackContents"),type);
        }catch(Exception e){
            allSackContents = new HashMap<>();
        }
    }
    
    public static void saveSettings(){
        try{
            BufferedWriter writer = Files.newBufferedWriter(SkyblockImprovements.FILE_LOCATION.resolve("sacks.json"));
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                writer.write(gson.toJson(allSackContents));
                writer.close();
            }catch(IOException e){
                LOGGER.error("[SkyblockImprovements] Sacks file may be missing. Attempting to recreate...");
                createFile();
                saveSettings();
            }
            catch(Exception e){
                LOGGER.error("[SkyblockImprovements] Sacks failed to save!");
                e.printStackTrace();
            }
    }

    public static void update(String strippedMessage, int input){ 
        String splitMessage = strippedMessage.split("§")[0].strip();
        allSackContents.put(splitMessage,allSackContents.getOrDefault(splitMessage, 0)+input);
    }
    public static void put(String type, int amount){
        allSackContents.put(type,amount);
    }
    public static String get(String strippedMessage){
        String splitMessage = strippedMessage.split("§")[0].strip().replaceAll("-[0-9]","").strip();
        try{
        return " ("+Utils.getColorString(ChangeInstance.getColorCode())+allSackContents.getOrDefault(splitMessage,0).toString()+"§8)";
        }catch(NullPointerException e){
            return "";
        }
    }
    public static boolean processListToSacks(List<ItemStack> list){
        try{
            for(ItemStack item : list){
                if(item!=null){
                    try{
                        NbtList itemLore = (NbtList)((NbtCompound) item.getNbt().get("display")).get("Lore");
                        NbtString itemName = (NbtString)((NbtCompound) item.getNbt().get("display")).get("Name");
                        for(NbtElement lore : itemLore){
                            if(lore.asString().contains("Stored:")){
                                Scanner intParser = new Scanner(lore.asString());
                                intParser.useDelimiter("\"");
                                while(intParser.hasNext()){
                                    if(intParser.hasNextInt()){
                                        put(Utils.removeNbtCharacters(itemName.asString().split("\"text\":\"")[1]),intParser.nextInt());
                                        break;
                                    }
                                    intParser.next();
                                }
                                intParser.close();
                                break;
                            }
                        }
                    }catch(NullPointerException e){
                        //do nothing, this happens sometimes
                    }
                }
            }
            saveSettings();
            return false;
        }catch(NullPointerException e){
            return true;
        }
    }

    public static void createFile(){
        if(Files.notExists(SkyblockImprovements.FILE_LOCATION.resolve("sacks.json"))){
			try{
				Files.writeString(SkyblockImprovements.FILE_LOCATION.resolve("sacks.json"),"",StandardOpenOption.CREATE);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
    }
    public static boolean getFeatureEnabled(){
        return featureEnabled;
    }
    public static void toggleFeature(){
        featureEnabled = !featureEnabled;
    }
    

}
