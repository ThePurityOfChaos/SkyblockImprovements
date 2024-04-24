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
import com.github.thepurityofchaos.features.packswapper.PackSwapper;
import com.github.thepurityofchaos.interfaces.Filer;
import com.github.thepurityofchaos.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;


import net.minecraft.client.gui.widget.ButtonWidget;

import net.minecraft.text.Text;


public class PSConfig implements Filer {
    private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);
    private static boolean isEnabled = true;

    public static void init(){
        createFile();
        try{
            //create parser based on the client
            BufferedReader reader = Files.newBufferedReader(SkyblockImprovements.FILE_LOCATION.resolve("ps.json"));
            JsonObject parser = JsonParser.parseReader(reader).getAsJsonObject();
            Gson gson = new Gson();
                //button
                JsonArray dimArray = parser.getAsJsonArray("button");
                    PackSwapper.getFeatureVisual().setDimensionsAndPosition(
                        dimArray.get(2).getAsInt(),
                        dimArray.get(3).getAsInt(),
                        dimArray.get(0).getAsInt(),
                        dimArray.get(1).getAsInt()
                    );
                    
                //advanced settings    
                JsonObject advanced = parser.getAsJsonObject("advanced");
                    PackSwapper.setRegionColor(advanced.get("colorCode").getAsString().charAt(0));
                    if(!advanced.get("showPackName").getAsBoolean())
                        PackSwapper.togglePackHelper();
                    if(!advanced.get("renderComponent").getAsBoolean())
                        PackSwapper.toggleRenderComponent();
                    if(!advanced.get("debugInfo").getAsBoolean())
                        PackSwapper.toggleDebugInfo();
                    isEnabled = parser.get("enabled").getAsBoolean();
                
                //customizeable map for areas & regions
                Type bigMap = new TypeToken<Map<String,Map<String,Map<String,Boolean>>>>(){}.getType();
                if(parser.getAsJsonObject("allRegions")==null) throw new Exception();
                PackSwapper.loadPackAreaRegionToggles(gson.fromJson(parser.getAsJsonObject("allRegions"),bigMap));
            LOGGER.info("[SkyblockImprovements] Pack Swapper Config Imported.");
            updateFeatureVisuals();
        }catch(Exception e){
            LOGGER.error("[SkyblockImprovements] Pack Swapper's Config failed to load! Did a name change, or was it just created?");

            PackSwapper.loadPackAreaRegionToggles(loadDefaultMap());
            updateFeatureVisuals();
        }
    }

    public static void saveSettings(){
        try{
        BufferedWriter writer = Files.newBufferedWriter(SkyblockImprovements.FILE_LOCATION.resolve("ps.json"));
            Map<String,Object> configOptions = new HashMap<>();
                //save all advanced options here
                Map<String,Object> advanced = new HashMap<>();
                    //save PackSwapper's advanced options
                    advanced.put("colorCode",PackSwapper.getRegionColor());
                    advanced.put("showPackName",PackSwapper.showPackHelper());
                    advanced.put("renderComponent",PackSwapper.isRendering());
                    advanced.put("debugInfo",PackSwapper.sendDebugInfo());
                //save all button locations here
                   
                ButtonWidget button = PackSwapper.getFeatureVisual(); 
                int[] PSButtonLocations = {button.getX(),button.getY(),button.getWidth(),button.getHeight()};

            //put all the config options into the main Map
            configOptions.put("button",PSButtonLocations);
            configOptions.put("advanced",advanced);
            configOptions.put("enabled",isEnabled);
            configOptions.put("allRegions",PackSwapper.getFullRegionMap());
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            writer.write(gson.toJson(configOptions));
            writer.close();
            /*
            BufferedWriter DEBUG_WRITER = Files.newBufferedWriter(SkyblockImprovements.FILE_LOCATION.resolve("TEMP_REGIONS.json"));
            DEBUG_WRITER.write(gson.toJson(PackSwapper.DEBUG_GETALLREGIONS()));
            DEBUG_WRITER.close();
            */

        }catch(IOException e){
            LOGGER.error("[SkyblockImprovements] PSConfig file may be missing. Attempting to recreate...");
            createFile();
            saveSettings();
        }
        catch(Exception e){
            LOGGER.error("[SkyblockImprovements] PSConfig failed to save!");
            e.printStackTrace();
        }
    }

    public static void createFile(){
        if(Files.notExists(SkyblockImprovements.FILE_LOCATION.resolve("ps.json"))){
			try{
				Files.writeString(SkyblockImprovements.FILE_LOCATION.resolve("ps.json"),"",StandardOpenOption.CREATE);
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
        LOGGER.info("[SkyblockImprovements] Pack Swapper Toggled.");
    }
    private static void updateFeatureVisuals(){
        PackSwapper.getFeatureVisual().setMessage(Text.of("Pack Swapper"+Utils.getStringFromBoolean(isEnabled)));
    }
    private static Map<String,Map<String,Map<String,Boolean>>> loadDefaultMap(){
        return new HashMap<>();
    }
}
