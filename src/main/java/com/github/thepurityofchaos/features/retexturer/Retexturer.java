package com.github.thepurityofchaos.features.retexturer;


import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import com.github.thepurityofchaos.SkyblockImprovements;
import com.github.thepurityofchaos.storage.config.RTConfig;
import com.github.thepurityofchaos.utils.NbtUtils;
import com.github.thepurityofchaos.utils.Utils;
import com.github.thepurityofchaos.utils.gui.GUIElement;
import com.github.thepurityofchaos.utils.math.ColorUtils;
import com.github.thepurityofchaos.utils.processors.InventoryProcessor;
import com.github.thepurityofchaos.utils.kmeans.Centroid;
import com.github.thepurityofchaos.utils.kmeans.EuclideanDistance;
import com.github.thepurityofchaos.utils.kmeans.KMeans;
import com.github.thepurityofchaos.utils.kmeans.Record;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

/**
 *  The Retexturer for the system. Currently can retexture: Helmets (Skull).
 *  <p> {@link #retextureHelm(ItemStack)}: Retextures a Helmet.
 * 
 *  <p> {@link #getURL(String)}: Returns the URL associated with a base64 string inside of a Helmet's NBT data.
 * 
 *  <p> {@link #changeColor(int)}: Changes the color that the Retexturer will change the item to.
 * 
 *  <p> {@link #changeK(int)}: Changes the amount of "expected colors" in the texture. 2-16 suggested.
 * 
 *  <p> {@link #refresh(ItemStack)}: Removes a Helmet from the list of known helms, essentially "forgetting" that it exists.
 * 
 *  <p> {@link #toggleRecolor()}: toggles whether the feature is enabled or not.
 * 
 *  <p> {@link #getFeatureEnabled()}: Returns whether the feature is enabled or not.
 * 
 *  <p> {@link #getKnownHelms()}: Returns the list of known helms and their alias textures.
 * 
 *  <p> {@link #getColorCode()}: Returns the RGBA color associated with the system.
 * 
 *  <p> {@link #getK()}: Returns the current K value.
 * 
 *  <p> {@link #setKnownHelms(Map)}: Loads the Map into knownHelms.
 * 
 *  <p> {@link #interact(Screen)}: Loads the buttons for the system.
 */
public class Retexturer {
        //INCLUDED IN: RTConfig -> enabled
        private static boolean isEnabled = false;
        private static Map<String,List<String>> knownHelms = new HashMap<>();
        private static int newColor = -16765017;
        private static int k = 15;

        public static void retextureHelm(ItemStack helmet){
            if(!isEnabled){
                return;
            }
            if(MinecraftClient.getInstance()==null) return;
            String helmetTextureURL = NbtUtils.getTextureFromSkull(helmet);
            UUID hID = NbtUtils.getUUIDFromSkull(helmet);
            if(helmetTextureURL == null|| hID ==null) return;
            String helmetID = hID.toString();
            String currentTextureURL = getURL(helmetTextureURL);
            //prevents the possibility of a malicious URL from getting in, even though it's saved as a PNG
            if(!currentTextureURL.contains("://textures.minecraft.net/")) return;

            if(knownHelms.containsKey(helmetID)){
                if(knownHelms.get(helmetID).contains(currentTextureURL)){
                    return;
                }
            }else{
                knownHelms.put(helmetID,new ArrayList<String>());
            }
            //retexture here
            SkyblockImprovements.push("SBI_Retexturer_Helmet");
            try{
                knownHelms.get(helmetID).add(currentTextureURL);
                //use indexes to minimize issues with animated helmets
                int index = knownHelms.get(helmetID).indexOf(currentTextureURL);
                //get texture from URL (high lag)
                BufferedImage currentTexture = crop(downloadTexture(helmetID, currentTextureURL, index));
                storeCurrentTexture(helmetID, uncrop(retexture(helmetID,currentTexture, 0)), index);
            }catch(Exception e){
                e.printStackTrace();
                knownHelms.get(helmetID).remove(currentTextureURL);
                RTRender.getKnownIdentifiers().remove(currentTextureURL);
                return;
            }
            
            SkyblockImprovements.pop();
        }

