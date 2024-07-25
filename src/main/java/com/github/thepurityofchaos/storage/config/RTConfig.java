package com.github.thepurityofchaos.storage.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.thepurityofchaos.SkyblockImprovements;
import com.github.thepurityofchaos.features.retexturer.Retexturer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;


/**
 * Config for the Retexturer.
 * 
 * <p> {@link #init()}: Gets settings from rt.json.
 * 
 * <p> {@link #createFile()}: Creates rt.json.
 * 
 * <p> {@link #saveSettings()}: Writes back settings to rt.json.
 */
public class RTConfig {
	private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);
    public static void init() {
        createFile();
		Retexturer rt = Retexturer.getInstance();
        try{
			BufferedReader reader = Files.newBufferedReader(SkyblockImprovements.FILE_LOCATION.resolve("rt.json"));
            JsonObject parser = JsonParser.parseReader(reader).getAsJsonObject();
            if(parser.get("enabled").getAsBoolean()) rt.toggleRecolor();
			Gson gson = new Gson();
			Type helmMap = new TypeToken<Map<String,List<String>>>(){}.getType();
			Map<String,List<String>> knownHelms = gson.fromJson(parser.get("knownHelms"), helmMap);
			if(knownHelms!=null) rt.setKnownHelms(knownHelms);
			JsonObject advanced = parser.getAsJsonObject("advanced");
				rt.changeColor(advanced.get("color").getAsInt());
				rt.changeK(advanced.get("k").getAsInt());
			LOGGER.info("[SkyblockImprovements] Helmet Info Imported.");
		}catch(Exception e){
			LOGGER.error("[SkyblockImprovements] Helmet Info failed to load!");
			rt.toggleRecolor();
		}
    }
    public static void createFile(){
        if(Files.notExists(SkyblockImprovements.FILE_LOCATION.resolve("helms"))){
			try{
			    Files.createDirectory(SkyblockImprovements.FILE_LOCATION.resolve("helms"));
			}catch(Exception e){
				e.printStackTrace();
			}
		}
        if(Files.notExists(SkyblockImprovements.FILE_LOCATION.resolve("rt.json"))){
			try{
			    Files.createFile(SkyblockImprovements.FILE_LOCATION.resolve("rt.json"));
			}catch(Exception e){
				e.printStackTrace();
			}
		}
    }
    public static void saveSettings(){
		try{
			Retexturer rt = Retexturer.getInstance();
            BufferedWriter writer = Files.newBufferedWriter(SkyblockImprovements.FILE_LOCATION.resolve("rt.json"));
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
				Map<String,Object> configOptions = new HashMap<>();
					configOptions.put("enabled",rt.getFeatureEnabled());
					configOptions.put("knownHelms",rt.getKnownHelms());
					Map<String,Object> advanced = new HashMap<>();
						advanced.put("color",rt.getColorCode());
						advanced.put("k",rt.getK());
					configOptions.put("advanced",advanced);
                writer.write(gson.toJson(configOptions));
                writer.close();
            }catch(IOException e){
                LOGGER.error("[SkyblockImprovements] Helmet Info file may be missing. Attempting to recreate...");
                try{
                    Config.createFiles();
                    saveSettings();
                    }catch(IOException ioE){
                        LOGGER.error("[SkyblockImprovements] Something went wrong. Helmet Info files may not have permission to save!");
                }
            }
            catch(Exception e){
                LOGGER.error("[SkyblockImprovements] Helmet Info failed to save!");
                e.printStackTrace();
        }
    }
}
