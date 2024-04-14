package com.github.thepurityofchaos.features.packswapper;

import java.util.ArrayList;
import java.util.Collection;

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
    

    public static void init(){
        //default location
        PSVisual = new GUIElement(64,96,128,32,null);
    }
    @SuppressWarnings("resource")
    public static void manipulatePacks(String sArea, String sRegion){
        if(sArea.equals("NoAreaFound!")||sRegion.equals("§cNotonSkyblock!"))
            return;

        ResourcePackManager manager = MinecraftClient.getInstance().getResourcePackManager();
        Collection<ResourcePackProfile> packs = manager.getProfiles();
        Collection<ResourcePackProfile> currentlyEnabledPacks = manager.getEnabledProfiles();
        Collection<String> packsToActivate = new ArrayList<>();
        Collection<String> packsToRemove = new ArrayList<>();
        Collection<String> modifiedPacks = new ArrayList<>();
        for(ResourcePackProfile pack:packs){
            //ignore all packs not directly relevant
            if(!pack.getName().startsWith("file/_")){
                if(currentlyEnabledPacks.contains(pack))
                    packsToActivate.add(pack.getName());
                continue;
            }

            String[] splitPackName = pack.getName().split("-");
            if(splitPackName.length==2){
                if(splitPackName[1].contains(sRegion)){
                    if(splitPackName[0].contains(sArea)){
                        modifiedPacks.add(pack.getName());
                        continue;
                    }else{
                        packsToRemove.add(pack.getName());
                    }
                }else{
                    packsToRemove.add(pack.getName());
                }
            }else if(splitPackName[0].contains(sArea)){
                modifiedPacks.add(pack.getName());
                continue;
            }
            else{
                packsToRemove.add(pack.getName());
            }
        }
        //this is done to prioritize the packs in the hierarchy. The last ones added have the highest priority.
        Collection<String> currentPacks = manager.getEnabledNames();
        packsToActivate.addAll(modifiedPacks);

        boolean hasChanged = false;
        for(String pack :packsToActivate){
            if(!currentPacks.contains(pack)){
                manager.setEnabledProfiles(packsToActivate);
                //Reload Resources.
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
        String sArea = currentArea.getString().replace("Area:","").replace(" ","");
        String sRegion = currentRegion.getString().replace("ф","").replace("⏣","").replace(" ","");

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

    //feature toggles & getters

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
}
