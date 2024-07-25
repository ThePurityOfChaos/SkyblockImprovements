package com.github.thepurityofchaos.features.economic;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.github.thepurityofchaos.storage.config.EcoConfig;
import com.github.thepurityofchaos.utils.Utils;
import com.github.thepurityofchaos.utils.gui.GUIElement;
import com.github.thepurityofchaos.utils.processors.TabListProcessor;
import com.github.thepurityofchaos.utils.screen.ScreenUtils;

//import net.minecraft.client.MinecraftClient;
//import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
/**
 * Renderer for Economic Features. 
 * 
 * <p> {@link #render(DrawContext, float)}: Renders the features if they are enabled and need to be rendered.
 */
public class EcoRender {
    //@SuppressWarnings("resource")
    public static void render(DrawContext drawContext, float tickDelta){   
        //TextRenderer renderer = MinecraftClient.getInstance().textRenderer;
        BatFirework bf = BatFirework.getInstance();
        if(bf.isEnabled()){
         GUIElement location = bf.getFeatureVisual();
            int [] pos = new int[2];
            pos[0] = location.getCenteredX();
            pos[1] = location.getCenteredY();
            if(bf.getProfit()!=0.0){
                List<Text> batFireworkProfit = new ArrayList<>();
                    batFireworkProfit.add(Text.of(Utils.getColorString(EcoConfig.getColorCode())+"Bat Firework Profit:"));
                    batFireworkProfit.add(Text.of(Utils.getColorString(EcoConfig.getColorCode())+Utils.addCommas(((Double)bf.getProfit()).toString(),0)));
                ScreenUtils.draw(drawContext, batFireworkProfit, null, pos[0], pos[1], -1, -1, 10, 1074790416, -1, -1, true);
            }
        }
        GenericProfit gp = GenericProfit.getInstance();
        if(gp.isEnabled()){
            GUIElement location = gp.getFeatureVisual();
            
                List<Text> genericProfit = new ArrayList<>();
                if(gp.getProfit()!=0.0){
                    genericProfit.add(Text.of(Utils.getColorString(EcoConfig.getColorCode())+"Profit This Session:"));
                    genericProfit.add(Text.of(Utils.getColorString(EcoConfig.getColorCode())+Utils.addCommas(((Long)(long)(gp.getProfit())).toString(),gp.getPrecision())));
                    genericProfit.add(Text.of(Utils.getColorString(EcoConfig.getColorCode())+"Time This Session: "));
                    genericProfit.add(Text.of(Utils.getColorString(EcoConfig.getColorCode())+Utils.getTime((System.currentTimeMillis()-gp.getTime())/1000.0)));
                    genericProfit.add(Text.of(Utils.getColorString(EcoConfig.getColorCode())+"Profit Per Hour: "));
                    genericProfit.add(Text.of(Utils.getColorString(EcoConfig.getColorCode())+Utils.addCommas(((Long)gp.getProfitPerHour()).toString(),gp.getPrecision())));
                }
                Set<Text> errors = gp.getErrors();
                if(errors.size()!=0){
                    genericProfit.add(Text.of(Utils.getColorString('4')+"Unknown Item Prices Detected."));
                    genericProfit.add(Text.of(Utils.getColorString('4')+"Check Bazaar for:"));
                    genericProfit.addAll(errors);
                }
                if(genericProfit.size()!=0)
                    ScreenUtils.draw(drawContext, genericProfit, null, location.getCenteredX(), location.getCenteredY(), -1, -1, 10, 1074790416, -1, -1, true);
            
        }
        Bingo bng = Bingo.getInstance();
        if(bng.isEnabled()&&TabListProcessor.getProfile().getString().contains("Ⓑ")){
            GUIElement location = bng.getFeatureVisual();
            List<Text> tasks = bng.getTasks();
            if(tasks!=null && tasks.size()!=0)
                ScreenUtils.draw(drawContext, tasks, null, location.getCenteredX(), location.getCenteredY(), -1, -1, 10, 1074790416, -1, -1, true);
        }
    }
}
