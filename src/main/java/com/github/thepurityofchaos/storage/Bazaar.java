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
import com.github.thepurityofchaos.config.Config;
import com.github.thepurityofchaos.interfaces.Filer;
import com.github.thepurityofchaos.utils.Utils;
import com.github.thepurityofchaos.utils.processors.NbtProcessor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class Bazaar implements Filer {
    private static final Logger LOGGER = LoggerFactory.getLogger(Bazaar.class);
    private static Map<String,Double> bazaarBuyPrices = null;
    private static Map<String,Double> bazaarSellPrices = null;

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
        }catch(Exception e){
            bazaarBuyPrices = new HashMap<>();
            bazaarSellPrices = new HashMap<>();
        }
    }
    

    public static boolean processList(List<ItemStack> list){
        try{
            //should not go above 54
            for(ItemStack item : list){
                if(item!=null){
                    try{
                        List<Text> itemLore = NbtProcessor.getLorefromItemStack(item);
                        for(Text lore : itemLore){
                            String loreString = lore.getString();
                            if(loreString.contains("Buy price:")){
                                Scanner doubleParser = new Scanner(Utils.removeCommas(loreString));
                                while(doubleParser.hasNext()){
                                    if(doubleParser.hasNextDouble()){
                                        putInBuy(NbtProcessor.getNamefromItemStack(item).getString(),doubleParser.nextDouble());
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
                                        putInSell(NbtProcessor.getNamefromItemStack(item).getString(),doubleParser.nextDouble());
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
    public static double getBuy(String name){
        return bazaarBuyPrices.containsKey(name)?bazaarBuyPrices.get(name):-1;
    }
    public static double getSell(String name){
        return bazaarSellPrices.containsKey(name)?bazaarSellPrices.get(name):-1;
    }
    public static void saveSettings(){
        try{
            BufferedWriter writer = Files.newBufferedWriter(SkyblockImprovements.FILE_LOCATION.resolve("bazaar.json"));
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                Map<String,Map<String,Double>> bazaarPrices = new HashMap<>();
                bazaarPrices.put("BuyPrices",bazaarBuyPrices);
                bazaarPrices.put("SellPrices",bazaarSellPrices);
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
}
