package com.github.thepurityofchaos.features.economic;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
        if(BatFirework.getFeatureEnabled()){
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
        if(GenericProfit.getFeatureEnabled()){
            ButtonWidget location = GenericProfit.getFeatureVisual();
            int [] pos = new int[2];
            pos[0] = location.getX()+location.getWidth()/2;
            pos[1] = location.getY()+location.getHeight()/2;
            
                List<Text> genericProfit = new ArrayList<>();
                if(GenericProfit.getProfit()!=0.0){
                genericProfit.add(Text.of(Utils.getColorString(EcoConfig.getColorCode())+"Profit This Session:"));
                genericProfit.add(Text.of(Utils.getColorString(EcoConfig.getColorCode())+Utils.addCommas(((Long)(long)(GenericProfit.getProfit())).toString())));
                genericProfit.add(Text.of(Utils.getColorString(EcoConfig.getColorCode())+"Time This Session: "));
                genericProfit.add(Text.of(Utils.getColorString(EcoConfig.getColorCode())+Utils.getTime((System.currentTimeMillis()-GenericProfit.getTime())/1000.0)));
                genericProfit.add(Text.of(Utils.getColorString(EcoConfig.getColorCode())+"Profit Per Hour: "));
                genericProfit.add(Text.of(Utils.getColorString(EcoConfig.getColorCode())+Utils.addCommas(((Long)GenericProfit.getProfitPerHour()).toString())));
                }
                Set<Text> errors = GenericProfit.getErrors();
                if(errors.size()!=0){
                    genericProfit.add(Text.of(Utils.getColorString('4')+"Unknown Item Prices Detected."));
                    genericProfit.add(Text.of(Utils.getColorString('4')+"Check Bazaar for:"));
                    genericProfit.addAll(errors);
                }
                if(genericProfit.size()!=0)
                ScreenUtils.draw(drawContext, genericProfit, null, pos[0], pos[1], -1, -1, 10, 1074790416, -1, -1);
            
        }
    }
}
