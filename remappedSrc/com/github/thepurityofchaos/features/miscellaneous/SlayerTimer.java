package com.github.thepurityofchaos.features.miscellaneous;

import com.github.thepurityofchaos.abstract_interfaces.Toggleable;
import com.github.thepurityofchaos.utils.Utils;
import com.github.thepurityofchaos.utils.processors.ScoreboardProcessor;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class SlayerTimer extends Toggleable {
    private static long questStartTime;
    private static long bossStartTime;
    private static boolean questStarted = false;
    private static boolean bossSpawned = false;
    public static void sendTime(){
        if(!questStarted){
            if(ScoreboardProcessor.onSlayerQuest()){
                questStartTime = System.currentTimeMillis();
                questStarted = true;
            }
        }
        if(!bossSpawned){
            if(ScoreboardProcessor.bossSpawned()){
                bossStartTime = System.currentTimeMillis();
                bossSpawned = true;
            }
        }
        else
            if(!ScoreboardProcessor.bossSpawned()&&bossSpawned){
                MinecraftClient client = MinecraftClient.getInstance();
                client.player.sendMessage(Text.of(
                    Utils.getColorString('6')+
                    "[§7SkyblockImprovements"+Utils.getColorString('6')+"]"+
                    " §7Quest time took: "+Utils.getColorString('b')+
                    Utils.getTime((bossStartTime-questStartTime)/1000)),false);
                client.player.sendMessage(Text.of(
                    Utils.getColorString('6')+
                    "[§7SkyblockImprovements"+Utils.getColorString('6')+"]"+
                    " §7Boss time took: "+Utils.getColorString('b')+
                    Utils.getTime((System.currentTimeMillis()-bossStartTime)/1000)),false);
                questStarted = false;
                bossSpawned = false;
            }
    }   
}
