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

public class RTRender {
    private static Map<String, Identifier> knownIdentifiers = new HashMap<>();
    public static RenderLayer getModifiedRenderLayer(SkullBlock.SkullType type, @Nullable GameProfile profile){
        try{
            //if texture contained in the list of textures and festure is enabled
            if(Retexturer.getFeatureEnabled()&&profile!=null&&Retexturer.getKnownHelms().size()!=0){
                Map<String,Collection<Property>> profileProperties = profile.getProperties().asMap();
                Object[] textureProperties = profileProperties.get("textures").toArray();
                String textureURL = Retexturer.getURL(((Property)textureProperties[0]).value());
                if(knownIdentifiers.containsKey(textureURL)){
                    return RenderLayer.getEntityTranslucent(knownIdentifiers.get(textureURL));
                }
                for(Entry<String,List<String>> entry : Retexturer.getKnownHelms().entrySet()){
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
        Retexturer.retextureHelm(InventoryProcessor.getHelmet());
    }
    public static void setKnownIdentifiers() {
        for(Entry<String,List<String>> entry : Retexturer.getKnownHelms().entrySet()){
                for(int i=0; i<entry.getValue().size(); i++){
                String name = entry.getKey().toString()+i+".png";
                Identifier ident = loadTexture(SkyblockImprovements.FILE_LOCATION.resolve("helms").resolve(name),name);
                knownIdentifiers.put(entry.getKey(),ident);
            }
        }
    }
}
