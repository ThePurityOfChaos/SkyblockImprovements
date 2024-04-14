package com.github.thepurityofchaos.features.packswapper;

import java.util.ArrayList;
import java.util.Collection;

import com.github.thepurityofchaos.interfaces.Feature;
import com.github.thepurityofchaos.utils.gui.GUIElement;

import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.text.Text;



public class PackSwapper implements Feature {
    //Used to show current Region and number of Packs associated with it. if desired.
    private static GUIElement PSVisual;
    private static char regionColor = 'e';
    private static boolean packHelper = true;;
    private static boolean renderComponent = true;
    private static boolean sendDebugInfo = true;
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
    
        for(ResourcePackProfile pack:packs){
            //ignore all packs not directly relevant
            if(!pack.getName().startsWith("file/SBIMP_")){
                if(currentlyEnabledPacks.contains(pack))
                    packsToActivate.add(pack.getName());
                continue;
            }

            String[] splitPackName = pack.getName().split("-");
            if(splitPackName.length==2){
                if(splitPackName[1].contains(sRegion)){
                    if(splitPackName[0].contains(sArea)){
                        packsToActivate.add(pack.getName());
                        continue;
                    }else{
                        packsToRemove.add(pack.getName());
                    }
                }else{
                    packsToRemove.add(pack.getName());
                }
            }else if(splitPackName[0].contains(sArea)){
                packsToActivate.add(pack.getName());
                continue;
            }
            else{
                packsToRemove.add(pack.getName());
            }
        }
        //only make changes if the packs change. 
        //This should be called RARELY.
        Collection<String> currentPacks = manager.getEnabledNames();
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
        if(hasChanged){
            if(sendDebugInfo)
                MinecraftClient.getInstance().player.sendMessage(Text.of("§"+PackSwapper.getRegionColor()+"[SkyblockImprovements] Region change detected. Reloading Resources."));
            MinecraftClient.getInstance().reloadResources();
        }
        
    }

    //a shell test to ensure that manipulatePacks() is only ever called when the region changes
    public static void testForValidManipulation(Text currentArea,Text currentRegion){
        String sArea = currentArea.getString().replace("Area:","").replace(" ","");
        String sRegion = currentRegion.getString().replace("ф","").replace("⏣","").replace(" ","");
        //only manipulate packs if area changes and not in no area
        if(!sArea.equals(previousArea)||!sRegion.equals(previousRegion)||!sArea.equals("NoAreaFound!")){
            manipulatePacks(sArea,sRegion);
        }
        previousArea = sArea;
        previousRegion = sRegion;
    }








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
    public static boolean isRendering(){
        return renderComponent;
    }
}
