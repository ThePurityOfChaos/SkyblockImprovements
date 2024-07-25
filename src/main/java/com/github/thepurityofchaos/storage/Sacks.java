package com.github.thepurityofchaos.storage;

import java.util.Map;
import java.util.Scanner;
import java.util.HashMap;
import java.util.List;
import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.thepurityofchaos.SkyblockImprovements;
import com.github.thepurityofchaos.abstract_interfaces.Filer;
import com.github.thepurityofchaos.abstract_interfaces.ScreenInteractor;
import com.github.thepurityofchaos.abstract_interfaces.Toggleable;
import com.github.thepurityofchaos.features.economic.GenericProfit;
import com.github.thepurityofchaos.storage.config.Config;
import com.github.thepurityofchaos.utils.NbtUtils;
import com.github.thepurityofchaos.utils.Utils;
import com.github.thepurityofchaos.utils.inventory.ChangeInstance;
import com.github.thepurityofchaos.utils.processors.InventoryProcessor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
/**
 * Storage module for Sack amounts.
 * <p> {@link #init()}: Gets the amounts from sacks.json.
 * 
 * <p> {@link #saveSettings()}: Writes back the amounts to sacks.json.
 * 
 * <p> {@link #update(String, int)}: Safely adds an item to the contents.
 * 
 * <p> {@link #put(String, int)}: Directly adds an item to the contents.
 * 
 * <p> {@link #get(String)}: Gets an item's amount from the contents.
 * 
 * <p> {@link #processList(List)}: Processes a list of itemstacks into sacks.
 * 
 * <p> {@link #interact(Screen)}: Determines when to process Sacks.
 * 
 * <p> {@link #createFile()}: Creates sacks.json.
 * 
 * <p> {@link #dataArrived()}: Returns if data has arrived or not.
 * 
 * <p> {@link #newData()}: dataArrived = false.
 * 
 * <p> {@link #ticksSinceData()}: Returns the number of ticks since the data arrived.
 * 
 * <p> {@link #tickData()}: ticksSinceData++.
 */
public class Sacks extends Toggleable implements Filer, ScreenInteractor{
    private final Logger LOGGER = LoggerFactory.getLogger(Sacks.class);
    private Map<String,Integer> allSackContents = null;
    private boolean dataArrived = false;
    private int ticksSinceData = 0;
    private static Sacks instance = new Sacks();
    public void init(){
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
    
    public void saveSettings(){
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

    public void update(String strippedMessage, int input){ 
        String splitMessage = Utils.stripSpecial(strippedMessage.split("§")[0]).strip();
        allSackContents.put(splitMessage,allSackContents.getOrDefault(splitMessage, 0)+input);
        GenericProfit.getInstance().add(splitMessage, input);
    }
    public void put(String type, int amount){
        allSackContents.put(type,amount);
    }
    public String get(String strippedMessage){
        String splitMessage = strippedMessage.split("§")[0].strip().replaceAll("-[0-9]","").strip();
        try{
        return " ("+Utils.getColorString(ChangeInstance.getColorCode())+allSackContents.getOrDefault(splitMessage,0).toString()+"§8)";
        }catch(NullPointerException e){
            return "";
        }
    }
    public boolean processList(List<ItemStack> list){
        try{
            //should not go above 54
            for(ItemStack item : list){
                if(item!=null){
                    try{
                        List<Text> itemLore = NbtUtils.getLorefromItemStack(item);
                        boolean isGem = false;
                        for(Text lore : itemLore){
                            String loreString = lore.getString();
                            //don't use Stored: if this is a Gem Sack
                            if(loreString.contains("Stored:")&& isGem==false){
                                dataArrived = true;
                                Scanner intParser = new Scanner(Utils.removeCommas(loreString.replace("/"," ")));
                                while(intParser.hasNext()){
                                    if(intParser.hasNextInt()){
                                        put(NbtUtils.getNamefromItemStack(item).getString(),intParser.nextInt());
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
                                dataArrived = true;
                                Scanner intParser = new Scanner(Utils.removeCommas(Utils.removeText(loreString.replace(":","").replace("/"," "))));
                                while(intParser.hasNext()){
                                    if(intParser.hasNextInt()){
                                        String temp = NbtUtils.getNamefromItemStack(item).getString();
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
    public void interact(Screen screen){
        instance.newData();
        ScreenEvents.afterTick(screen).register(currentScreen -> {
            if(ticksSinceData() < 5 ||!dataArrived){
                processList(InventoryProcessor.processSlotsToList(((GenericContainerScreen)screen).getScreenHandler()));
                saveSettings();
                tickData();
            }
        });
    }

    public void createFile(){
        if(Files.notExists(SkyblockImprovements.FILE_LOCATION.resolve("sacks.json"))){
			try{
				Files.writeString(SkyblockImprovements.FILE_LOCATION.resolve("sacks.json"),"",StandardOpenOption.CREATE);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
    }
    public boolean dataArrived(){
        return dataArrived;
    }
    public void newData(){
        dataArrived = false;
    }
    public int ticksSinceData(){
        return ticksSinceData;
    }
    public void tickData(){
        ticksSinceData++;
    }
    public static Sacks getInstance(){
        return instance;
    }

}
