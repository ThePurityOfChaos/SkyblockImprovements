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
import com.github.thepurityofchaos.features.economic.BatFirework;

import com.github.thepurityofchaos.interfaces.Filer;
import com.github.thepurityofchaos.utils.Utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class EcoConfig implements Filer {
    private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);
    private static boolean math = true;
    private static char colorCode = 'e';
    private static boolean isEnabled = false;
    public static void init(){
        //init all subsystems
        BatFirework.init();
        try{
            //create parser based on the client
            BufferedReader reader = Files.newBufferedReader(SkyblockImprovements.FILE_LOCATION.resolve("eco.json"));
            JsonObject parser = JsonParser.parseReader(reader).getAsJsonObject();
                JsonObject buttons = parser.getAsJsonObject("buttons");
                
                    JsonArray bfDimArray = buttons.getAsJsonArray("Bat");
                    //width, height, x, y (inverted). It's weird. I just store it as x,y,width,height.
                    BatFirework.getFeatureVisual().setDimensionsAndPosition(
                        bfDimArray.get(2).getAsInt(),
                        bfDimArray.get(3).getAsInt(),
                        bfDimArray.get(0).getAsInt(),
                        bfDimArray.get(1).getAsInt()
                    );
                //advanced settings
                JsonObject advanced = parser.getAsJsonObject("advanced");
                    colorCode = advanced.get("colorCode").getAsString().charAt(0);
            isEnabled = parser.get("enabled").getAsBoolean();
            LOGGER.info("[SkyblockImprovements] Economic Config Imported.");
            updateFeatureVisuals();
        }catch(Exception e){
            LOGGER.error("[SkyblockImprovements] Economic Config failed to load! Did a name change, or was it just created?"); 
            BatFirework.toggleFeature();
            EcoConfig.toggleFeature();
            updateFeatureVisuals();
        }
    }
    public static void createFile(){
         if(Files.notExists(SkyblockImprovements.FILE_LOCATION.resolve("eco.json"))){
			try{
				Files.writeString(SkyblockImprovements.FILE_LOCATION.resolve("eco.json"),"",StandardOpenOption.CREATE);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
    }
    public static void saveSettings(){
        try{
            BufferedWriter writer = Files.newBufferedWriter(SkyblockImprovements.FILE_LOCATION.resolve("eco.json"));
            Map<String,Object> configOptions = new HashMap<>();
                //save all advanced options here
                Map<String,Object> advanced = new HashMap<>();
                    advanced.put("colorCode",colorCode);
                    advanced.put("Math",math);
                    
                //save button locations here
                ButtonWidget BatWidget = BatFirework.getFeatureVisual();
                Map<String,Integer[]> EcoButtonLocations = new HashMap<>();
                    Integer[] BatButtonLoc = {BatWidget.getX(),BatWidget.getY(),BatWidget.getWidth(),BatWidget.getHeight()}; 
                    EcoButtonLocations.put("Bat",BatButtonLoc);
            
            //put all completed options into the main Map    
            configOptions.put("buttons",EcoButtonLocations);
            configOptions.put("advanced",advanced);
            configOptions.put("enabled",isEnabled);
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
            LOGGER.error("[SkyblockImprovements] EcoConfig failed to save!");
            e.printStackTrace();
        }
    }
    public static boolean getFeatureEnabled(){
        return isEnabled;
    }
    public static void toggleFeature(){
        isEnabled = !isEnabled;
    }

    public static boolean doMath(){
        return math;
    }
    public static void toggleMath(){
        math = !math;
    }
    public static char getColorCode(){
        return colorCode;
    }
    private static void updateFeatureVisuals(){
        BatFirework.getFeatureVisual().setMessage(Text.of("Bat Firework Profit"+Utils.getStringFromBoolean(isEnabled)));
    }
    public static void setColorCode(char c) {
        colorCode = c;
    }
}
