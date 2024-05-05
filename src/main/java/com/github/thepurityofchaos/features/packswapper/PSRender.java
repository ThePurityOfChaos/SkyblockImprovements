package com.github.thepurityofchaos.features.packswapper;

import java.util.ArrayList;
import java.util.List;

import com.github.thepurityofchaos.utils.processors.ScoreboardProcessor;
import com.github.thepurityofchaos.utils.processors.TabListProcessor;
import com.github.thepurityofchaos.utils.screen.ScreenUtils;

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
            pos[0] = location.getX()+location.getWidth()/2;
            pos[1] = location.getY()+location.getHeight()/2;

            MutableText currentRegion = MutableText.of(Text.of("§"+PackSwapper.getRegionColor()+"Region:").getContent()); 
            MutableText currentArea = MutableText.of(Text.of("§"+PackSwapper.getRegionColor()).getContent());
            
            Text tempRegion = ScoreboardProcessor.getRegion();
            Text tempArea = TabListProcessor.getArea();
            currentRegion.append(tempRegion);
            currentArea.append(tempArea);
            PackSwapper.testForValidManipulation(tempArea,tempRegion);
            if(PackSwapper.isRendering()){
                //draw the text if the region exists
                List<Text> psText = new ArrayList<>();

                if(currentArea!=null)
                    psText.add(currentArea);
                if(currentRegion!=null)
                    psText.add(currentRegion);
                
                if(PackSwapper.showPackHelper()){
                    psText.add(Text.of("§8§oIf you want to add a resource pack to automation, "));
                    psText.add(Text.of("§8§oadd a _ at the start! Then, simply type "));
                    psText.add(Text.of("§8§o/sbi PackSwapper config and choose where you want "));
                    psText.add(Text.of("§8§othe pack to be active! "));
                }
                ScreenUtils.draw(drawContext, psText, pos[0], pos[1], -1, -1, 10, -1, -1, -1);
            }
            
        }

    }
}

