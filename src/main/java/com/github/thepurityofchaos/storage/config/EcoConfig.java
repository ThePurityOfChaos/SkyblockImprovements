package com.github.thepurityofchaos.storage.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.thepurityofchaos.SkyblockImprovements;
import com.github.thepurityofchaos.features.economic.BatFirework;
import com.github.thepurityofchaos.features.economic.Bingo;
import com.github.thepurityofchaos.features.economic.GenericProfit;
import com.github.thepurityofchaos.interfaces.Filer;
import com.github.thepurityofchaos.utils.Utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
/**
 * Config for all Economic Widgets. Currently Bat Firework Helper, Math Helper, and Generic Profit Manager.
 * 
 * <p> {@link #init()}: Initializes all settings and subsystems for the Economic Widgets.
 * 
 * <p> {@link #saveSettings()}: Saves settings deemed to be persistent by the developer.
 * 
 * <p> {@link #createFile()}: Creates the file eco.json for persistent storage.
 * 
 * <p> {@link #doMath()}: Getter for the MathHelper toggle.
 * 
 * <p> {@link #getColorCode()}: Standard color code char getter.
 * 
 * <p> {@link #getFeatureEnabled()}: Getter for the toggle for all economic features at once.
 * 
 * <p> {@link #setColorCode(char)}: Standard color code char setter.
 * 
 * <p> {@link #toggleFeature()}: Flips the return value for {@link #getFeatureEnabled()}.
 * 
 * <p> {@link #toggleMath()}: Toggle for {@link #doMath()}.
 * 
 * <p> {@link #updateFeatureVisuals()}: Sets the message for each feature's visual component.
 * 
 * <p> This is a static file and does not have a constructor implemented.
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
        Bingo.init();
        try{
            //create parser based on the client
            BufferedReader reader = Files.newBufferedReader(SkyblockImprovements.FILE_LOCATION.resolve("eco.json"));
            JsonObject parser = JsonParser.parseReader(reader).getAsJsonObject();
                //buttons
                JsonObject buttons = parser.getAsJsonObject("buttons");
                    JsonArray bfDimArray = buttons.getAsJsonArray("Bat");
                    Utils.setDim(BatFirework.getFeatureVisual(),bfDimArray);
                    JsonArray gpDimArray = buttons.getAsJsonArray("GP");
                    Utils.setDim(GenericProfit.getFeatureVisual(), gpDimArray);
                    JsonArray bingoDimArray = buttons.getAsJsonArray("Bingo");
                    Utils.setDim(Bingo.getFeatureVisual(), bingoDimArray);
                //advanced settings
                JsonObject advanced = parser.getAsJsonObject("advanced");
                    colorCode = advanced.get("colorCode").getAsString().charAt(0);
                    math=advanced.get("Math").getAsBoolean();
                    if(advanced.get("Eco").getAsBoolean()) toggleFeature();
                    if(advanced.get("BatFirework").getAsBoolean()) BatFirework.toggleFeature();
                    if(advanced.get("GenericProfit").getAsBoolean()) GenericProfit.toggleFeature();
                    if(advanced.get("Bingo").getAsBoolean()) Bingo.toggleFeature();
                    if(advanced.get("showCommunity").getAsBoolean()) Bingo.toggleCommunity();
                    JsonElement bingoTasks = advanced.get("BingoTasks");
                    Gson gson = new Gson();
                    Type type = new TypeToken<List<Text>>(){}.getType();
                    if(bingoTasks==null) throw new Exception();
                    Bingo.setTasks(gson.fromJson(bingoTasks,type));
                       
            isEnabled = parser.get("enabled").getAsBoolean();
            LOGGER.info("[SkyblockImprovements] Economic Config Imported.");
            updateFeatureVisuals();
        }catch(Exception e){
            LOGGER.error("[SkyblockImprovements] Economic Config failed to load! Was it updated, or was it just created?"); 
            //enable all features
            toggleFeature();
            BatFirework.toggleFeature();
            GenericProfit.toggleFeature();
            EcoConfig.toggleFeature();
            Bingo.toggleFeature();
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
                    advanced.put("Eco",isEnabled);
                    advanced.put("colorCode",colorCode);
                    advanced.put("Math",math);
                    advanced.put("BatFirework",BatFirework.getFeatureEnabled());
                    advanced.put("GenericProfit",GenericProfit.getFeatureEnabled());
                    advanced.put("Bingo",Bingo.getFeatureEnabled());
                    advanced.put("showCommunity",Bingo.showCommunity());
                    advanced.put("BingoTasks",Bingo.getTasks());
                    
                //save button locations here
                ButtonWidget BatWidget = BatFirework.getFeatureVisual();
                ButtonWidget GPWidget = GenericProfit.getFeatureVisual();
                ButtonWidget BingoWidget = Bingo.getFeatureVisual();
                Map<String,Integer[]> EcoButtonLocations = new HashMap<>();
                    Integer[] BatButtonLoc = {BatWidget.getX(),BatWidget.getY(),BatWidget.getWidth(),BatWidget.getHeight()}; 
                    Integer[] GPButtonLoc = {GPWidget.getX(),GPWidget.getY(),GPWidget.getWidth(),GPWidget.getHeight()};
                    Integer[] BingoButtonLoc = {BingoWidget.getX(),BingoWidget.getY(),BingoWidget.getWidth(),BingoWidget.getHeight()};
                    EcoButtonLocations.put("Bat",BatButtonLoc);
                    EcoButtonLocations.put("GP",GPButtonLoc);
                    EcoButtonLocations.put("Bingo",BingoButtonLoc);
            
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
        Bingo.getFeatureVisual().setMessage(Text.of("Bingo Tasks"+Utils.getStringFromBoolean(isEnabled&&Bingo.getFeatureEnabled())));
    }
    public static void setColorCode(char c) {
        colorCode = c;
    }
}
