package com.github.thepurityofchaos.features.economic;

import java.util.ArrayList;
import java.util.List;

import com.github.thepurityofchaos.config.EcoConfig;
import com.github.thepurityofchaos.utils.Utils;
import com.github.thepurityofchaos.utils.screen.ScreenUtils;

//import net.minecraft.client.MinecraftClient;
//import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class EcoRender {
    //@SuppressWarnings("resource")
    public static void render(DrawContext drawContext, float tickDelta){   
        //TextRenderer renderer = MinecraftClient.getInstance().textRenderer;
         ButtonWidget location = BatFirework.getFeatureVisual();
            int [] pos = new int[2];
            pos[0] = location.getX()+location.getWidth()/2;
            pos[1] = location.getY()+location.getHeight()/2;
            if(BatFirework.getProfit()!=0.0){
                List<Text> batFireworkProfit = new ArrayList<>();
                batFireworkProfit.add(Text.of(Utils.getColorString(EcoConfig.getColorCode())+"Bat Firework Profit:"));
                batFireworkProfit.add(Text.of(Utils.getColorString(EcoConfig.getColorCode())+Utils.addCommas(Utils.normalizeDouble(BatFirework.getProfit()))));
                ScreenUtils.draw(drawContext, batFireworkProfit, null, pos[0], pos[1], -1, -1, 10, 1074790416, -1, -1);
            }
    }
}
