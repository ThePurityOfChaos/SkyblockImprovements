package com.github.thepurityofchaos.features.retexturer;


import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
import com.github.thepurityofchaos.utils.Utils;
import com.github.thepurityofchaos.utils.math.ColorUtils;
import com.github.thepurityofchaos.utils.processors.NbtProcessor;
import com.github.thepurityofchaos.utils.kmeans.Centroid;
import com.github.thepurityofchaos.utils.kmeans.EuclideanDistance;
import com.github.thepurityofchaos.utils.kmeans.KMeans;
import com.github.thepurityofchaos.utils.kmeans.Record;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;


public class HelmetRetexturer {
        private static boolean recolor = true;
        private String currentTextureURL = null;
        private BufferedImage currentTexture = null;
        private Map<String,List<String>> knownHelms = new HashMap<>();
        private int newColor = -16765017;
        public static HelmetRetexturer instance = new HelmetRetexturer();

        public void retextureHelm(ItemStack helmet){
            if(!recolor){
                return;
            }
            if(MinecraftClient.getInstance()==null) return;
            String helmetTextureURL = NbtProcessor.getTextureFromSkull(helmet);
            String helmetName = Utils.asciify(NbtProcessor.getNamefromItemStack(helmet).getString());
            if(helmetTextureURL == null) return;
            currentTextureURL = getURL(helmetTextureURL);
            
            if(knownHelms.containsKey(helmetName)){
                if(knownHelms.get(helmetName).contains(currentTextureURL)){
                    //draw using stored texture
                    getCurrentTexture(helmetName,knownHelms.get(helmetName).indexOf(currentTextureURL));
                    //???
                    return;
                }
            }else{
                knownHelms.put(helmetName,new ArrayList<String>());
            }
            knownHelms.get(helmetName).add(currentTextureURL);
            //use indexes to minimize issues with animated helmets
            int index = knownHelms.get(helmetName).indexOf(currentTextureURL);


            //retexture here
            try{
            //get texture from URL (Distance Dependent, high lag)
            try{
            downloadTexture(helmetName,index);
            }catch(Exception e){ //if download fails, remove the texture to try again
                knownHelms.get(helmetName).remove(currentTextureURL);
                return;
            }
            getCurrentTexture(helmetName,index);

            //modify texture
            crop();
            int[] colors = ColorUtils.intToRGBA(newColor);
            List<Double> newRGB = new ArrayList<>();
            //clamp color to prevent strong colors from overpowering. O(n)
            for(int i=0; i<colors.length-1; i++){
                colors[i] = ColorUtils.clamp(colors[i],10,253);
                newRGB.add(colors[i]+0.0);
            }
            colors[3] = 255;

            int[][] startingImage = getImage();
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
            //k-means clustering, find top 5 colors. O(mnk), which would normally be problematic but we're only doing it once.
            Map<Centroid, List<Record>> centers = KMeans.fit(records, 5, new EuclideanDistance(), 10);
            
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
                    for(Centroid center : centers.keySet()){
                        double cscale = ncoeff.get(index);
                        if(ColorUtils.norm(pixelList,center.getRGBList()) < criterion){         
                            double rratio = currentPixel[0]/center.getCoordinates().get("r");
                            double gratio = currentPixel[1]/center.getCoordinates().get("g");
                            double bratio = currentPixel[2]/center.getCoordinates().get("b");
                            int newred = (int)Math.min(255, colors[0]* cscale * rratio);
                            int newgreen = (int)Math.min(255, colors[1]* cscale * gratio);
                            int newblue = (int)Math.min(255, colors[2]* cscale * bratio);
                            result[x][y] = ColorUtils.rGBAToInt(newred, newgreen, newblue, currentPixel[3]);
                        }
                    }
                }
            }
            

            //store texture
            setImage(result);
            storeCurrentTexture(helmetName,index);
            }catch(Exception e){
            }

            //draw using stored texture
            //???
            return;
        }
        //URL helpers
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
        private void downloadTexture(String helmetName, int index) throws IOException{
            URL url = new URL(currentTextureURL);
            InputStream input = url.openConnection().getInputStream();
            FileOutputStream output = new FileOutputStream(SkyblockImprovements.FILE_LOCATION.resolve("helms").resolve(helmetName+index+".png").toString());
            ReadableByteChannel channel = Channels.newChannel(input);
            output.getChannel().transferFrom(channel, 0, Integer.MAX_VALUE);
            output.close();
            input.close();
        }
        //Texture Storage
        private void getCurrentTexture(String helmetName, int index){
            try{
                currentTexture = ImageIO.read(SkyblockImprovements.FILE_LOCATION.resolve("helms").resolve(helmetName+index+".png").toFile());
            }catch(Exception e){ //will be null sometimes
                currentTexture= null;
            }
        }
        private void storeCurrentTexture(String helmetName, int index){
            try{
                ImageIO.write(currentTexture, "PNG", SkyblockImprovements.FILE_LOCATION.resolve("helms").resolve(helmetName+index+".png").toFile());
            }catch(Exception e){
                e.printStackTrace();
                return;
            }
        }
        //Texture Modifiers
        private void crop(){
            currentTexture = currentTexture.getSubimage(0, 0, 64, 16);
        }
        private int[][] getImage(){
            int[][] image = new int[currentTexture.getWidth()][currentTexture.getHeight()];
            for(int x=0; x<currentTexture.getWidth(); x++){
                for(int y=0; y<currentTexture.getHeight(); y++){
                    image[x][y]=currentTexture.getRGB(x, y);
                }
            }
            return image;
        }
        private void setImage(int[][] image){
            for(int x=0; x<currentTexture.getWidth(); x++){
                for(int y=0; y<currentTexture.getHeight(); y++){
                    currentTexture.setRGB(x,y,image[x][y]);
                }
            }
        }

        //instance modifiers
        public void changeColor(int rgba){
            newColor = rgba;
            knownHelms = new HashMap<>();
        }
        //getter for instance
        public static HelmetRetexturer getDefaultInstance(){
            return instance;
        }
}
