package com.github.thepurityofchaos.utils.processors;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.github.thepurityofchaos.SkyblockImprovements;
import com.github.thepurityofchaos.listeners.SpecialListener;
import com.github.thepurityofchaos.mixin.TabListAccessor;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;

public class TabListProcessor {
    private static List<Text> tabList = null;
    private static Set<PlayerListEntry> previousPlayers = new ReferenceOpenHashSet<>();
    public static void processTabList(){
        MinecraftClient client = MinecraftClient.getInstance();
        SkyblockImprovements.push("SBI_TabListProcessor");
        if(client.getNetworkHandler()!=null){
            //get a Set of PlayerListEntries. Was previously getPlayerList().
            Set<PlayerListEntry> players = ((TabListAccessor)client.getNetworkHandler()).getListedPlayerList();
            //if nothing has changed, do nothing.
            if(players.equals(previousPlayers)){
                SkyblockImprovements.pop();
                return;
            } 
            tabList = new ArrayList<>();
            players.forEach(player ->{
                Text temp = SpecialListener.isMyMessage(player.getDisplayName());
                if(temp!=null)
                    player.setDisplayName(temp);
                tabList.add(player.getDisplayName());
            });
            //shallow copy, looks for changes in what's being listed not changes in the currently listed ones
            previousPlayers.clear();
            previousPlayers.addAll(players);

            
        }
        SkyblockImprovements.pop();
    }
    public static Text getArea(){
        for(Text listEntry : tabList){
            if(listEntry!=null)
                if(listEntry.getString().contains("Area:")||listEntry.getString().contains("Dungeon:")){
                    return listEntry;
                
            }
        }
        return Text.of(" §cNo Area Found!");
    }
    
    /* generic getter for tab list
     * public static Text getSomething(){
        for(Text listEntry : tabList){
            if(listEntry!=null)
                if(listEntry.getString().contains(Something)){
                    return listEntry;
            }
        }
        return Text.of("Nothing Found!");
    }
     */
}
