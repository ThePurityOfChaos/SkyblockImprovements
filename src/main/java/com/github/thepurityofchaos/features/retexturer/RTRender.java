package com.github.thepurityofchaos.features.retexturer;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;



import com.github.thepurityofchaos.SkyblockImprovements;
import com.github.thepurityofchaos.utils.processors.InventoryProcessor;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;


import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;

import net.minecraft.util.Identifier;

/**
 * Rendering component for the Retexturer.
 * <p> : Returns the retextured RenderLayer, if applicable. Otherwise, returns what it would without this.
 * 
 * <p> {@link #getKnownIdentifiers()}: Gets the list of known Identifiers.
 * 
 * <p> {@link #loadTexture(Path, String)}: Transforms a texture file into a Minecraft-readable Identifier.
 * 
 * <p> {@link #render()}: Retextures the current helm.
 * 
 * <p> {@link #setKnownIdentifiers()}: Calls loadTexture() for every known helm.
 */
public class RTRender {
    private static Map<String, Identifier> knownIdentifiers = new HashMap<>();
    public static RenderLayer getModifiedRenderLayer(GameProfile profile) {
        try{
            //if texture contained in the list of textures and feature is enabled
            Retexturer rt = Retexturer.getInstance();
            if(rt.getFeatureEnabled()&&profile!=null&& !rt.getKnownHelms().isEmpty()){
                Map<String,Collection<Property>> profileProperties = profile.properties().asMap();
                Object[] textureProperties = profileProperties.get("textures").toArray();
                String textureURL = rt.getURL(((Property)textureProperties[0]).value());
                if(knownIdentifiers.containsKey(textureURL)){
                    return RenderLayer.getEntityTranslucent(knownIdentifiers.get(textureURL));
                }
                for(Entry<String,List<String>> entry : rt.getKnownHelms().entrySet()){
                        int index = entry.getValue().indexOf(textureURL);
                        if(index!=-1){
                            String name = entry.getKey() +index+".png";
                            Identifier ident = loadTexture(SkyblockImprovements.FILE_LOCATION.resolve("helms").resolve(name),name);
                            knownIdentifiers.put(textureURL,ident);
                            return RenderLayer.getEntityTranslucent(ident);
                        }
                    }
                }
        }catch(Exception ignored){}
        return null;
    }
    public static Map<String,Identifier> getKnownIdentifiers(){
        return knownIdentifiers;
    }
    public static Identifier loadTexture(Path path, String name){
        try{
            Supplier<String> nameSupplier = () -> name;
            FileInputStream input = new FileInputStream(path.toFile());
            NativeImage img = NativeImage.read(input);
            NativeImageBackedTexture texture = new NativeImageBackedTexture(nameSupplier,img);
            Identifier tId = Identifier.of("sbimp:"+name);
            MinecraftClient.getInstance().getTextureManager().registerTexture(tId, texture);
            return tId;
        }catch(Exception e){
            return null;
        }
    }
    public static void render(){
        Retexturer.getInstance().retextureHelm(InventoryProcessor.getHelmet());
    }
    public static void setKnownIdentifiers() {
        for(Entry<String,List<String>> entry : Retexturer.getInstance().getKnownHelms().entrySet()){
                for(int i=0; i<entry.getValue().size(); i++){
                String name = entry.getKey().toString()+i+".png";
                Identifier ident = loadTexture(SkyblockImprovements.FILE_LOCATION.resolve("helms").resolve(name),name);
                knownIdentifiers.put(entry.getKey(),ident);
            }
        }
    }
}
