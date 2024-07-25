package com.github.thepurityofchaos.features.packswapper;

import java.util.ArrayList;
import java.util.List;

import com.github.thepurityofchaos.SkyblockImprovements;
import com.github.thepurityofchaos.utils.gui.GUIElement;
import com.github.thepurityofchaos.utils.processors.ScoreboardProcessor;
import com.github.thepurityofchaos.utils.processors.TabListProcessor;
import com.github.thepurityofchaos.utils.screen.ScreenUtils;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
/**
 * Rendering functionality of the Pack Swapper.
 * <p> {@link #render(DrawContext, float)}: Checks for valid manipulations and renders the current Area & Region.
 */
public class PSRender {
    public static void render(DrawContext drawContext, float tickDelta){
        PackSwapper ps = PackSwapper.getInstance();
        if(ps.getFeatureVisual()!=null){
            SkyblockImprovements.push("SBI_PackSwapper");
            GUIElement location = ps.getFeatureVisual();
            int [] pos = new int[2];
            pos[0] = location.getCenteredX();
            pos[1] = location.getCenteredY();

            
            Text tempRegion = ScoreboardProcessor.getRegion();
            Text tempArea = TabListProcessor.getArea();
            ps.testForValidManipulation(tempArea,tempRegion);
            if(ps.isRendering()){
                //draw the text if the region exists
                List<Text> psText = new ArrayList<>();
                MutableText currentRegion = MutableText.of(Text.of("§"+ps.getRegionColor()+"Region:").getContent()); 
                MutableText currentArea = MutableText.of(Text.of("§"+ps.getRegionColor()).getContent());
                currentRegion.append(tempRegion);
                currentArea.append(tempArea);
                psText.add(currentArea);
                psText.add(currentRegion);
                
                if(ps.showPackHelper()){
                    psText.add(Text.of("§8§oIf you want to add a resource pack to automation, "));
                    psText.add(Text.of("§8§oadd a _ at the start! Then, simply type "));
                    psText.add(Text.of("§8§o/sbi PackSwapper config and choose where you want "));
                    psText.add(Text.of("§8§othe pack to be active! "));
                }
                ScreenUtils.draw(drawContext, psText, pos[0], pos[1], -1, -1, 10, -1, -1, -1, false);
            }
            SkyblockImprovements.pop();
        }

    }
}

