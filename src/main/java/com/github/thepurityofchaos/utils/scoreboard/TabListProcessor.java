package com.github.thepurityofchaos.utils.scoreboard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;

public class TabListProcessor {
    private static List<Text> tabList = null;
    public static void processTabList(){
        tabList = new ArrayList<>();
        MinecraftClient client = MinecraftClient.getInstance();
        if(client.getNetworkHandler()!=null){
            //get a Set of PlayerListEntries. Was previously getPlayerList().
            Collection<PlayerListEntry> players = client.getNetworkHandler().getListedPlayerListEntries();
            for(PlayerListEntry player : players){
                tabList.add(player.getDisplayName());
            }
        }
    }
    public static Text getArea(){
        for(Text listEntry : tabList){
            if(listEntry!=null)
                if(listEntry.getString().contains("Area:")){
                    return listEntry;
            }
        }
        return Text.of("No Area Found!");
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
