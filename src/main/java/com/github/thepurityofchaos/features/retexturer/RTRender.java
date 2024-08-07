package com.github.thepurityofchaos.features.retexturer;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jetbrains.annotations.Nullable;

import com.github.thepurityofchaos.SkyblockImprovements;
import com.github.thepurityofchaos.utils.processors.InventoryProcessor;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import net.minecraft.block.SkullBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

/**
 * Rendering component for the Retexturer.
 * <p> {@link #getModifiedRenderLayer(net.minecraft.block.SkullBlock.SkullType, GameProfile)}: Returns the retextured RenderLayer, if applicable. Otherwise, returns what it would without this.
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
    public static RenderLayer getModifiedRenderLayer(SkullBlock.SkullType type, @Nullable GameProfile profile){
        try{
            //if texture contained in the list of textures and festure is enabled
            Retexturer rt = Retexturer.getInstance();
            if(rt.getFeatureEnabled()&&profile!=null&&rt.getKnownHelms().size()!=0){
                Map<String,Collection<Property>> profileProperties = profile.getProperties().asMap();
                Object[] textureProperties = profileProperties.get("textures").toArray();
                String textureURL = rt.getURL(((Property)textureProperties[0]).value());
                if(knownIdentifiers.containsKey(textureURL)){
                    return RenderLayer.getEntityTranslucent(knownIdentifiers.get(textureURL));
                }
                for(Entry<String,List<String>> entry : rt.getKnownHelms().entrySet()){
                        int index = entry.getValue().indexOf(textureURL);
                        if(index!=-1){
                            String name = entry.getKey().toString()+index+".png";
                            Identifier ident = loadTexture(SkyblockImprovements.FILE_LOCATION.resolve("helms").resolve(name),name);
                            knownIdentifiers.put(textureURL,ident);
                            return RenderLayer.getEntityTranslucent(ident);
                        }
                    }
                }
            return SkullBlockEntityRenderer.getRenderLayer(type, profile);
        }catch(Exception e){
            return SkullBlockEntityRenderer.getRenderLayer(type, profile);
        }
    }
    public static Map<String,Identifier> getKnownIdentifiers(){
        return knownIdentifiers;
    }
    public static Identifier loadTexture(Path path, String name){
        try{
            FileInputStream input = new FileInputStream(path.toFile());
            NativeImage img = NativeImage.read(input);
            NativeImageBackedTexture texture = new NativeImageBackedTexture(img);
            Identifier tId = MinecraftClient.getInstance().getTextureManager().registerDynamicTexture(name, texture);
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
