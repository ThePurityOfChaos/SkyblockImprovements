package com.github.thepurityofchaos.features.packswapper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.thepurityofchaos.interfaces.Feature;
import com.github.thepurityofchaos.utils.Utils;
import com.github.thepurityofchaos.utils.gui.GUIElement;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;



public class PackSwapper implements Feature {
    //Used to show current Region and number of Packs associated with it. if desired.
    private static GUIElement PSVisual;
    private static char regionColor = 'e';
    private static boolean packHelper = true;
    private static boolean renderComponent = true;
    private static boolean sendDebugInfo = true;
    private static boolean undefinedRegions = true;
    private static String previousArea;
    private static String previousRegion;
    private static Map<String,Map<String,Map<String,Boolean>>> packAreaRegionToggles = null;
    
    //defines ALL default regions
    private static Map<String,List<String>> allDefaultRegions = new HashMap<>();

    public static void init(){PSVisual = new GUIElement(64,96,128,32,null);}
    @SuppressWarnings("resource")
    public static void manipulatePacks(String eArea, String eRegion){
        String sArea = Utils.clearArea(eArea);
        String sRegion = Utils.clearRegion(eRegion);
        if(sArea.equals("NoAreaFound!")||sRegion.equals("§cNotonSkyblock!"))
            return;
        //DEBUG_ADDREGION(sArea, sRegion);
        ResourcePackManager manager = MinecraftClient.getInstance().getResourcePackManager();
        Collection<ResourcePackProfile> packs = manager.getProfiles();
        Collection<ResourcePackProfile> currentlyEnabledPacks = manager.getEnabledProfiles();
        List<String> packsToRemove = new ArrayList<>();
        List<String> guaranteedPacks = new ArrayList<>();
        List<String> unmodifiedPacks = new ArrayList<>();
        List<String> modifiedPacks = new ArrayList<>();
        for(ResourcePackProfile pack:packs){
            //ignore all packs not directly relevant: guaranteed packs, and unmodified ones.
            String name = pack.getName();
            if(!name.startsWith("file/_")){
                if(pack.isAlwaysEnabled()){
                    guaranteedPacks.add(name);
                }
                if(currentlyEnabledPacks.contains(pack))
                    unmodifiedPacks.add(name);
                continue;
            }
            //if the pack is new, add it to the main pack map.
            if(!packAreaRegionToggles.containsKey(name)){
                //only load the default packs if a new pack is needed
                if(undefinedRegions){
                    defineDefaultRegions();
                }
                packAreaRegionToggles.put(name,loadDefaultAreas());
            }
            //add modified packs
            Map<String,Map<String,Boolean>> areaRegionToggles = packAreaRegionToggles.get(name);

            if(areaRegionToggles.containsKey(sArea)){
                //full area check
                if(areaRegionToggles.get(sArea).get("").booleanValue()){
                    modifiedPacks.add(name);
                    continue;
                }
                //if not, go to specific regions
                if(areaRegionToggles.get(sArea).containsKey(sRegion)){
                    if(areaRegionToggles.get(sArea).get(sRegion).booleanValue()){
                        modifiedPacks.add(name);
                    }else{
                        packsToRemove.add(name);
                    }
                }
            }
        }

        //this is done to prioritize the packs in the hierarchy. The last ones added have the highest priority.
        Collection<String> packsToActivate = new ArrayList<>();
        packsToActivate.addAll(guaranteedPacks);
        packsToActivate.addAll(unmodifiedPacks);
        packsToActivate.addAll(modifiedPacks);
        
        Collection<String> currentPacks = manager.getEnabledNames();
        boolean hasChanged = false;
        for(String pack :packsToActivate){
            if(!currentPacks.contains(pack)){
                manager.setEnabledProfiles(packsToActivate);
                hasChanged = true;
                break;
            }
        }
        for(String pack : packsToRemove){
            if(currentPacks.contains(pack)){
                manager.disable(pack);
                hasChanged = true;
            }
        }
        //only make changes if the packs change. 
        //These should be called RARELY.
        if(hasChanged){
            if(sendDebugInfo)
                MinecraftClient.getInstance().player.sendMessage(Text.of("§"+PackSwapper.getRegionColor()+"[§7SkyblockImprovements§"+PackSwapper.getRegionColor()+"]"+" §7Region change detected."),false);
            MinecraftClient.getInstance().reloadResources();
        }
        
    }

