package com.github.thepurityofchaos.utils.render;

import com.github.thepurityofchaos.features.itempickuplog.ItemPickupLog;
import com.github.thepurityofchaos.utils.inventory.ChangeInstance;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.text.Text;


//https://fabric.moddedmc.wiki/rendering/ helps here
public class IPLRender {
    public static void render(DrawContext drawContext, float tickDelta){
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            buffer.begin(DrawMode.QUADS,VertexFormats.POSITION);
            int [] pos = new int[2];
            if(ItemPickupLog.getFeatureVisual()!=null){
                ButtonWidget location = ItemPickupLog.getFeatureVisual().getWidget();
                pos[0] = location.getX();
                pos[1] = location.getY();
                ItemPickupLog.determineChanges();
                ItemPickupLog.cleanLog();
            }

            @SuppressWarnings("resource")
            TextRenderer renderer = MinecraftClient.getInstance().textRenderer;
            ChangeInstance[] log =  ItemPickupLog.getLog().toArray(new ChangeInstance[ItemPickupLog.getLog().size()]);
           
            for(int i=0; i<log.length; i++){

                Text temp = Text.of(
                    
                    //color (positive or negative)
                    ((log[i].getCount()>0?"§a+":"§c"))+
                    //count if not from sacks
                    log[i].getCount()+"§"+ChangeInstance.getColorCode()+" "+
                    //if from sacks, remove all duplicate or unwanted characters, and add in specialty color code
                    (log[i].isFromSacks()?
                    (log[i].getName().getString()
                    //remove line feed character
                    .replace("\n","")
                    //remove extraneous count
                    .replace(Integer.toString(log[i].getCount()),""))
                    //remove extraneous + if it exists
                    .replace("+","")
                    //darken (Sack Type) portion of message
                    .replace("(","§8(")
                    //remove extraneous spaces
                    .replace("   ","")
                    //otherwise just get the string
                    :log[i].getName().getString()));
                
                

                drawContext.drawText(renderer,temp,pos[0],pos[1]-i*ChangeInstance.getDistance(),1,true);
            }
            RenderSystem.setShader(GameRenderer::getPositionProgram);
            tessellator.draw();
            RenderSystem.setShaderColor(1f,1f,1f,1f);
        }
}
