package com.github.thepurityofchaos.config;

import java.io.IOException;
import java.nio.file.Files;

import com.github.thepurityofchaos.SkyblockImprovements;

public class RTConfig {
    public static void init(){
        
    }
    public static void createFile() throws IOException{
        if(Files.notExists(SkyblockImprovements.FILE_LOCATION.resolve("helms"))){
			try{
			    Files.createDirectory(SkyblockImprovements.FILE_LOCATION.resolve("helms"));
			}catch(Exception e){
				throw new IOException();
			}
		}
    }
    public static void saveSettings(){

    }
}
