package com.github.thepurityofchaos.storage.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.thepurityofchaos.SkyblockImprovements;
import com.github.thepurityofchaos.abstract_interfaces.Filer;
import com.github.thepurityofchaos.features.itempickuplog.ItemPickupLog;
import com.github.thepurityofchaos.storage.Sacks;
import com.github.thepurityofchaos.utils.Utils;
import com.github.thepurityofchaos.utils.inventory.ChangeInstance;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.client.gui.widget.ButtonWidget;
/**
 * Config for the Item Pickup Log.
 * 
 * <p> {@link #init()}: Loads the IPLConfig from file, if it exists.
 * 
 * <p> {@link #saveSettings()}: Writes the IPLConfig settings back to the file.
 */
public class IPLConfig implements Filer{
    private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);
    private static boolean removeMessage = false;

    public static void init(){
        createFile();
        ItemPickupLog ipl = ItemPickupLog.getInstance();
        try{
            //create parser based on the client
            BufferedReader reader = Files.newBufferedReader(SkyblockImprovements.FILE_LOCATION.resolve("ipl.json"));
            JsonObject parser = JsonParser.parseReader(reader).getAsJsonObject();
                JsonArray dimArray = parser.getAsJsonArray("button");
                
                Utils.setDim(ipl.getFeatureVisual(), dimArray);
                if(parser.get("enabled").getAsBoolean()) ipl.toggle();
                //advanced settings
                JsonObject advanced = parser.getAsJsonObject("advanced");
                    ChangeInstance.setColorCode(advanced.get("colorCode").getAsString().charAt(0));
                    ChangeInstance.setDistance(advanced.get("distance").getAsInt());
                    ChangeInstance.setLifespan(advanced.get("duration").getAsInt());
                    if(advanced.get("showSacks").getAsBoolean()){
                        Sacks.getInstance().toggle();
                    }
                    if(advanced.get("removeMessage").getAsBoolean()){
                        removeMessage = !removeMessage;
                    }
            LOGGER.info("[SkyblockImprovements] Item Pickup Log Config Imported.");
        }catch(Exception e){
            LOGGER.error("[SkyblockImprovements] Item Pickup Log's Config failed to load! Was it updated, or was it just created?"); 
            ipl.toggle();
        }
    }

    public static void saveSettings(){
        try{
            BufferedWriter writer = Files.newBufferedWriter(SkyblockImprovements.FILE_LOCATION.resolve("ipl.json"));
            Map<String,Object> configOptions = new HashMap<>();
                //save all advanced options here
                Map<String,Object> advanced = new HashMap<>();
                    advanced.put("colorCode",ChangeInstance.getColorCode());
                    advanced.put("duration",(int)(ChangeInstance.getMaxLifespan()/1000));
                    advanced.put("distance",ChangeInstance.getDistance());
                    advanced.put("showSacks",Sacks.getInstance().isEnabled());
                    advanced.put("removeMessage",removeMessage);
                //save button location here
                ButtonWidget IPLWidget = ItemPickupLog.getInstance().getFeatureVisual();
                int[] IPLButtonLocations = {IPLWidget.getX(),IPLWidget.getY(),IPLWidget.getWidth(),IPLWidget.getHeight()}; 

            //put all completed options into the main Map    
            configOptions.put("button",IPLButtonLocations);
            configOptions.put("advanced",advanced);
            configOptions.put("enabled",ItemPickupLog.getInstance().isEnabled());
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            
            writer.write(gson.toJson(configOptions));
            writer.close();

        }catch(IOException e){
            LOGGER.error("[SkyblockImprovements] Config files may be missing. Attempting to recreate...");
            try{
            Config.createFiles();
            saveSettings();
            }catch(IOException ioE){
                LOGGER.error("[SkyblockImprovements] Something went wrong. Config files may not have permission to save!");
            }
        }
        catch(Exception e){
            LOGGER.error("[SkyblockImprovements] IPLConfig failed to save!");
            e.printStackTrace();
        }
    }

    public static void createFile(){
        if(Files.notExists(SkyblockImprovements.FILE_LOCATION.resolve("ipl.json"))){
			try{
				Files.writeString(SkyblockImprovements.FILE_LOCATION.resolve("ipl.json"),"",StandardOpenOption.CREATE);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
    }
    public static boolean removeMessage(){
        return removeMessage;
    }
    public static void toggleRemoval(){
        removeMessage = !removeMessage;
    }
}
