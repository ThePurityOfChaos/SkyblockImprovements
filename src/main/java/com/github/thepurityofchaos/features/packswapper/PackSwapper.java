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

import com.github.thepurityofchaos.SkyblockImprovements;
import com.github.thepurityofchaos.interfaces.Feature;
import com.github.thepurityofchaos.storage.config.PSConfig;
import com.github.thepurityofchaos.utils.Utils;
import com.github.thepurityofchaos.utils.gui.GUIElement;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;


/**
 * Automated Resource Pack Swapper.
 * <p> {@link #init()}: Initialize the visual component.
 * 
 * <p> {@link #manipulatePacks(String, String)}: Modifies the active packs. Memoized.
 * 
 * <p> {@link #testForValidManipulation(Text, Text)}: Checks whether the input Area and Region are different from the previous.
 * 
 * <p> {@link #getFeatureVisual()}: Returns the visual component.
 * 
 * <p> {@link #getRegionColor()}: Returns the colorCode corresponding to the PackSwapper.
 * 
 * <p> {@link #setRegionColor(char)}: Setter for {@link #getRegionColor()}.
 * 
 * <p> {@link #togglePackHelper()}: Toggles whether to show additional text info.
 * 
 * <p> {@link #showPackHelper()}: Returns whether to show additional text info.
 * 
 * <p> {@link #toggleRenderComponent()}: Toggles whether to show the render component or not.
 * 
 * <p> {@link #toggleDebugInfo()}: Toggles whether or not to show that the Pack Swapper detected a region change in chat.
 * 
 * <p> {@link #sendDebugInfo()}: Returns whether or not to show that the Pack Swapper detected a region change.
 * 
 * <p> {@link #isRendering()}: Returns whether to render the visual component or not.
 * 
 * <p> {@link #toggleRegion(String, String, String)}: Toggles the state of a region given Pack|Area|Region.
 * 
 * <p> {@link #loadDefaultAreas(Map)}: Updates the Map to be consistent with the default areas.
 * 
 * <p> {@link #loadDefaultRegions(String, Map)}: Updates the Map to be consistent with the default regions.
 * 
 * <p> {@link #getFullRegionMap()}: Returns the entire Map of Packs|Areas|Regions. 
 * 
 * <p> {@link #setDefaultRegions(Map)}: Sets the default region structure to the input Map<String,List<String>>.
 * 
 * 
 * 
 * <p> {@link #needsUpdate()}: Notifies the system that it needs an update.
 */
public class PackSwapper implements Feature {
    //Used to show current Region and number of Packs associated with it. if desired.
    //INCLUDED IN: PSConfig -> buttons
    private static GUIElement PSVisual;
    //INCLUDED IN: PSConfig -> advanced
    private static char regionColor = 'e';
    private static boolean packHelper = true;
    private static boolean renderComponent = true;
    private static boolean sendDebugInfo = true;
    private static boolean undefinedRegions = true;
    private static boolean needsUpdate = false;
    //INCLUDED IN: None
    private static String previousArea;
    private static String previousRegion;
    //INCLUDED IN: PSConfig -> allRegions
    private static Map<String,Map<String,Map<String,Boolean>>> packAreaRegionToggles = null;
    
    
    //defines ALL default regions
    private static Map<String,List<String>> allDefaultRegions = new HashMap<>();

    public static void init(){PSVisual = new GUIElement(64,96,128,32,null);}
    
