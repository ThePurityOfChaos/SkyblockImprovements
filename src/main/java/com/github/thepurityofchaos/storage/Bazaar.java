package com.github.thepurityofchaos.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.thepurityofchaos.SkyblockImprovements;
import com.github.thepurityofchaos.abstract_interfaces.Filer;
import com.github.thepurityofchaos.abstract_interfaces.ScreenInteractor;
import com.github.thepurityofchaos.storage.config.Config;
import com.github.thepurityofchaos.utils.NbtUtils;
import com.github.thepurityofchaos.utils.Utils;
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

/**
 * Storage module for the Bazaar.
 * 
 * <p> {@link #init()}: Gets the price maps from bazaar.json.
 * 
 * <p> {@link #processList(List)}: Gets prices from the list, if they exist.
 * 
 * <p> {@link #putInBuy(String, double)}: Puts a price in the buyPrices list.
 * 
 * <p> {@link #putInSell(String, double)}: Puts a price in the sellPrices list.
 * 
 * <p> {@link #putIn7dAvg(String, double)}: Puts a price in the 7dAvgPrices list.
 * 
 * <p> {@link #getBuy(String)}: Gets the price of something from the buyPrices list, or -1.
 * 
 * <p> {@link #getSell(String)}: Gets the price of something from the sellPrices list, or -1.
 * 
 * <p> {@link #saveSettings()}: Writes back the buy and sell prices to bazaar.json.
 * 
 * <p> {@link #createFile()}: Creates bazaar.json.
 * 
 * <p> {@link #interact(Screen)}: Lets processList know when to process.
 */
public class Bazaar implements Filer, ScreenInteractor {
    private static final Logger LOGGER = LoggerFactory.getLogger(Bazaar.class);
    private static Map<String,Double> bazaarBuyPrices = null;
    private static Map<String,Double> bazaarSellPrices = null;
    private static Map<String,Double> bazaar7dAvgPrices = null;

    public static void init(){
        createFile();
        try{
            BufferedReader reader = Files.newBufferedReader(SkyblockImprovements.FILE_LOCATION.resolve("bazaar.json"));
            JsonObject bazaarPrices = JsonParser.parseReader(reader).getAsJsonObject();
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String,Double>>(){}.getType();
            if(bazaarPrices==null) throw new Exception();
            bazaarBuyPrices = gson.fromJson(bazaarPrices.get("BuyPrices"),type);
            bazaarSellPrices = gson.fromJson(bazaarPrices.get("SellPrices"),type);
            bazaar7dAvgPrices = gson.fromJson(bazaarPrices.get("7dAvgPrices"),type);
            if(bazaar7dAvgPrices==null || bazaarBuyPrices==null || bazaarSellPrices==null) throw new Exception();
        }catch(Exception e){
            bazaarBuyPrices = new HashMap<>();
            bazaarSellPrices = new HashMap<>();
            bazaar7dAvgPrices = new HashMap<>();
        }
    }

    public static boolean processList(List<ItemStack> list){
        try{
            //should not go above 54
            for(ItemStack item : list){
                if(item!=null){
                    try{
                        List<Text> itemLore = NbtUtils.getLorefromItemStack(item);
                        for(Text lore : itemLore){
                            String loreString = lore.getString();
                            if(loreString.contains("Buy price:")){
                                Scanner doubleParser = new Scanner(Utils.removeCommas(loreString));
                                while(doubleParser.hasNext()){
                                    if(doubleParser.hasNextDouble()){
                                        putInBuy(Utils.stripSpecial(NbtUtils.getNamefromItemStack(item).getString()).strip(),doubleParser.nextDouble());
                                        continue;
                                    }
                                    doubleParser.next();
                                }
                                doubleParser.close();
                                continue;
                            }else 
                            //this can safely be placed after for a very slight performance boost
                            if(loreString.contains("Sell price:")){
                                Scanner doubleParser = new Scanner(Utils.removeCommas(loreString));
                                while(doubleParser.hasNext()){
                                    if(doubleParser.hasNextDouble()){
                                        putInSell(Utils.stripSpecial(NbtUtils.getNamefromItemStack(item).getString()).strip(),doubleParser.nextDouble());
                                        continue;
                                    }
                                    doubleParser.next();
                                }
                                doubleParser.close();
                                continue;
                            }else
                            if(loreString.contains("7d Avg. price:")){
                                Scanner doubleParser = new Scanner(Utils.removeCommas(loreString));
                                while(doubleParser.hasNext()){
                                    if(doubleParser.hasNextDouble()){
                                        putIn7dAvg(Utils.stripSpecial(NbtUtils.getNamefromItemStack(item).getString()).strip(),doubleParser.nextDouble());
                                        continue;
                                    }
                                    doubleParser.next();
                                }
                                doubleParser.close();
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
    public static void putInBuy(String name, double price){
        bazaarBuyPrices.put(name, price);
    }
    public static void putInSell(String name, double price){
        bazaarSellPrices.put(name, price);
    }
    public static void putIn7dAvg(String name, double price){
        bazaar7dAvgPrices.put(name,price);
    }
    public static double getBuy(String name){
        return bazaarBuyPrices.containsKey(name)?bazaarBuyPrices.get(name):-1;
    }
    public static double getSell(String name){
        return bazaarSellPrices.containsKey(name)?bazaarSellPrices.get(name):-1;
    }
    public static double get7dAvg(String name){
        return bazaar7dAvgPrices.containsKey(name)?bazaar7dAvgPrices.get(name):-1;
    }
    public static void saveSettings(){
        try{
            BufferedWriter writer = Files.newBufferedWriter(SkyblockImprovements.FILE_LOCATION.resolve("bazaar.json"));
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                Map<String,Map<String,Double>> bazaarPrices = new HashMap<>();
                bazaarPrices.put("BuyPrices",bazaarBuyPrices);
                bazaarPrices.put("SellPrices",bazaarSellPrices);
                bazaarPrices.put("7dAvgPrices",bazaar7dAvgPrices);
                writer.write(gson.toJson(bazaarPrices));
                writer.close();
            }catch(IOException e){
                LOGGER.error("[SkyblockImprovements] Bazaar file may be missing. Attempting to recreate...");
                try{
                    Config.createFiles();
                    saveSettings();
                    }catch(IOException ioE){
                        LOGGER.error("[SkyblockImprovements] Something went wrong. Bazaar files may not have permission to save!");
                }
            }
            catch(Exception e){
                LOGGER.error("[SkyblockImprovements] Bazaar failed to save!");
                e.printStackTrace();
            }
    }
    public static void createFile(){
        if(Files.notExists(SkyblockImprovements.FILE_LOCATION.resolve("bazaar.json"))){
			try{
				Files.writeString(SkyblockImprovements.FILE_LOCATION.resolve("bazaar.json"),"",StandardOpenOption.CREATE);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
    }
    public static void interact(Screen screen){
        ScreenEvents.afterTick(screen).register(currentScreen ->{
            Bazaar.processList(InventoryProcessor.processSlotsToList(((GenericContainerScreen)screen).getScreenHandler()));
        });
    }
}