        //URL helper
        @SuppressWarnings("unchecked")
        public static String getURL(String helmetTextureURL){
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

        //Download Texture from MC's Servers
        private static BufferedImage downloadTexture(String helmetID, String currentTextureURL, int index) throws IOException{
            URL url = new URL(currentTextureURL);
            InputStream input = url.openConnection().getInputStream();
            FileOutputStream output = new FileOutputStream(SkyblockImprovements.FILE_LOCATION.resolve("helms").resolve(helmetID+index+".png").toString());
            ReadableByteChannel channel = Channels.newChannel(input);
            output.getChannel().transferFrom(channel, 0, Integer.MAX_VALUE);
            output.close();
            input.close();
            try{
                return ImageIO.read(SkyblockImprovements.FILE_LOCATION.resolve("helms").resolve(helmetID+index+".png").toFile());
            }catch(Exception e){ //will be null sometimes
                return null;
            }
        }

        //Store to File
        private static void storeCurrentTexture(String helmetID, BufferedImage currentTexture, int index){
            try{
                ImageIO.write(currentTexture, "PNG", SkyblockImprovements.FILE_LOCATION.resolve("helms").resolve(helmetID+index+".png").toFile());
            }catch(Exception e){
                e.printStackTrace();
                return;
            }
        }
        //Texture Modifiers
        private static BufferedImage crop(BufferedImage currentTexture){
            return currentTexture.getSubimage(0, 0, 64, 16);
        }
        private static BufferedImage uncrop(BufferedImage currentTexture){
            BufferedImage result = new BufferedImage(64, 64, currentTexture.getType());
            result.setRGB(0,0,64,16,currentTexture.getRGB(0, 0, 64, 16, null,0,64),0,64);
            return result;
        }
        private static int[][] getImage(BufferedImage currentTexture){
            int[][] image = new int[currentTexture.getWidth()][currentTexture.getHeight()];
            for(int x=0; x<currentTexture.getWidth(); x++){
                for(int y=0; y<currentTexture.getHeight(); y++){
                    image[x][y]=currentTexture.getRGB(x, y);
                }
            }
            return image;
        }
        private static BufferedImage setImage(int[][] image, BufferedImage currentTexture){
            for(int x=0; x<currentTexture.getWidth(); x++){
                for(int y=0; y<currentTexture.getHeight(); y++){
                    currentTexture.setRGB(x,y,image[x][y]);
                }
            }
            return currentTexture;
        }
        private static BufferedImage retexture(String helmetID, BufferedImage currentTexture, int attemptNumber){
                //retexture here 
                int[][] startingImage = getImage(currentTexture);
                int[] colors = ColorUtils.intToRGBA(newColor);
                List<Double> newRGB = new ArrayList<>();
                //clamp color to prevent strong colors from overpowering. O(1), since colors is constant size.
                for(int i=0; i<colors.length-1; i++){
                    colors[i] = ColorUtils.clamp(colors[i],10,253);
                    newRGB.add(colors[i]+0.0);
                }
                colors[3] = 255;
    
                
                int[][] result = new int[startingImage.length][startingImage[0].length];
    
                //get alpha channels of array for masking. O(n^2).
                boolean alphaMask;
                List<Record> records = new ArrayList<>();
                for(int x=0; x<startingImage.length; x++){
                    for(int y=0; y<startingImage[0].length; y++){
                        alphaMask = ColorUtils.getAlpha(startingImage[x][y])>0;
                        if(alphaMask)
                            records.add(new Record((new int[]{x,y}),startingImage[x][y]));
                    }
                }          
                //k-means clustering, find top 5 colors. O(mnk), which is problematic, but this algorithm will (hopefully) run rarely under normal use.
                Map<Centroid, List<Record>> centers = KMeans.fit(records, k, new EuclideanDistance(), 10,true);
                
                List<List<Map.Entry<Centroid,List<Record>>>> pairedCenters = (List<List<Map.Entry<Centroid,List<Record>>>>) Utils.allPairs(centers,false);
                List<Double[]> distances = new ArrayList<>();
                //Add all combinations. O(n).
                for(List<Map.Entry<Centroid,List<Record>>> pair : pairedCenters){
                    Map<String,Double> pair1 = pair.get(0).getKey().getCoordinates();
                    Map<String,Double> pair2 = pair.get(1).getKey().getCoordinates();
                    distances.add(new Double[]{
                        pair1.get("r")-pair2.get("r"),
                        pair1.get("g")-pair2.get("g"),
                        pair1.get("b")-pair2.get("b"),
                        pair1.get("a")-pair2.get("a")
                    });
                }
                Double criterion = 0.0; 
                for(Double[] n : distances)
                    criterion+=n[0]+n[1]+n[2]+n[3];
                criterion/=(Math.sqrt(distances.size()*4)*distances.size()*4);
    
                List<Integer> clusterSizes = new ArrayList<>();
                List<Double> satDistances = new ArrayList<>();
                List<Centroid> centroids = new ArrayList<>();
                for(Centroid centroid : centers.keySet()){
                    int clusterSize = 0;
                    for(Record record : records){
                        if(ColorUtils.norm(centroid.getRGBList(),record.getRGBList())<criterion){
                            clusterSize++;
                        }
                    }
                    Double satDistance = Math.abs(ColorUtils.norm(centroid.getRGBList())-ColorUtils.norm(newRGB));
                    clusterSizes.add(clusterSize);
                    satDistances.add(satDistance);
                    centroids.add(centroid);
                }
                int targetIndex = 0;
                Double maxMetric = Double.MIN_VALUE;
                for(int i=0; i<clusterSizes.size(); i++){
                    if(Math.pow(clusterSizes.get(i),2)/satDistances.get(i)>maxMetric){
                        maxMetric = Math.pow(clusterSizes.get(i),2)/satDistances.get(i);
                        targetIndex = i;
                    }
                }
                Centroid targetCentroid = centroids.get(targetIndex);
                List<Double> hypersaturatedTarget = targetCentroid.getRGBList();
                for(int i=0; i<hypersaturatedTarget.size(); i++){
                    hypersaturatedTarget.set(i,hypersaturatedTarget.get(i)/ColorUtils.norm(targetCentroid.getRGBList()));
                }
                List<Integer> removalIndexes = new ArrayList<>();
                for(int i=0; i< centroids.size(); i++){
                    List<Double> sat_center = new ArrayList<>();
                    for(Double d : centroids.get(i).getRGBList()){
                        sat_center.add(d/ColorUtils.norm(centroids.get(i).getRGBList()));
                    }
                    double hue_distance = ColorUtils.norm(sat_center,hypersaturatedTarget);
                    if(hue_distance>=0.3){
                        removalIndexes.add(i);
                    }
                }
                List<Centroid> toRemove = new ArrayList<>();
                for(int i=0; i<centroids.size(); i++){
                    if(removalIndexes.contains(i)) toRemove.add(centroids.get(i));
                }
                for(Centroid centroid : toRemove){
                    centers.remove(centroid);
                }
                List<Double> ncoeff = new ArrayList<>();
                for(Centroid centroid : centers.keySet()){
                    ncoeff.add(ColorUtils.norm(centroid.getRGBList())/ColorUtils.norm(targetCentroid.getRGBList()));
                }
                for(int x=0; x<startingImage.length; x++){
                    for(int y=0; y<startingImage[0].length; y++){
                        int[] currentPixel = ColorUtils.intToRGBA(startingImage[x][y]);
                        
                        List<Double> pixelList = new ArrayList<>();
                        for( int n : currentPixel) pixelList.add(n+0.0);
                        //replace the new image pixel with the current image pixel
                        result[x][y] = ColorUtils.rGBAToInt(currentPixel[0],currentPixel[1],currentPixel[2],currentPixel[3]);
                        if(currentPixel[3]==0) continue;
                        Centroid[] keySet = centers.keySet().toArray(new Centroid[centers.keySet().size()]);
                        for(int i=0; i<keySet.length; i++){
                            double cscale = ncoeff.get(i);
                            if(ColorUtils.norm(pixelList,keySet[i].getRGBList()) < criterion){         
                                double rratio = currentPixel[0]/keySet[i].getCoordinates().get("r");
                                double gratio = currentPixel[1]/keySet[i].getCoordinates().get("g");
                                double bratio = currentPixel[2]/keySet[i].getCoordinates().get("b");
                                int newred = (int)Math.min(255, colors[0]* cscale * rratio);
                                int newgreen = (int)Math.min(255, colors[1]* cscale * gratio);
                                int newblue = (int)Math.min(255, colors[2]* cscale * bratio);
                                result[x][y] = ColorUtils.rGBAToInt(newred, newgreen, newblue, currentPixel[3]);
                            }
                        }
                    }
                }
                //store texture, try again up to 3x if nothing was changed
                if(result.equals(startingImage) && attemptNumber < 3){
                    return retexture(helmetID, currentTexture, attemptNumber++);
                }
                return setImage(result, currentTexture);
                
            
        }

        //instance modifiers & getters
        public static void changeColor(int rgba){
            newColor = rgba;
            RTConfig.saveSettings();
        }
        public static void changeK(int newK){
            k = newK;
            RTConfig.saveSettings();
        }
        public static void refresh(ItemStack helmet){
            try{
                String helmetID = NbtUtils.getUUIDFromSkull(helmet).toString();
                String textureURL = NbtUtils.getTextureFromSkull(helmet);
                knownHelms.remove(helmetID);
                RTRender.getKnownIdentifiers().remove(getURL(textureURL));
                RTConfig.saveSettings();
            }catch(Exception e){}
            
        }
        public static void toggleRecolor(){
            isEnabled = !isEnabled;
            RTConfig.saveSettings();
        }
        public static boolean getFeatureEnabled(){
            return isEnabled;
        }
        public static Map<String,List<String>> getKnownHelms(){
            return knownHelms;
        }
        public static int getColorCode() {
            return newColor;
        }
        public static int getK() {
            return k;
        }
        public static void setKnownHelms(Map<String,List<String>> map){
            knownHelms = map;
        }
        public static void interact(Screen screen){
                int x = screen.width/3+screen.width/12-screen.width/48;
                int y = screen.height/3+screen.height/48;
                GUIElement helmRefreshButton = new GUIElement(x, y, 16, 16, button ->{
                    Retexturer.refresh(InventoryProcessor.getHelmet());
                    return;
                });
                GUIElement helmResetButton = new GUIElement(x-16, y, 16, 16, button ->{
                    try{
                    ItemStack helmet = InventoryProcessor.getHelmet();
                    String helmetTextureURL = NbtUtils.getTextureFromSkull(helmet);
                    UUID hID = NbtUtils.getUUIDFromSkull(helmet);
                    if(helmetTextureURL == null|| hID ==null) return;
                    String helmetID = hID.toString();
                    String currentTextureURL = getURL(helmetTextureURL);
                    knownHelms.remove(helmetID);
                    RTRender.getKnownIdentifiers().remove(getURL(helmetTextureURL));
                    Retexturer.storeCurrentTexture(helmetID,uncrop(crop(downloadTexture(helmetID, currentTextureURL, 0))),0);
                    knownHelms.put(helmetID,new ArrayList<String>());
                    knownHelms.get(helmetID).add(currentTextureURL);
                    RTConfig.saveSettings();
                    }catch(Exception e){}
                });
                helmRefreshButton.setMessage(Text.of(Utils.getColorString('b')+"⟳"));
                helmResetButton.setMessage(Text.of(Utils.getColorString('b')+"X"));
                if(isEnabled){
                Screens.getButtons(screen).add(helmRefreshButton);
                Screens.getButtons(screen).add(helmResetButton);    
                }        
        }


        

}
