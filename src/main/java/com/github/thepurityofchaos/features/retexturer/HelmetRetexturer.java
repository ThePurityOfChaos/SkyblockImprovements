package com.github.thepurityofchaos.features.retexturer;

import java.util.Base64;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import com.github.thepurityofchaos.SkyblockImprovements;
import com.github.thepurityofchaos.utils.processors.NbtProcessor;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;

public class HelmetRetexturer {
        //private static int newColor;
        private static boolean recolor = false;
        private String currentTextureURL = null;
        private String previousTextureURL = null;
        private BufferedImage currentTexture = null;
        public static HelmetRetexturer instance = new HelmetRetexturer();
        private int textureNumber = 0;

        public void retextureHelm(ItemStack helmet){
            if(!recolor){
                return;
            }
            if(MinecraftClient.getInstance()==null) return;
            String helmetTextureURL = NbtProcessor.getTextureFromSkull(helmet);
            if(helmetTextureURL == null) return;
            currentTextureURL = getURL(helmetTextureURL);
            
            if(currentTextureURL.equals(previousTextureURL)){
                //draw using stored texture
                getCurrentTexture();
                return;
            }
            previousTextureURL = currentTextureURL;
            //retexture here
            

            //get texture from URL
            downloadTexture();
            getCurrentTexture();
            crop();
            
            //modify texture
            //int[] colors = Utils.intToRGBA(newColor);

            //store texture
            storeCurrentTexture();

            //draw using stored texture
            return;
        }

        @SuppressWarnings("unchecked")
        private String getURL(String helmetTextureURL){
            byte[] decodedTextureURL = Base64.getDecoder().decode(helmetTextureURL);
            String json = new String(decodedTextureURL);
            Gson gson = new Gson();
            Type type = new TypeToken<LinkedTreeMap<String,Object>>(){}.getType();
            LinkedTreeMap<String,Object> urlMap = gson.fromJson(json, type);
            try{
                LinkedTreeMap<String,Object> subMap1 = (LinkedTreeMap<String,Object>)urlMap.get("textures");
                LinkedTreeMap<String,Object> subMap2 = (LinkedTreeMap<String,Object>)subMap1.get("SKIN");
                return (String)subMap2.get("url");
            }catch(Exception e){
                return "";
            }
        }
        private void getCurrentTexture(){
            try{
                currentTexture = ImageIO.read(SkyblockImprovements.FILE_LOCATION.resolve("helms").resolve("helm"+textureNumber+".png").toFile());
            }catch(Exception e){ //will be null sometimes
                currentTexture= null;
            }
        }
        private void storeCurrentTexture(){
            try{
                ImageIO.write(currentTexture, "PNG", SkyblockImprovements.FILE_LOCATION.resolve("helms").resolve("helm"+textureNumber+".png").toFile());
            }catch(Exception e){
                e.printStackTrace();
                return;
            }
        }
        private void downloadTexture(){
            try{
            URL url = new URL(currentTextureURL);
            InputStream input = url.openConnection().getInputStream();
            FileOutputStream output = new FileOutputStream(SkyblockImprovements.FILE_LOCATION.resolve("helms").resolve("helm"+textureNumber+".png").toString());
            ReadableByteChannel channel = Channels.newChannel(input);
            output.getChannel().transferFrom(channel, 0, Integer.MAX_VALUE);
            output.close();
            input.close();
            }catch(Exception e){
                //no texture found!
            }

        }
        private void crop(){
            currentTexture = currentTexture.getSubimage(0, 0, 64, 16);
        }
        public static HelmetRetexturer getDefaultInstance(){
            return instance;
        }



}
