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
import com.github.thepurityofchaos.features.packswapper.PackSwapper;
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

/**
 * Config for the Pack Swapper.
 * 
 * <p> {@link #init()}: Gets all info from ps.json.
 * 
 * <p> {@link #saveSettings()}: Saves settings back to ps.json.
 * 
 * <p> {@link #createFile()}: Creates ps.json if it does not exist.
 * 
 * <p> {@link #getFeatureEnabled()}: Is the Pack Swapper enabled?
 * 
 * <p> {@link #toggleFeature()}: Toggles whether it is enabled or not.
 * 
 * <p> {@link #updateFile()}: Tells the Pack Swapper to update its areas and regions, something might have changed.
 */
public class PSConfig implements Filer {
    private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);

    public static void init(){
        createFile();
        PackSwapper ps = PackSwapper.getInstance();
        try{
            //create parser based on the client
            BufferedReader reader = Files.newBufferedReader(SkyblockImprovements.FILE_LOCATION.resolve("ps.json"));
            JsonObject parser = JsonParser.parseReader(reader).getAsJsonObject();
            Gson gson = new Gson();
                //button
                JsonArray dimArray = parser.getAsJsonArray("button");
                Utils.setDim(ps.getFeatureVisual(), dimArray);
                    
                //advanced settings    
                JsonObject advanced = parser.getAsJsonObject("advanced");
                    ps.setRegionColor(advanced.get("colorCode").getAsString().charAt(0));
                    if(!advanced.get("showPackName").getAsBoolean())
                        ps.togglePackHelper();
                    if(!advanced.get("renderComponent").getAsBoolean())
                        ps.toggleRenderComponent();
                    if(!advanced.get("debugInfo").getAsBoolean())
                        ps.toggleDebugInfo();
                    if(parser.get("enabled").getAsBoolean()) ps.toggle();
                
                //customizeable map for areas & regions
                Type bigMap = new TypeToken<Map<String,Map<String,Map<String,Boolean>>>>(){}.getType();
                if(parser.getAsJsonObject("allRegions")==null) throw new Exception();
                ps.loadPackAreaRegionToggles(gson.fromJson(parser.getAsJsonObject("allRegions"),bigMap));
            LOGGER.info("[SkyblockImprovements] Pack Swapper Config Imported.");
            updateFeatureVisuals();
        }catch(Exception e){
            LOGGER.error("[SkyblockImprovements] Pack Swapper's Config failed to load! Was it updated, or was it just created?");

            ps.loadPackAreaRegionToggles(loadDefaultMap());
            updateFeatureVisuals();
        }
    }
    public static void saveSettings(){
        PackSwapper ps = PackSwapper.getInstance();
        try{
        BufferedWriter writer = Files.newBufferedWriter(SkyblockImprovements.FILE_LOCATION.resolve("ps.json"));
            Map<String,Object> configOptions = new HashMap<>();
                //save all advanced options here
                Map<String,Object> advanced = new HashMap<>();
                    //save PackSwapper's advanced options
                    advanced.put("colorCode",ps.getRegionColor());
                    advanced.put("showPackName",ps.showPackHelper());
                    advanced.put("renderComponent",ps.isRendering());
                    advanced.put("debugInfo",ps.sendDebugInfo());
                //save all button locations here
                   
                ButtonWidget button = ps.getFeatureVisual(); 
                int[] PSButtonLocations = {button.getX(),button.getY(),button.getWidth(),button.getHeight()};

            //put all the config options into the main Map
            configOptions.put("button",PSButtonLocations);
            configOptions.put("advanced",advanced);
            configOptions.put("enabled",ps.isEnabled());
            configOptions.put("allRegions",ps.getFullRegionMap());
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            writer.write(gson.toJson(configOptions));
            writer.close();
            /*
            BufferedWriter DEBUG_WRITER = Files.newBufferedWriter(SkyblockImprovements.FILE_LOCATION.resolve("TEMP_REGIONS.json"));
            DEBUG_WRITER.write(gson.toJson(ps.DEBUG_GETALLREGIONS()));
            DEBUG_WRITER.close();
            */

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
    private static void updateFeatureVisuals(){
        PackSwapper.getInstance().getFeatureVisual().setMessage(Text.of("Pack Swapper"));
    }
    private static Map<String,Map<String,Map<String,Boolean>>> loadDefaultMap(){
        return new HashMap<>();
    }
    public static void updateFile(){
        //I love ConcurrentModificationExceptions :D
        PackSwapper.getInstance().needsUpdate(); 
    }
}
