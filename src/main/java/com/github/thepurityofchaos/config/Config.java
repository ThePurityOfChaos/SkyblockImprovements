package com.github.thepurityofchaos.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.thepurityofchaos.SkyblockImprovements;
import com.github.thepurityofchaos.storage.Sacks;
import com.google.gson.Gson;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
/*
 * The Config holds the init functionalities for the mod, as well as the mod's Version. 
 * Ideally, the currentVersion should match the version found in gradle.properties, 
 * but this has not yet been researched.
 */
public class Config {
    private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);
    private static String currentVersion = "0.2.1";
    public static void init(){
        //Create config files if none exist
        try{
            createFiles();
        }catch(IOException e){
            LOGGER.error("[SkyblockImprovements] Something went wrong. Config files may not have permission to save!");
        }
        //Restore Config Settings
        try{
                getVersion();
                IPLConfig.init();
                PSConfig.init();
                Sacks.init();

                
        }catch(Exception e){
            LOGGER.error("[SkyblockImprovements] Config failed to load! Did a name change, or was it just created?");
        }
        //done restoring config settings
    }
    public static void saveSettings(){
        IPLConfig.saveSettings();
        PSConfig.saveSettings();
        Sacks.saveSettings();
    }

    public static void createFiles() throws IOException{
        //Create Directory if none exist
		if(Files.notExists(SkyblockImprovements.FILE_LOCATION)){
			try{
			    Files.createDirectory(SkyblockImprovements.FILE_LOCATION);
			}catch(Exception e){
				throw new IOException();
			}
		}
        //Create Mod Files if none exist
        if(Files.notExists(SkyblockImprovements.FILE_LOCATION.resolve("version.json"))){
            try{
                Files.writeString(SkyblockImprovements.FILE_LOCATION.resolve("version.json"),"",StandardOpenOption.CREATE);
                Gson gson = new Gson();
                BufferedWriter writer = Files.newBufferedWriter(SkyblockImprovements.FILE_LOCATION.resolve("version.json"));
                writer.write(gson.toJson(-1));
                writer.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        IPLConfig.createFile();
        PSConfig.createFile();
        Sacks.createFile();
    }
    private static void getVersion(){
        try{
            BufferedReader reader = Files.newBufferedReader(SkyblockImprovements.FILE_LOCATION.resolve("version.json"));
            String version = JsonParser.parseReader(reader).getAsString();
            boolean hasChanged = false;
            Gson gson = new Gson();
            try{
                if(!version.equals(currentVersion)) hasChanged = true;
            }catch(Exception e){
                hasChanged = true;
            }
            if(hasChanged){
            BufferedWriter writer = Files.newBufferedWriter(SkyblockImprovements.FILE_LOCATION.resolve("version.json"));
            writer.write(gson.toJson(currentVersion));
            writer.close();
            Files.deleteIfExists(SkyblockImprovements.FILE_LOCATION.resolve("ps.json"));
            Files.deleteIfExists(SkyblockImprovements.FILE_LOCATION.resolve("sacks.json"));
            }

        }catch(Exception e){
           e.printStackTrace();
        }
    }
}
