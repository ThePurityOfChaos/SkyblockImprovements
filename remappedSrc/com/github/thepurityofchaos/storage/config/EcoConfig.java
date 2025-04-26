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
import com.github.thepurityofchaos.abstract_interfaces.Filer;
import com.github.thepurityofchaos.features.economic.BatFirework;
import com.github.thepurityofchaos.features.economic.Bingo;
import com.github.thepurityofchaos.features.economic.ChocolateFactory;
import com.github.thepurityofchaos.features.economic.GenericProfit;
import com.github.thepurityofchaos.features.economic.Refinery;
import com.github.thepurityofchaos.features.economic.ReforgeHelper;
import com.github.thepurityofchaos.utils.Utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import net.minecraft.client.gui.widget.ButtonWidget;
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
 * <p> {@link #isEnabled()}: Getter for the toggle for all economic features at once.
 * 
 * <p> {@link #setColorCode(char)}: Standard color code char setter.
 * 
 * <p> {@link #toggle()}: Flips the return value for {@link #isEnabled()}.
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
    public static int mathPrecision = 3;
    /**
     * 
     */
    public static void init(){
        //init all subsystems
        BatFirework bf = BatFirework.getInstance();
        GenericProfit gp = GenericProfit.getInstance();
        Bingo bng = Bingo.getInstance();
        ChocolateFactory cf = ChocolateFactory.getInstance();
        ReforgeHelper rh = ReforgeHelper.getInstance();
        Refinery rf = Refinery.getInstance();
        bf.init();
        gp.init();
        bng.init();
        cf.init();
        rh.init();
        rf.init();
        try{
            //create parser based on the client
            BufferedReader reader = Files.newBufferedReader(SkyblockImprovements.FILE_LOCATION.resolve("eco.json"));
            JsonObject parser = JsonParser.parseReader(reader).getAsJsonObject();
                //buttons
                JsonObject buttons = parser.getAsJsonObject("buttons");
                try{
                    JsonArray bfDimArray = buttons.getAsJsonArray("Bat");
                    Utils.setDim(bf.getFeatureVisual(),bfDimArray);
                }catch(Exception e){}
                try{
                    JsonArray gpDimArray = buttons.getAsJsonArray("GP");
                    Utils.setDim(gp.getFeatureVisual(), gpDimArray);
                }catch(Exception e){}
                try{
                    JsonArray bingoDimArray = buttons.getAsJsonArray("BNG");
                    Utils.setDim(bng.getFeatureVisual(), bingoDimArray);
                }catch(Exception e){}
                try{
                    JsonArray cfDimArray = buttons.getAsJsonArray("CF");
                    Utils.setDim(cf.getFeatureVisual(), cfDimArray);
                }catch(Exception e){}
                try{
                    JsonArray rhDimArray = buttons.getAsJsonArray("RH");
                    Utils.setDim(rh.getFeatureVisual(), rhDimArray);
                }catch(Exception e){}
                try{
                    JsonArray rfDimArray = buttons.getAsJsonArray("Ref");
                    Utils.setDim(rf.getFeatureVisual(),rfDimArray);
                }catch(Exception e){}

                //advanced settings
                JsonObject advanced = parser.getAsJsonObject("advanced");
                    colorCode = advanced.get("colorCode").getAsString().charAt(0);
                    math=advanced.get("Math").getAsBoolean();
                    if(advanced.get("BatFirework").getAsBoolean()) bf.toggle();
                    if(advanced.get("GenericProfit").getAsBoolean()) gp.toggle();
                    if(advanced.get("Bingo").getAsBoolean()) bng.toggle();
                    if(advanced.get("showCommunity").getAsBoolean()) bng.toggleCommunity();
                    JsonElement bingoTasks = advanced.get("BingoTasks");
                    Gson gson = new Gson();
                    Type type = new TypeToken<List<String>>(){}.getType();
                    if(bingoTasks==null) throw new Exception();
                    bng.setTasksFromStrings(gson.fromJson(bingoTasks,type));
                       
            LOGGER.info("[SkyblockImprovements] Economic Config Imported.");
        }catch(Exception e){
            LOGGER.error("[SkyblockImprovements] Economic Config failed to load! Was it updated, or was it just created?"); 
            //enable all features
            bf.toggle();
            gp.toggle();
            bng.toggle();
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
                    advanced.put("BatFirework",BatFirework.getInstance().isEnabled());
                    advanced.put("GenericProfit",GenericProfit.getInstance().isEnabled());
                    advanced.put("Bingo",Bingo.getInstance().isEnabled());
                    advanced.put("showCommunity",Bingo.getInstance().showCommunity());
                    advanced.put("BingoTasks",Bingo.getInstance().getTasksAsStrings());
                    
                //save button locations here
                ButtonWidget BatWidget = BatFirework.getInstance().getFeatureVisual();
                ButtonWidget GPWidget = GenericProfit.getInstance().getFeatureVisual();
                ButtonWidget BingoWidget = Bingo.getInstance().getFeatureVisual();
                ButtonWidget CFWidget = ChocolateFactory.getInstance().getFeatureVisual();
                ButtonWidget RHWidget = ReforgeHelper.getInstance().getFeatureVisual();
                ButtonWidget RFWidget = Refinery.getInstance().getFeatureVisual();
                Map<String,Integer[]> EcoButtonLocations = new HashMap<>();
                    Integer[] BatButtonLoc = {BatWidget.getX(),BatWidget.getY(),BatWidget.getWidth(),BatWidget.getHeight()}; 
                    Integer[] GPButtonLoc = {GPWidget.getX(),GPWidget.getY(),GPWidget.getWidth(),GPWidget.getHeight()};
                    Integer[] BingoButtonLoc = {BingoWidget.getX(),BingoWidget.getY(),BingoWidget.getWidth(),BingoWidget.getHeight()};
                    Integer[] CFButtonLoc = {CFWidget.getX(),CFWidget.getY(),CFWidget.getWidth(),CFWidget.getHeight()};
                    Integer[] RHButtonLoc = {RHWidget.getX(),RHWidget.getY(),RHWidget.getWidth(),RHWidget.getHeight()};
                    Integer[] RFButtonLoc = {RFWidget.getX(),RFWidget.getY(),RFWidget.getWidth(),RFWidget.getHeight()};
                    EcoButtonLocations.put("Bat",BatButtonLoc);
                    EcoButtonLocations.put("GP",GPButtonLoc);
                    EcoButtonLocations.put("BNG",BingoButtonLoc);
                    EcoButtonLocations.put("CF",CFButtonLoc);
                    EcoButtonLocations.put("RH",RHButtonLoc);
                    EcoButtonLocations.put("Ref",RFButtonLoc);
            
            //put all completed options into the main Map    
            configOptions.put("buttons",EcoButtonLocations);
            configOptions.put("advanced",advanced);
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

    public static boolean doMath(){
        return math;
    }
    public static void toggleMath(){
        math = !math;
    }
    public static void setPrecision(int precision){
        mathPrecision = precision;
    }
    public static int getPrecision(){
        return mathPrecision;
    }
    public static char getColorCode(){
        return colorCode;
    }
    public static void setColorCode(char c) {
        colorCode = c;
    }
}
