package com.github.thepurityofchaos.config;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.thepurityofchaos.features.itempickuplog.ItemPickupLog;
import com.github.thepurityofchaos.features.packswapper.PackSwapper;
import com.github.thepurityofchaos.utils.inventory.ChangeInstance;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.HashMap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
/*
 * The Config holds all of the mod's data currently. Ideally, it should be split up into multiple files in the future.
 */
public class Config {
    private static Map<String,Boolean> toggles = null;
    private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);
    public static void init(){
        //Create config files if none exist
        createFiles();

        //Restore Config Settings from config.json
        toggles = new HashMap<>();
        try{
            //create parser based on the client
            BufferedReader reader = Files.newBufferedReader(FabricLoader.getInstance().getConfigDir().resolve("sbimp").resolve("config.json"));
            JsonObject parser = JsonParser.parseReader(reader).getAsJsonObject();

                //parse toggles
                toggles.put("ItemPickupLog", parser.getAsJsonObject("toggles").get("ItemPickupLog").getAsBoolean());
                toggles.put("PackSwapper",parser.getAsJsonObject("toggles").get("PackSwapper").getAsBoolean());

                //parse button locations
                JsonObject buttons = parser.getAsJsonObject("buttons");
                    //Item Pickup Log
                    JsonArray IPLdimArray = buttons.getAsJsonArray("IPL");
                        //width, height, x, y (inverted). It's weird. I just store it as x,y,width,height.
                        ItemPickupLog.getFeatureVisual().setDimensionsAndPosition(
                            IPLdimArray.get(2).getAsInt(),
                            IPLdimArray.get(3).getAsInt(),
                            IPLdimArray.get(0).getAsInt(),
                            IPLdimArray.get(1).getAsInt()
                        );
                    //Pack Swapper    
                    JsonArray PSdimArray = buttons.getAsJsonArray("PS");
                        PackSwapper.getFeatureVisual().setDimensionsAndPosition(
                            PSdimArray.get(2).getAsInt(),
                            PSdimArray.get(3).getAsInt(),
                            PSdimArray.get(0).getAsInt(),
                            PSdimArray.get(1).getAsInt()
                        );

                //advanced settings
                JsonObject advancedSettings = parser.getAsJsonObject("advanced");
                    //Item Pickup Log
                    JsonObject advancedIPLSettings = advancedSettings.getAsJsonObject("IPL");
                        ChangeInstance.setColorCode(advancedIPLSettings.get("colorCode").getAsString().charAt(0));
                        ChangeInstance.setDistance(advancedIPLSettings.get("distance").getAsInt());
                        ChangeInstance.setLifespan(advancedIPLSettings.get("duration").getAsInt());
                    //Pack Swapper
                    JsonObject advancedPSSettings = advancedSettings.getAsJsonObject("PS");
                        PackSwapper.setRegionColor(advancedPSSettings.get("colorCode").getAsString().charAt(0));
                        if(advancedPSSettings.get("showPackName").getAsBoolean())
                            PackSwapper.togglePackHelper();
                        updateFeatureVisuals();
            LOGGER.info("[SkyblockImprovements] Config Imported.");
        }catch(Exception e){
            //defaults
            LOGGER.error("[SkyblockImprovements] Config failed to load! Did a name change, or was it just created?");
            toggles.put("ItemPickupLog", true);
            toggles.put("PackSwapper",true);
            PackSwapper.togglePackHelper();
            updateFeatureVisuals();
        }
        //done restoring config settings
    }
    public static void saveSettings(){
        //Save settings to config.json
        try{
            BufferedWriter writer = Files.newBufferedWriter(FabricLoader.getInstance().getConfigDir().resolve("sbimp").resolve("config.json"));
            Map<String,Object> configOptions = new HashMap<>();
                //save all advanced options here
                Map<String,Object> advanced = new HashMap<>();
                    //save Item Pickup Log's advanced options
                    Map<String,Object> IPL = new HashMap<>();
                    IPL.put("colorCode",ChangeInstance.getColorCode());
                    IPL.put("duration",(int)ChangeInstance.getMaxLifespan()/1000);
                    IPL.put("distance",ChangeInstance.getDistance());
                advanced.put("IPL",IPL);
                    //save PackSwapper's advanced options
                    Map<String,Object> PS = new HashMap<>();
                    PS.put("colorCode",PackSwapper.getRegionColor());
                    PS.put("showPackName",PackSwapper.showPackHelper());
                advanced.put("PS",PS);
                //save all button locations here
                Map<String,Object> buttons = new HashMap<>();
                    ButtonWidget IPLWidget = ItemPickupLog.getFeatureVisual();
                    ButtonWidget PSWidget = PackSwapper.getFeatureVisual();
                    int[] IPLButtonLocations = {IPLWidget.getX(),IPLWidget.getY(),IPLWidget.getWidth(),IPLWidget.getHeight()}; 
                    int[] PSButtonLocations = {PSWidget.getX(),PSWidget.getY(),PSWidget.getWidth(),PSWidget.getHeight()};
                buttons.put("IPL",IPLButtonLocations);
                buttons.put("PS",PSButtonLocations);
                

            //put all sub-Maps into the main Map    
            configOptions.put("buttons",buttons);
            configOptions.put("toggles",toggles);
            configOptions.put("advanced",advanced);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            
            writer.write(gson.toJson(configOptions));
            writer.close();

        }catch(IOException e){
            LOGGER.error("[SkyblockImprovements] Config files may be missing. Attempting to recreate...");
            Config.createFiles();
            saveSettings();
        }
        catch(Exception e){
            LOGGER.error("[SkyblockImprovements] Config failed to save!");
            e.printStackTrace();
        }
        //done saving config settings
    }

    public static void toggleFeature(String feature){
        if(toggles==null) return;
		toggles.put(feature,!toggles.get(feature));
        updateFeatureVisuals();
        LOGGER.info(feature+" Toggled.");
	}
	public static boolean getFeatureEnabled(String feature){
        if(toggles==null) return false;
		return toggles.get(feature);
    }
    public static String getStringFromBoolean(boolean b){
        return b?" [ON] ":" [OFF] ";
    }

    //This must be updated when a new feature is added
    private static void updateFeatureVisuals(){
        try{
        ItemPickupLog.getFeatureVisual().setMessage(Text.of("Item Pickup Log"+getStringFromBoolean(toggles.get("ItemPickupLog"))));
        PackSwapper.getFeatureVisual().setMessage(Text.of("Pack Swapper"+getStringFromBoolean(toggles.get("PackSwapper"))));
        }catch(Exception e){
            LOGGER.error("[SkyblockImprovements] Feature visuals failed to update! Is there a problematic name?");
        }
    }

    public static void createFiles(){
        //Create Config files if none exist
		if(Files.notExists(FabricLoader.getInstance().getConfigDir().resolve("sbimp"))){
			try{
			    Files.createDirectory(FabricLoader.getInstance().getConfigDir().resolve("sbimp"));
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		if(Files.notExists(FabricLoader.getInstance().getConfigDir().resolve("sbimp").resolve("config.json"))){
			try{
				Files.writeString(FabricLoader.getInstance().getConfigDir().resolve("sbimp").resolve("config.json"),"",StandardOpenOption.CREATE);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
    }
}
