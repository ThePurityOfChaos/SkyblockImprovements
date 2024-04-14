package com.github.thepurityofchaos.features.packswapper;

import com.github.thepurityofchaos.utils.scoreboard.ScoreboardProcessor;
import com.github.thepurityofchaos.utils.scoreboard.TabListProcessor;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class PSRender {
    @SuppressWarnings("resource")
    public static void render(DrawContext drawContext, float tickDelta){
        if(PackSwapper.getFeatureVisual()!=null){
            ButtonWidget location = PackSwapper.getFeatureVisual();
            int [] pos = new int[2];
            pos[0] = location.getX();
            pos[1] = location.getY();
            TextRenderer renderer = MinecraftClient.getInstance().textRenderer;
            MutableText currentRegion = MutableText.of(Text.of("§"+PackSwapper.getRegionColor()+"Region:").getContent()); 
            MutableText currentArea = MutableText.of(Text.of("§"+PackSwapper.getRegionColor()).getContent());
            
            Text tempRegion = ScoreboardProcessor.getRegion();
            Text tempArea = TabListProcessor.getArea();
            currentRegion.append(tempRegion);
            currentArea.append(tempArea);
            PackSwapper.testForValidManipulation(tempArea,tempRegion);
            if(PackSwapper.isRendering()){
                Text validResourcePackName = Text.of("§"+PackSwapper.getRegionColor()+"Expected Pack Name: SBIMP_"+
                tempArea.getString().replace("Area:","").replace(" ","")+
                "-"+tempRegion.getString().replace("ф","").replace("⏣","").replace(" ",""));
                //draw the text if the region exists
                if(currentRegion!=null)
                    drawContext.drawText(renderer, currentRegion, pos[0], pos[1], 1, true);
                if(currentArea!=null)
                    drawContext.drawText(renderer, currentArea, pos[0], pos[1]-8, 1, true);
                if(PackSwapper.showPackHelper()){
                    drawContext.drawText(renderer, validResourcePackName, pos[0], pos[1]+8, 1, true);
                    drawContext.drawText(renderer, Text.of("§8§oPack Name can ignore anything after the - character."), pos[0], pos[1]+16, 1, true);
                    drawContext.drawText(renderer, Text.of("§8§oBy adding on an [other region] to the pack name, you "), pos[0], pos[1]+24, 1, true);
                    drawContext.drawText(renderer, Text.of("§8§ocan make the pack work in multiple areas and regions! "), pos[0], pos[1]+32, 1, true);
                    drawContext.drawText(renderer, Text.of("§8§oThis works both before and after the -. You can also  "), pos[0], pos[1]+40, 1, true);
                    drawContext.drawText(renderer, Text.of("§8§onot include - to make a pack that works in a full area."), pos[0], pos[1]+48, 1, true);
                    drawContext.drawText(renderer, Text.of("§8§oPack Names must start with SBIMP_ to be valid, otherwise they will be ignored."), pos[0], pos[1]+56, 1, true);
                }
            }
            
        }

    }
}

