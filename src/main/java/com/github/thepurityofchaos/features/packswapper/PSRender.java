package com.github.thepurityofchaos.features.packswapper;

import com.github.thepurityofchaos.utils.Utils;
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
                //cut out unneeded portions of the string
                String tempAreaString = Utils.clearArea(tempArea.getString()).replace("§c","");
                String tempRegionString = Utils.clearRegion(tempRegion.getString()).replace("§c","");

                //EXPERIMENTAL FEATURES
                if(PackSwapper.experimental_useShortArea())
                    tempAreaString = Utils.removeLowerCase(tempAreaString);
                if(PackSwapper.experimental_useShortRegion())
                    tempRegionString = Utils.removeLowerCase(tempRegionString);
                //draw the text if the region exists
                if(currentRegion!=null)
                    drawContext.drawText(renderer, currentRegion, pos[0], pos[1], 1, true);
                if(currentArea!=null)
                    drawContext.drawText(renderer, currentArea, pos[0], pos[1]-8, 1, true);
                if(PackSwapper.showPackHelper()){
                    drawContext.drawText(renderer, Text.of("§8§oIf you want to add a resource pack to automation, "), pos[0], pos[1]+8, 1, true);
                    drawContext.drawText(renderer, Text.of("§8§oadd a _ at the start! Then, simply type "), pos[0], pos[1]+16, 1, true);
                    drawContext.drawText(renderer, Text.of("§8§o/sbi PackSwapper config and choose where you want "), pos[0], pos[1]+24, 1, true);
                    drawContext.drawText(renderer, Text.of("§8§othe pack to be active! "), pos[0], pos[1]+32, 1, true);
                }
            }
            
        }

    }
}

