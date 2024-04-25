package com.github.thepurityofchaos.storage;

import java.util.Map;
import java.util.Scanner;
import java.util.HashMap;
import java.util.List;
import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.thepurityofchaos.SkyblockImprovements;
import com.github.thepurityofchaos.config.Config;
import com.github.thepurityofchaos.interfaces.Filer;
import com.github.thepurityofchaos.utils.Utils;
import com.github.thepurityofchaos.utils.inventory.ChangeInstance;
import com.github.thepurityofchaos.utils.processors.NbtProcessor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;


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
            JsonObject storedSackContents = JsonParser.parseReader(reader).getAsJsonObject();
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String,Integer>>(){}.getType();
            if(storedSackContents==null) throw new Exception();
            allSackContents = gson.fromJson(storedSackContents,type);
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
                try{
                    Config.createFiles();
                    saveSettings();
                    }catch(IOException ioE){
                        LOGGER.error("[SkyblockImprovements] Something went wrong. Sack files may not have permission to save!");
                }
            }
            catch(Exception e){
                LOGGER.error("[SkyblockImprovements] Sacks failed to save!");
                e.printStackTrace();
            }
    }

    public static void update(String strippedMessage, int input){ 
        String splitMessage = Utils.stripSpecial(strippedMessage.split("§")[0]).strip();
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
            //should not go above 54
            for(ItemStack item : list){
                if(item!=null){
                    try{
                        List<Text> itemLore = NbtProcessor.getLorefromItemStack(item);
                        boolean isGem = false;
                        for(Text lore : itemLore){
                            String loreString = lore.getString();
                            //don't use Stored: if this is a Gem Sack
                            if(loreString.contains("Stored:")&& isGem==false){
                                Scanner intParser = new Scanner(Utils.removeCommas(loreString.replace("/"," ")));
                                while(intParser.hasNext()){
                                    if(intParser.hasNextInt()){
                                        put(NbtProcessor.getNamefromItemStack(item).getString(),intParser.nextInt());
                                        continue;
                                    }
                                    intParser.next();
                                }
                                intParser.close();
                                break;
                            }else 
                            //this can safely be placed after for a very slight performance boost
                            if(loreString.contains("Rough:")||loreString.contains("Flawed:")||loreString.contains("Fine:")){
                                isGem=true;
                                Scanner intParser = new Scanner(Utils.removeCommas(Utils.removeText(loreString.replace(":","").replace("/"," "))));
                                while(intParser.hasNext()){
                                    if(intParser.hasNextInt()){
                                        String temp = NbtProcessor.getNamefromItemStack(item).getString();
                                        //Gemstones -> Gemstone
                                        put(loreString.split(":")[0].strip()+" "+(temp.endsWith("s")?temp.substring(0, temp.length()-1):temp),intParser.nextInt());
                                        continue;
                                    }
                                    intParser.next();
                                }
                                intParser.close();
                                continue;
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