    public static void manipulatePacks(String eArea, String eRegion){
        String sArea = Utils.clearArea(eArea);
        String sRegion = Utils.clearRegion(eRegion);
        if(sArea.equals("NoAreaFound!")||sRegion.equals("§cNotonSkyblock!")){
            if(needsUpdate){
                defineDefaultRegions();
                Map<String,Map<String,Map<String,Boolean>>> newMap = new HashMap<>();
                getFullRegionMap().forEach((k,v)->{
                    newMap.put(k,loadDefaultAreas(v));
                });
                loadPackAreaRegionToggles(newMap);
                PSConfig.saveSettings(); 
                needsUpdate = false;
            }
            return;
        }

        //DEBUG_ADDREGION(sArea, sRegion);
        ResourcePackManager manager = MinecraftClient.getInstance().getResourcePackManager();
        Collection<ResourcePackProfile> packs = manager.getProfiles();
        Collection<ResourcePackProfile> currentlyEnabledPacks = manager.getEnabledProfiles();
        List<String> packsToRemove = new ArrayList<>();
        List<String> guaranteedPacks = new ArrayList<>();
        List<String> unmodifiedPacks = new ArrayList<>();
        List<String> modifiedPacks = new ArrayList<>();
        boolean isValidPack = false;
        for(ResourcePackProfile pack:packs){
            //ignore all packs not directly relevant: guaranteed packs, and unmodified ones.
            String name = pack.getName();
            ResourcePack metadataHelper = pack.createResourcePack();
            try{
                InputStreamReader stream = new InputStreamReader((InputStream)
                metadataHelper.openRoot(SkyblockImprovements.RESOURCE_PACK_LOCATION.resolve(name).resolve(ResourcePack.PACK_METADATA_NAME).toString()));
                    if(stream!=null){
                        JsonElement json = JsonParser.parseReader(stream);
                        if(json.isJsonObject()){
                            JsonObject jsonObject = json.getAsJsonObject();
                            if(jsonObject.has("sbimp")){
                                isValidPack = jsonObject.get("sbimp").getAsBoolean();
                            }else{
                                isValidPack = false;
                            }
                        }
                    }
            }catch(Exception e){
                isValidPack = false;
            }

            if(!isValidPack&&!name.startsWith("file/_")){
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
                packAreaRegionToggles.put(name,loadDefaultAreas(new HashMap<>()));
            }
            //add modified packs
            Map<String,Map<String,Boolean>> areaRegionToggles = packAreaRegionToggles.get(name);

            if(areaRegionToggles.containsKey(sArea)){
                //full area check
                if(areaRegionToggles.get(sArea).get("").booleanValue()){
                    modifiedPacks.add(name);
                    continue;
                }
                else if(areaRegionToggles.get(sArea).size()==1){
                    packsToRemove.add(name);
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
        for(String pack : packsToActivate){
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
            if(sendDebugInfo){
                MinecraftClient client = MinecraftClient.getInstance();
                client.player.sendMessage(Text.of("§"+PackSwapper.getRegionColor()+"[§7SkyblockImprovements§"+PackSwapper.getRegionColor()+"]"+" §7Region change detected."),false);
            }
            MinecraftClient.getInstance().reloadResources();
        }
        
    }

    //a shell test to ensure that manipulatePacks() is only ever called when the region changes
    public static void testForValidManipulation(Text currentArea,Text currentRegion){
        String sArea = Utils.clearArea(currentArea.getString());
        String sRegion = Utils.clearRegion(currentRegion.getString());

        //only manipulate packs if area changes and not in no area
        if(!sArea.equals("NoAreaFound!")&&(!sArea.equals(previousArea)||!sRegion.equals(previousRegion))){
            manipulatePacks(sArea,sRegion);
        }
        previousArea = sArea;
        previousRegion = sRegion;
    }

    /*
     * feature toggles & getters
     */
    public static GUIElement getFeatureVisual(){return PSVisual;}
    public static char getRegionColor(){return regionColor;}
    public static void setRegionColor(char c){regionColor = c;}
    public static void togglePackHelper(){packHelper = !packHelper;}
    public static boolean showPackHelper(){return packHelper;}
    public static void toggleRenderComponent(){renderComponent = !renderComponent;}
    public static void toggleDebugInfo(){sendDebugInfo = !sendDebugInfo;}
    public static boolean sendDebugInfo(){return sendDebugInfo;}
    public static boolean isRendering(){return renderComponent;}

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
    public static Map<String,Map<String,Boolean>> loadDefaultAreas(Map<String,Map<String,Boolean>> areasForThisPack){
        allDefaultRegions.forEach((k,v) -> {
            if(!areasForThisPack.containsKey(k))
                areasForThisPack.put(k,loadDefaultRegions(k,new HashMap<String,Boolean>()));
            else{
                areasForThisPack.put(k,loadDefaultRegions(k,areasForThisPack.get(k)));
            }
        });
        return areasForThisPack;

    }
    private static Map<String,Boolean> loadDefaultRegions(String area, Map<String,Boolean> regions){
        if(allDefaultRegions.containsKey(area))
            for(String region : allDefaultRegions.get(area)){
                if(!regions.containsKey(region))
                    regions.put(region,false);
            }
        return regions;
    }
    private static void removeMissing(){
        //remove any portions of the map that aren't actually in minecraft's pack list.
        ResourcePackManager manager = MinecraftClient.getInstance().getResourcePackManager();
        Collection<ResourcePackProfile> packs = manager.getProfiles();
        List<String> names = new ArrayList<>();
        packs.forEach(pack ->{
            names.add(pack.getName());
        });
        Map<String,Map<String,Map<String,Boolean>>> newMap = new HashMap<>();
        packAreaRegionToggles.forEach((pack,map) ->{
            if(names.contains(pack))
                newMap.put(pack,map);
        });
        packAreaRegionToggles = newMap;
    }

    public static Map<String,Map<String,Map<String,Boolean>>> getFullRegionMap(){
        //prevents issues when unloaded
        if(MinecraftClient.getInstance().getResourcePackManager().getProfiles().size()!=0)
            removeMissing();
        return packAreaRegionToggles;

    }
    
    public static void setDefaultRegions(Map<String,List<String>> map){
        allDefaultRegions = map;
    }
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
    public static void needsUpdate(){
        needsUpdate = true;
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
}
