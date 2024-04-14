package com.github.thepurityofchaos.config;

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
import com.github.thepurityofchaos.features.itempickuplog.ItemPickupLog;
import com.github.thepurityofchaos.interfaces.Filer;
import com.github.thepurityofchaos.utils.Utils;
import com.github.thepurityofchaos.utils.inventory.ChangeInstance;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class IPLConfig implements Filer{
    private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);
    private static boolean isEnabled = true;

    public static void init(){
        createFile();
        try{
            //create parser based on the client
            BufferedReader reader = Files.newBufferedReader(SkyblockImprovements.FILE_LOCATION.resolve("ipl.json"));
            JsonObject parser = JsonParser.parseReader(reader).getAsJsonObject();
                JsonArray dimArray = parser.getAsJsonArray("button");
                //width, height, x, y (inverted). It's weird. I just store it as x,y,width,height.
                ItemPickupLog.getFeatureVisual().setDimensionsAndPosition(
                    dimArray.get(2).getAsInt(),
                    dimArray.get(3).getAsInt(),
                    dimArray.get(0).getAsInt(),
                    dimArray.get(1).getAsInt()
                );
                //advanced settings
                JsonObject advanced = parser.getAsJsonObject("advanced");
                    ChangeInstance.setColorCode(advanced.get("colorCode").getAsString().charAt(0));
                    ChangeInstance.setDistance(advanced.get("distance").getAsInt());
                    ChangeInstance.setLifespan(advanced.get("duration").getAsInt());
            isEnabled = parser.get("enabled").getAsBoolean();
            LOGGER.info("[SkyblockImprovements] Item Pickup Log Config Imported.");
            updateFeatureVisuals();
        }catch(Exception e){
            LOGGER.error("[SkyblockImprovements] Item Pickup Log's Config failed to load! Did a name change, or was it just created?"); 
            updateFeatureVisuals();
        }
    }

    public static void saveSettings(){
        try{
            BufferedWriter writer = Files.newBufferedWriter(SkyblockImprovements.FILE_LOCATION.resolve("ipl.json"));
            Map<String,Object> configOptions = new HashMap<>();
                //save all advanced options here
                Map<String,Object> advanced = new HashMap<>();
                    advanced.put("colorCode",ChangeInstance.getColorCode());
                    advanced.put("duration",(int)ChangeInstance.getMaxLifespan()/1000);
                    advanced.put("distance",ChangeInstance.getDistance());
                //save button location here
                ButtonWidget IPLWidget = ItemPickupLog.getFeatureVisual();
                int[] IPLButtonLocations = {IPLWidget.getX(),IPLWidget.getY(),IPLWidget.getWidth(),IPLWidget.getHeight()}; 

            //put all completed options into the main Map    
            configOptions.put("button",IPLButtonLocations);
            configOptions.put("advanced",advanced);
            configOptions.put("enabled",isEnabled);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            
            writer.write(gson.toJson(configOptions));
            writer.close();

        }catch(IOException e){
            LOGGER.error("[SkyblockImprovements] IPLConfig file may be missing. Attempting to recreate...");
            createFile();
            saveSettings();
        }
        catch(Exception e){
            LOGGER.error("[SkyblockImprovements] TPLConfig failed to save!");
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
    public static boolean getFeatureEnabled(){
		return isEnabled;
    }
    public static void toggleFeature(){
        isEnabled = !isEnabled;
        updateFeatureVisuals();
        LOGGER.info("[SkyblockImprovements] Item Pickup Log Toggled.");
    }
    private static void updateFeatureVisuals(){
        ItemPickupLog.getFeatureVisual().setMessage(Text.of("Item Pickup Log"+Utils.getStringFromBoolean(isEnabled)));
    }

}