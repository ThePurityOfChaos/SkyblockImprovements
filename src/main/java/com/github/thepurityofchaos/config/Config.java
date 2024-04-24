package com.github.thepurityofchaos.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.thepurityofchaos.SkyblockImprovements;
import com.github.thepurityofchaos.storage.Sacks;

import java.nio.file.Files;
/*
 * The Config holds all of the mod's data currently. Ideally, it should be split up into multiple files in the future.
 */
public class Config {
    private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);
    public static void init(){
        //Create config files if none exist
        createFiles();
        //Restore Config Settings
        try{
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

    public static void createFiles(){
        //Create Directory if none exist
		if(Files.notExists(SkyblockImprovements.FILE_LOCATION)){
			try{
			    Files.createDirectory(SkyblockImprovements.FILE_LOCATION.resolve("sbimp"));
			}catch(Exception e){
				e.printStackTrace();
			}
		}
        IPLConfig.createFile();
        PSConfig.createFile();
    }
}