    //a shell test to ensure that manipulatePacks() is only ever called when the region changes
    public static void testForValidManipulation(Text currentArea,Text currentRegion){
        String sArea = Utils.clearArea(currentArea.getString());
        String sRegion = Utils.clearRegion(currentRegion.getString());

        //only manipulate packs if area changes and not in no area
        if(!sArea.equals(previousArea)||!sRegion.equals(previousRegion)||!sArea.equals("NoAreaFound!")){
            manipulatePacks(sArea,sRegion);
        }
        previousArea = sArea;
        previousRegion = sRegion;
    }

    /*
     * feature toggles & getters
     */
    public static GUIElement getFeatureVisual(){
        return PSVisual;
    }
    public static char getRegionColor(){
        return regionColor;
    }
    public static void setRegionColor(char c){
        regionColor = c;
    }
    public static void togglePackHelper(){
        packHelper = !packHelper;
    }
    public static boolean showPackHelper(){
        return packHelper;
    }
    public static void toggleRenderComponent(){
        renderComponent = !renderComponent;
    }
    public static void toggleDebugInfo(){
        sendDebugInfo = !sendDebugInfo;
    }
    public static boolean sendDebugInfo(){
        return sendDebugInfo;
    }
    public static boolean isRendering(){
        return renderComponent;
    }

    /*
     * Methods relating to the Pack Swapper's Config Map
     */
    
    public static void toggleRegion(String pack, String area, String region){
        Map<String,Boolean> areaMap = packAreaRegionToggles.get(pack).get(area);
        areaMap.put(region,(Boolean)!areaMap.get(region).booleanValue());
    }
    public static void loadPackAreaRegionToggles(Map<String,Map<String,Map<String,Boolean>>> map){
        packAreaRegionToggles = map;

    }
    private static Map<String,Map<String,Boolean>> loadDefaultAreas(){
        Map<String,Map<String,Boolean>> areasForThisPack = new HashMap<>();
        allDefaultRegions.forEach((k,v) -> {
            areasForThisPack.put(k,loadDefaultRegions(k));
        });
        return areasForThisPack;

    }
    private static Map<String,Boolean> loadDefaultRegions(String area){
        Map<String,Boolean> regions = new HashMap<>();
        if(allDefaultRegions.containsKey(area))
            for(String region : allDefaultRegions.get(area)){
                regions.put(region,false);
            }
        return regions;
    }
    public static Map<String,Map<String,Map<String,Boolean>>> getFullRegionMap(){
        return packAreaRegionToggles;
    }
    public static void setDefaultRegions(Map<String,List<String>> map){
        allDefaultRegions = map;
    }
    /*
    public static void DEBUG_ADDREGION(String area, String region){
        if(!area.equals("§cNoAreaFound!")){
            if(!allDefaultRegions.containsKey(area)){
                allDefaultRegions.put(area,new ArrayList<>());
            }
            if(!(allDefaultRegions.get(area).contains(region))){
                allDefaultRegions.get(area).add(region);
            }
        }
    }
    public static Map<String,List<String>> DEBUG_GETALLREGIONS(){
        return allDefaultRegions;
    }*/
    public static void defineDefaultRegions(){
        //load default regions
        try{
            Type smallMap = new TypeToken<Map<String,List<String>>>(){}.getType();
            Identifier allDefaultRegions = new Identifier("sbimp","info/defaultar.json");
            ResourceManager source = MinecraftClient.getInstance().getResourceManager();
            if(source!=null){
            Gson gson = new Gson();
            InputStream stream = source.getResource(allDefaultRegions).get().getInputStream();
            BufferedReader r = new BufferedReader(new InputStreamReader(stream));
            JsonObject defaultParser = JsonParser.parseReader(r).getAsJsonObject();
            setDefaultRegions(gson.fromJson(defaultParser, smallMap));
            r.close();
            undefinedRegions=false;
            }
        }catch(Exception x){
            x.printStackTrace();
        }
    }
}
