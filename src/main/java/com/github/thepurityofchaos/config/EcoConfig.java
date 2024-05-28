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
import com.github.thepurityofchaos.features.economic.GenericProfit;
import com.github.thepurityofchaos.interfaces.Filer;
import com.github.thepurityofchaos.utils.Utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
/**
 * Config for all Economic Widgets. Currently Bat Firework Helper, Math Helper, and Generic Profit Manager.
 * 
 * <p> {@link #init()}: Initializes all settings and subsystems for the Economic Widgets.
 */
public class EcoConfig implements Filer {
    private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);
    private static boolean math = true;
    private static char colorCode = 'e';
    private static boolean isEnabled = false;
    /**
     * 
     */
    public static void init(){
        //init all subsystems
        BatFirework.init();
        GenericProfit.init();
        try{
            //create parser based on the client
            BufferedReader reader = Files.newBufferedReader(SkyblockImprovements.FILE_LOCATION.resolve("eco.json"));
            JsonObject parser = JsonParser.parseReader(reader).getAsJsonObject();
                JsonObject buttons = parser.getAsJsonObject("buttons");
                    JsonArray bfDimArray = buttons.getAsJsonArray("Bat");
                    Utils.setDim(BatFirework.getFeatureVisual(),bfDimArray);
                    JsonArray gpDimArray = buttons.getAsJsonArray("GP");
                    Utils.setDim(GenericProfit.getFeatureVisual(), gpDimArray);
                //advanced settings
                JsonObject advanced = parser.getAsJsonObject("advanced");
                    colorCode = advanced.get("colorCode").getAsString().charAt(0);
                    math=advanced.get("Math").getAsBoolean();
                    if(advanced.get("BatFirework").getAsBoolean()) BatFirework.toggleFeature();
                    if(advanced.get("GenericProfit").getAsBoolean()) GenericProfit.toggleFeature();
                       
            isEnabled = parser.get("enabled").getAsBoolean();
            LOGGER.info("[SkyblockImprovements] Economic Config Imported.");
            updateFeatureVisuals();
        }catch(Exception e){
            LOGGER.error("[SkyblockImprovements] Economic Config failed to load! Was it updated, or was it just created?"); 
            //enable all features
            BatFirework.toggleFeature();
            GenericProfit.toggleFeature();
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
                    advanced.put("BatFirework",BatFirework.getFeatureEnabled());
                    advanced.put("GenericProfit",GenericProfit.getFeatureEnabled());
                    
                //save button locations here
                ButtonWidget BatWidget = BatFirework.getFeatureVisual();
                ButtonWidget GPWidget = GenericProfit.getFeatureVisual();
                Map<String,Integer[]> EcoButtonLocations = new HashMap<>();
                    Integer[] BatButtonLoc = {BatWidget.getX(),BatWidget.getY(),BatWidget.getWidth(),BatWidget.getHeight()}; 
                    Integer[] GPButtonLoc = {GPWidget.getX(),GPWidget.getY(),GPWidget.getWidth(),GPWidget.getHeight()};
                    EcoButtonLocations.put("Bat",BatButtonLoc);
                    EcoButtonLocations.put("GP",GPButtonLoc);
            
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
    
    /** 
     * @return boolean
     */
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
        BatFirework.getFeatureVisual().setMessage(Text.of("Bat Firework Profit"+Utils.getStringFromBoolean(isEnabled&&BatFirework.getFeatureEnabled())));
        GenericProfit.getFeatureVisual().setMessage(Text.of("Generic Profit"+Utils.getStringFromBoolean(isEnabled&&GenericProfit.getFeatureEnabled())));
    }
    public static void setColorCode(char c) {
        colorCode = c;
    }
}
