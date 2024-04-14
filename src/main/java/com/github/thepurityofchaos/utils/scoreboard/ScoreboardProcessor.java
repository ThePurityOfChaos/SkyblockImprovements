package com.github.thepurityofchaos.utils.scoreboard;

import java.util.List;
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
 * <p>{@link #getScoreboard getScoreboard()}: Gets a List<Text> containing everything in the current scoreboard. It's suggested to call processScoreboard() before calling this, unless you want the previous instance.
 * 
 * <p>{@link #processScoreboard processScoreboard()}: Updates the current state of the Scoreboard.
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
    private static Text previousRegion;



    @SuppressWarnings("resource")
    public static void processScoreboard(){
        try{
            //the current scoreboard
            Scoreboard scoreboard = MinecraftClient.getInstance().player.getScoreboard();
            List<Text> newScoreboard = new ArrayList<>();
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
        }catch(NullPointerException e){
            //occurs when player is null, such as when in the title screen or loading into a world. This happens sometimes, so it's expected and no action is taken.
        }
        //fixes an issue with uncertainty in regions.
        getRegion();
    }


    public static List<Text> getScoreboard(){
        return currentScoreboard;
    }


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
}
