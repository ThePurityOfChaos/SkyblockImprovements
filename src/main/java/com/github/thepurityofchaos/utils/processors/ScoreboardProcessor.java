package com.github.thepurityofchaos.utils.processors;

import java.util.List;

import com.github.thepurityofchaos.SkyblockImprovements;
import com.github.thepurityofchaos.features.miscellaneous.SlayerTimer;

import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.client.MinecraftClient;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;


/**
 * The main processor for all Scoreboard operations in the mod.
 * 
 * <p> {@link #getScoreboard getScoreboard()}: Gets a List<Text> containing everything in the current scoreboard. It's suggested to call processScoreboard() before calling this, unless you want the previous instance.
 * 
 * <p> {@link #processScoreboard processScoreboard()}: Updates the current state of the Scoreboard.
 * 
 * <p> {@link #getRegion getRegion()}: Gets the current region in {@link Text} format. 
 * 
 * <p> {@link #regionChange regionChange()}: returns if the region changed for [skyblock,rift]. This returns an Boolean ARRAY of size [2], not just a Boolean.
 * 
 * <p> {@link #isInRift() isInRift()}: Returns whether or not the player is in the Rift.
 * 
 * 
**/
public class ScoreboardProcessor {
    private static List<Text> currentScoreboard = new ArrayList<>();
    private static boolean isInRift = false;
    private static boolean wasInRift = false;
    private static boolean isOnSkyblock = false;
    private static boolean wasOnSkyblock = false;
    private static int recentChange = 100;
    private static int timer = 20;
    private static Text previousRegion;



    
    public static void processScoreboard(){
        SkyblockImprovements.push("SBI_ScoreboardProcessor");
        try{
            //parse the current scoreboard

            //only perform this every second
            timer--;
            if(timer>0) return;
            MinecraftClient client = MinecraftClient.getInstance();
            Scoreboard scoreboard = client.player.getScoreboard();
            List<Text> newScoreboard = new ArrayList<>();
            //we're only looking for objectives in the sidebar
            ScoreboardObjective sidebar = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR);
            for(ScoreHolder h : scoreboard.getKnownScoreHolders()){
                if(scoreboard.getScoreHolderObjectives(h).containsKey(sidebar)){
                    Team team = scoreboard.getScoreHolderTeam(h.getNameForScoreboard());
                    if(team!=null){
                        newScoreboard.add(Text.of(team.getPrefix().copy().append(team.getSuffix().copy())));
                    }
                }
            }
            currentScoreboard = new ArrayList<>();
            for(Text text : newScoreboard){
                MutableText temp = MutableText.of(Text.of("").getContent());
                Iterator<Text> siblings = text.getSiblings().iterator();

                while(siblings.hasNext())
                    temp.append(siblings.next());
                currentScoreboard.add(Text.of(temp));
            }
            SlayerTimer.sendTime();
            timer = 20;
        }catch(NullPointerException e){
            //occurs when player is null, such as when in the title screen or loading into a world. This happens sometimes, so it's expected and no action is taken.
        }
        //fixes an issue with uncertainty in regions.
        getRegion();
        SkyblockImprovements.pop();

    }

    /**
     * 
     * @return the list of every line on the Sidebar.
     * 
     */
    public static List<Text> getScoreboard(){
        return currentScoreboard;
    }

    /**
     * 
     * @return the Text associated with the current region, or Not On Skyblock if no region is found.
     * 
     */
    public static Text getRegion(){
        for(Text scoreboardText : currentScoreboard){
            //Rift
            if(scoreboardText.getString().contains("ф")){
                isInRift = true;
                isOnSkyblock = true;
                return scoreboardText;
            }
            isInRift = false;
            //Normal Region
            if(scoreboardText.getString().contains("⏣")){
                isOnSkyblock = true;
                return scoreboardText;
            }
        }
        isOnSkyblock = false;
        return Text.of(" §cNot on Skyblock!");
    }
    public static boolean isInRift(){
        return isInRift;
    }
    public static boolean isOnSkyblock(){
        return isOnSkyblock;
    }
    
    public static boolean[] regionChange(){
        boolean[] regionChanges = new boolean[2];
        regionChanges[0] = wasOnSkyblock!=isOnSkyblock;
        regionChanges[1] = wasInRift!=isInRift;
        //maintain the region change for a short while to prevent dependent classes, such as IPLRender, from drawing for 1/10th of a second.
        if(recentChange<=0){
        wasInRift = isInRift;
        wasOnSkyblock = isOnSkyblock;
        recentChange = 100;
        }
        recentChange--;
        return regionChanges;
    }
    //a more targeted check to see if the current scoreboard region changed
    public static boolean dynamicRegionChange(){
        Text currentRegion = getRegion();
        boolean regionChanged = false;
        if(!previousRegion.equals(currentRegion))
            regionChanged = true;
        previousRegion = currentRegion;
        return regionChanged;
    }
    public static boolean bossSpawned(){
        for(Text scoreboardText : currentScoreboard){
            //Boss Spawned
            if(scoreboardText.getString().contains("Slay the boss!")){
                return true;
            }
        }
        return false;
    }

    public static boolean onSlayerQuest() {
        for(Text scoreboardText : currentScoreboard){
            //On a Slayer Quest
            if(scoreboardText.getString().contains("Slayer Quest")){
                return true;
            }
        }
        return false;
    }
}
