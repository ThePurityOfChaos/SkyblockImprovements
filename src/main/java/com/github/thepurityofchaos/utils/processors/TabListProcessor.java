package com.github.thepurityofchaos.utils.processors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.thepurityofchaos.SkyblockImprovements;
import com.github.thepurityofchaos.mixin.TabListAccessor;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;

public class TabListProcessor {
    private static List<Text> tabList = null;
    private static Set<PlayerListEntry> previousPlayers = new ReferenceOpenHashSet<>();
    private static Map<String,Text> currentValues = new HashMap<>();
    private static boolean hasChanged = true;
    public static void processTabList(){
        MinecraftClient client = MinecraftClient.getInstance();
        SkyblockImprovements.push("SBI_TabListProcessor");
        if(client.getNetworkHandler()!=null){
            //get a Set of PlayerListEntries. Was previously getPlayerList().
            Set<PlayerListEntry> players = ((TabListAccessor)client.getNetworkHandler()).getListedPlayerList();
            //if nothing has changed, do nothing.
            if(players.equals(previousPlayers)){
                hasChanged = false;
                return;
            } 
            hasChanged = true;
            tabList = new ArrayList<>();
            players.forEach(player ->{
                Text temp = SpecialProcessor.isMyMessage(player.getDisplayName());
                if(temp!=null)
                    player.setDisplayName(temp);
                tabList.add(player.getDisplayName());
            });
            //shallow copy, looks for changes in what's being listed not changes in the currently listed ones
            previousPlayers.clear();
            previousPlayers.addAll(players);

            //call all finders
            getArea();
            getProfile();
        }
        SkyblockImprovements.pop();
    }
    public static Text getArea(){
        if(!hasChanged) return currentValues.get("Area");
        for(Text listEntry : tabList){
            if(listEntry!=null)
                if(listEntry.getString().contains("Area:")||listEntry.getString().contains("Dungeon:")){
                    currentValues.put("Area",listEntry);
                    return listEntry;
                
            }
        }
        currentValues.put("Area",Text.of("§cNo Area Found!"));
        return Text.of("§cNo Area Found!");
    }
    
    public static Text getProfile(){
        if(!hasChanged) return currentValues.get("Profile");
        for(Text listEntry : tabList){
            if(listEntry!=null)
                if(listEntry.getString().contains("Profile:")){
                    currentValues.put("Profile",listEntry);
                    return listEntry;
            }
        }
        currentValues.put("Profile",Text.of("§cNo Profile Found!"));
        return Text.of("§cNo Profile Found!");
    }
    public static Text getREDACTED(){
        if(!hasChanged) return currentValues.get("REDACTED");
        for(Text listEntry : tabList){
            if(listEntry!=null)
                if(listEntry.getString().contains("REDACTED:")){
                    currentValues.put("REDACTED",listEntry);
                    return listEntry;
            }
        }
        currentValues.put("REDACTED",Text.of("§cREDACTED not found. Try enabling your REDACTED /tablist widget."));
        return Text.of("§cREDACTED not found. Try enabling your REDACTED /tablist widget.");
    }
    public static Text getMana(){
        if(!hasChanged) return currentValues.get("Mana");
        for(Text listEntry : tabList){
            if(listEntry!=null)
                if(listEntry.getString().contains("Mana:")){
                    currentValues.put("Mana",listEntry);
                    return listEntry;
            }
        }
        currentValues.put("Mana",Text.of("§cMana not found. Try enabling your Mana /tablist widget."));
        return Text.of("§cMana not found. Try enabling your Mana /tablist widget.");
    }
    public static Text getSoulflow(){
        if(!hasChanged) return currentValues.get("Soulflow");
        for(Text listEntry : tabList){
            if(listEntry!=null)
                if(listEntry.getString().contains("Soulflow:")){
                    currentValues.put("Soulflow",listEntry);
                    return listEntry;
            }
        }
        currentValues.put("Soulflow",Text.of("§cSoulflow not found. Try enabling your Soulflow /tablist widget."));
        return Text.of("§cSoulflow not found. Try enabling your Soulflow /tablist widget.");
    }

    /* generic getter for tab list
       public static Text getSomething(){
        if(!hasChanged) return currentValues.get("Something");
        for(Text listEntry : tabList){
            if(listEntry!=null)
                if(listEntry.getString().contains("Something")){
                    currentValues.put("Something",listEntry);
                    return listEntry;
            }
        }
        currentValues.put("Something",Text.of("§cNothing Found!"));
        return Text.of("§cNothing Found!");
    }
     */
}
