package com.github.thepurityofchaos.features.packswapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.thepurityofchaos.interfaces.Feature;
import com.github.thepurityofchaos.utils.Utils;
import com.github.thepurityofchaos.utils.gui.GUIElement;

import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.text.Text;



public class PackSwapper implements Feature {
    //Used to show current Region and number of Packs associated with it. if desired.
    private static GUIElement PSVisual;
    private static char regionColor = 'e';
    private static boolean packHelper = true;
    private static boolean renderComponent = true;
    private static boolean sendDebugInfo = true;
    private static boolean experimental_useShortRegion = false;
    private static boolean experimental_useShortArea = false;
    private static String previousArea;
    private static String previousRegion;
    private static Map<String,Map<String,Map<String,Boolean>>> packAreaRegionToggles = null;
    
    //defines ALL default regions
    private static Map<String,List<String>> allDefaultRegions = new HashMap<>();
    static{
        //allRegions.put("areaName",Arrays.asList("",""});
        allDefaultRegions.put("DwarvenMines",Arrays.asList("","DwarvenMines","Rampart'sQuarry","UpperMines","DwarvenVillage","PalaceBridge","RoyalPalace","RoyalQuarters","HangingCourt"));
        allDefaultRegions.put("CrystalHollows",Arrays.asList(""));
        allDefaultRegions.put("Rift",Arrays.asList(""));
        allDefaultRegions.put("Hub",Arrays.asList("","Village","CommunityCenter","Colosseum","Wilderness","Fisherman'sHut","Unincorporated","Mountain"));
        allDefaultRegions.put("PrivateIsland",Arrays.asList("","YourIsland"));
        allDefaultRegions.put("Garden",Arrays.asList(""));
        allDefaultRegions.put("DungeonHub",Arrays.asList(""));
        allDefaultRegions.put("TheFarmingIslands",Arrays.asList(""));
        allDefaultRegions.put("ThePark",Arrays.asList(""));
        allDefaultRegions.put("GoldMine",Arrays.asList(""));
        allDefaultRegions.put("DeepCaverns",Arrays.asList(""));
        allDefaultRegions.put("CrystalHollows",Arrays.asList(""));
        allDefaultRegions.put("Spider'sDen",Arrays.asList(""));
        allDefaultRegions.put("TheEnd",Arrays.asList(""));
        allDefaultRegions.put("CrimsonIsle",Arrays.asList(""));
    }

    public static void init(){
        //default location
        PSVisual = new GUIElement(64,96,128,32,null);
    }
    @SuppressWarnings("resource")
    public static void manipulatePacks(String eArea, String eRegion){
        String sArea = Utils.clearArea(eArea);
        String sRegion = Utils.clearRegion(eRegion);
        if(sArea.equals("NoAreaFound!")||sRegion.equals("§cNotonSkyblock!"))
            return;

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

            //OLD
            /*
            String[] splitPackName = name.split("-");
            if(splitPackName.length==2){
                if(splitPackName[1].contains(sRegion)){
                    if(splitPackName[0].contains(sArea)){
                        modifiedPacks.add(name);
                        continue;
                    }else{
                        packsToRemove.add(name);
                    }
                }else{
                    packsToRemove.add(name);
                }
            }else if(splitPackName[0].contains(sArea)){
                modifiedPacks.add(name);
                continue;
            }
            else{
                packsToRemove.add(name);
            }*/
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
            //use experimental settings?
            String sAreaMod = sArea;
            String sRegionMod = sRegion;
            if(experimental_useShortArea)
                sAreaMod = Utils.removeLowerCase(sAreaMod);
            if(experimental_useShortRegion)
                sRegionMod = Utils.removeLowerCase(sRegionMod);
            manipulatePacks(sAreaMod,sRegionMod);
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
    public static void toggleExperimentalArea(){
        experimental_useShortArea = !experimental_useShortArea;
    }
    public static boolean experimental_useShortArea(){
        return experimental_useShortArea;
    }
    public static void toggleExperimentalRegion(){
        experimental_useShortRegion = !experimental_useShortRegion;
    }
    public static boolean experimental_useShortRegion(){
        return experimental_useShortRegion;
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
    public static void DEBUG_ADDREGION(String area, String region){
        if(!allDefaultRegions.containsKey(area))
            allDefaultRegions.put(area,new ArrayList<>());
        if(!allDefaultRegions.get(area).contains(region))
            allDefaultRegions.get(area).add(region);
    }
}
