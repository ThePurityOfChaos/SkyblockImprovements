package com.github.thepurityofchaos.storage.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.thepurityofchaos.SkyblockImprovements;
import com.github.thepurityofchaos.storage.Bazaar;
import com.github.thepurityofchaos.storage.Sacks;
import com.google.gson.Gson;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
/**
 *
 * The Config holds many of the init functionalities for the mod.
 * 
 * <p> {@link #init()}: Contains all of the init() functions for features and storage modules, as well as checking the version and debug info.
 * 
 * <p> {@link #saveSettings()}: Calls every other config's saveSettings() command. 
 * 
 * <p> {@link #saveDebug()}: Saves the current debug state.
 * 
 */
public class Config {
    private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);
    /**
     * In this function, call every feature config's init().
     */
    public static void init(){
        //Create config files if none exist
        try{
            createFiles();
        }catch(IOException e){
            LOGGER.error("[SkyblockImprovements] Something went wrong. Config files may not have permission to save!");
        }
        //Restore Config Settings
        try{
                IPLConfig.init();
                PSConfig.init();
                Sacks.init();
                Bazaar.init();
                EcoConfig.init();
                RTConfig.init();
                getVersion();
                getDebug();
                
        }catch(Exception e){
            LOGGER.error("[SkyblockImprovements] Config failed to load! Did a name change, or was it just created?");
        }
        //done restoring config settings
    }
    /**
     * In this function, call every feature config's saveSettings(). 
     */
    public static void saveSettings(){
        IPLConfig.saveSettings();
        PSConfig.saveSettings();
        Sacks.saveSettings();
        Bazaar.saveSettings();
        EcoConfig.saveSettings();
        RTConfig.saveSettings();
    }
    /**
     * Create the directory in which all files are stored, as well as the version.json and debug.json files. Also calls every feature config's createFile().
     * @throws IOException
     */
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
        if(Files.notExists(SkyblockImprovements.FILE_LOCATION.resolve("debug.json"))){
            try{
                Files.writeString(SkyblockImprovements.FILE_LOCATION.resolve("debug.json"),"false",StandardOpenOption.CREATE);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        IPLConfig.createFile();
        PSConfig.createFile();
        Sacks.createFile();
        Bazaar.createFile();
        RTConfig.createFile();
    }
    /**
     * Gets whether or not the version has changed. If it has, call the necessary updateFile() commands and write back the new version.
     */
    private static void getVersion(){
        try{
            BufferedReader reader = Files.newBufferedReader(SkyblockImprovements.FILE_LOCATION.resolve("version.json"));
            String version = JsonParser.parseReader(reader).getAsString();
            boolean hasChanged = false;
            Gson gson = new Gson();
            try{
                if(!version.equals(SkyblockImprovements.VERSION)) hasChanged = true;
            }catch(Exception e){
                hasChanged = true;
            }
            if(hasChanged){
                PSConfig.updateFile();
                BufferedWriter writer = Files.newBufferedWriter(SkyblockImprovements.FILE_LOCATION.resolve("version.json"));
                writer.write(gson.toJson(SkyblockImprovements.VERSION));
                writer.close();
            }
        }catch(Exception e){
           e.printStackTrace();
        }
    }
    /**
     * Gets the current debug state from a file.
     */
    private static void getDebug(){
        try{
            BufferedReader reader = Files.newBufferedReader(SkyblockImprovements.FILE_LOCATION.resolve("debug.json"));
            if(JsonParser.parseReader(reader).getAsBoolean()) SkyblockImprovements.EXPERIMENTAL_TOGGLE_DEBUG_FEATURES();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    /**
     * Writes back the current debug state to a file.
     */
    public static void saveDebug(){
        try{
            BufferedWriter writer = Files.newBufferedWriter(SkyblockImprovements.FILE_LOCATION.resolve("debug.json"));
            Gson gson = new Gson();
            writer.write(gson.toJson(SkyblockImprovements.DEBUG()));
            writer.close();
        }catch(Exception e){
            e.printStackTrace();
        }        
    }
}
