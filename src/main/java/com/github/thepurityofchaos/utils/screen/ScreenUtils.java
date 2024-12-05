package com.github.thepurityofchaos.utils.screen;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.github.thepurityofchaos.utils.math.ColorUtils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

public class ScreenUtils {
    
    public static Pair<Integer, Integer> draw(DrawContext context, int x, int y, int width, int height, int z, int baseColor, int lineStartColor, int lineEndColor, boolean centered){
        return draw(context, null, null, x, y, width, height, z, baseColor, lineStartColor,lineEndColor, 0, centered);
    }
    public static Pair<Integer, Integer> draw(DrawContext context, List<Text> text, int x, int y, int width, int height, int z, int baseColor, int lineStartColor, int lineEndColor, boolean centered){
        return draw(context,text, null, x, y, width, height, z, baseColor, lineStartColor, lineEndColor, 0, centered);
    }
    public static Pair<Integer, Integer> draw(DrawContext context, List<Text> text, int x, int y, int width, int height, int z, int baseColor, int lineStartColor, int lineEndColor, int firstLineOffset, boolean centered){
        return draw(context,text, null, x, y, width, height, z, baseColor, lineStartColor, lineEndColor, firstLineOffset, centered);
    }
    public static Pair<Integer, Integer> draw(DrawContext context, Identifier texture, int x, int y, int width, int height, int z, int baseColor, int lineStartColor, int lineEndColor, boolean centered){
        return draw(context, null, texture, x, y, width, height, z, baseColor, lineStartColor, lineEndColor, 0, centered);
    }
    public static Pair<Integer, Integer> draw(DrawContext context, @Nullable List<Text> texts, @Nullable Identifier texture, int x, int y, int width, int height, int z, int baseColor, int lineStartColor, int lineEndColor, boolean centered){
        return draw(context, texts, texture, x, y, width, height, z, 8, baseColor, lineStartColor, lineEndColor, 0, centered);
    }
    public static Pair<Integer, Integer> draw(DrawContext context, @Nullable List<Text> texts, @Nullable Identifier texture, int x, int y, int width, int height, int z, int baseColor, int lineStartColor, int lineEndColor, int firstLineOffset, boolean centered){
        return draw(context, texts, texture, x, y, width, height, z, 8, baseColor, lineStartColor, lineEndColor, firstLineOffset, centered);
    }

    //based on TooltipBackgroundRenderer.class and heavily modified by allowing custom colors, text, textures, and more. 
    //This allows SBI to mimic the visual structure of Minecraft's tooptips without copying the color scheme, texture, or text.
    //Use -1 for width and height if the values should be taken from the text.
    public static Pair<Integer,Integer> draw(DrawContext context, @Nullable List<Text> texts, @Nullable Identifier texture, int x, int y, int width, int height, int z, int textDistance, int baseColor, int lineStartColor, int lineEndColor, int firstLineOffset, boolean centered) {
        int i,j,k,l;
        //if undefined width and height, use the size of text.
        if(texts!=null && width < 0 && height < 0){
            MinecraftClient client = MinecraftClient.getInstance();
            int currentWidth = 0;
            int nulls = 0;
            for(Text text : texts){
                if(text!=null)
                    currentWidth = Math.max(currentWidth,client.textRenderer.getWidth(text));
                else{nulls++;}
            }
            width = currentWidth;
            height = (texts.size()-nulls) * textDistance;
            //center by text
            if(centered){
                i = x - width/2 - 3;
            }
            else{
                i = x - 3;
            }
        }else i = x - 3;

        k = width + 3 + 3;
        boolean inverted = false;
        if(textDistance<0){
            j = y + 3 - textDistance;
            l = height - 3 - 3;
            inverted = true;
        }else{
            j = y - 3;
            l = height + 3 + 3;
        }
        
        
        // -267386864 default base color, 1347420415 base line start color, 1344798847 base line end color.
        // color is in ARGB
        if(baseColor == -1){
            baseColor = ColorUtils.rGBAToInt(16, 0, 16,240);
        }
        if(lineStartColor == -1){
            lineStartColor = ColorUtils.rGBAToInt(80,0,255,80);
        }
        if(lineEndColor == -1){
            lineEndColor = ColorUtils.rGBAToInt(40,0,127,80);
        }
        drawHorizontalLine(context, i, j - 1, k, z, baseColor);
        drawHorizontalLine(context, i, j + l, k, z, baseColor);
        if(texture!=null){
            context.drawTexture(texture,
            //x,y
            i,j,
            //u,v
            0,0,
            //width,height
            k,l,
            //texture width,height
            k,l);
        }else{
            drawRectangle(context, i, j, k, l, z, baseColor);
        }
        
        drawVerticalLine(context, i - 1, j, l, z, baseColor);
        drawVerticalLine(context, i + k, j, l, z, baseColor);
        drawBorder(context, i, j + 1, k, l, z, lineStartColor, lineEndColor, inverted);
        if(texts!=null){
            MinecraftClient client = MinecraftClient.getInstance();
            int nulls = 0;
            for(int n = 0; n<texts.size(); n++){
                if(texts.get(n)==null){
                    nulls++;
                    continue;
                } 
                context.getMatrices().push();
                context.getMatrices().translate(0, 0, z+1);
                int y0 = y;
                if(n!=0){
                    y0+=firstLineOffset;
                }
                
                if(centered)
                    context.drawCenteredTextWithShadow(client.textRenderer,texts.get(n),x,y0+(n-nulls)*textDistance,1);
                else
                    context.drawTextWithShadow(client.textRenderer, texts.get(n), i+3, y0+(n-nulls)*textDistance, 1);
                context.getMatrices().pop();
            }
        }
        return new Pair<>(k,l);
    }

    public static void drawBorder(DrawContext context, int x, int y, int width, int height, int z, int startColor, int endColor, boolean inverted) {
        //context.fill() requires a positive width/height to be valid.
        if(inverted){
            drawVerticalLine(context, x, y+height, -height - 2, z+1, startColor, endColor);
            drawVerticalLine(context, x + width - 1, y+height, -height - 2, z+1, startColor, endColor);
            drawHorizontalLine(context, x, y - 1, width, z, endColor);
            drawHorizontalLine(context, x, y - 1 + height - 1, width, z, startColor);
        }else{
            drawVerticalLine(context, x, y, height - 2, z, startColor, endColor);
            drawVerticalLine(context, x + width - 1, y, height - 2, z, startColor, endColor);
            drawHorizontalLine(context, x, y - 1, width, z, startColor);
            drawHorizontalLine(context, x, y - 1 + height - 1, width, z, endColor);
        }
    }
      
    public static void drawVerticalLine(DrawContext context, int x, int y, int height, int z, int color) {
        context.fill(x, y, x + 1, y + height, z, color);
    }
      
    public static void drawVerticalLine(DrawContext context, int x, int y, int height, int z, int startColor, int endColor) {
        context.fillGradient(x, y, x + 1, y + height, z, startColor, endColor);
    }
      
    public static void drawHorizontalLine(DrawContext context, int x, int y, int width, int z, int color) {
        context.fill(x, y, x + width, y + 1, z, color);
    }
      
    public static void drawRectangle(DrawContext context, int x, int y, int width, int height, int z, int color) {
        context.fill(x, y, x + width, y + height, z, color);
    }
}
