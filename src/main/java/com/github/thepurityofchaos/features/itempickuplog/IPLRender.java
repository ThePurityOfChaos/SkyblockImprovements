package com.github.thepurityofchaos.features.itempickuplog;

import java.util.Arrays;

import com.github.thepurityofchaos.utils.inventory.ChangeInstance;
import com.github.thepurityofchaos.utils.processors.ScoreboardProcessor;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;


//https://fabric.moddedmc.wiki/rendering/ helps here
public class IPLRender {
    @SuppressWarnings("resource")
    public static void render(DrawContext drawContext, float tickDelta){
            if(ItemPickupLog.getFeatureVisual()!=null){
                ButtonWidget location = ItemPickupLog.getFeatureVisual();
                int [] pos = new int[2];
                pos[0] = location.getX();
                pos[1] = location.getY();
                ItemPickupLog.determineChanges();
                ItemPickupLog.cleanLog();
                TextRenderer renderer = MinecraftClient.getInstance().textRenderer;
                ChangeInstance[] log =  ItemPickupLog.getLog().toArray(new ChangeInstance[ItemPickupLog.getLog().size()]);

                //if you're joining or leaving Skyblock or The Rift, reset the log.
                if(Arrays.equals(ScoreboardProcessor.regionChange(), new boolean[2])){
                    for(int i=0; i<log.length; i++){
                        //create a temporary MutableText to show the data for a ChangeInstance.
                        MutableText temp = MutableText.of(
                            Text.of(
                                //color (positive or negative)
                                ((log[i].getCount()>0?"§a+":"§c"))+
                                //count
                                log[i].getCount()+"x§"+ChangeInstance.getColorCode()+" "+
                                //name: multiple tests to determine whether in sacks or not / whether or not to use custom data if it exists
                                ((log[i].isFromSacks()==true || log[i].getName().getStyle()==null)?
                                log[i].getName().getString():"")
                                )
                            .getContent()
                        );

                        //if it has passed both the sacks and Style checks, append the name as a sibling. 
                        //This keeps the Text's internal data, such as color.

                        if(!log[i].isFromSacks() && log[i].getName().getStyle()!=null)
                            temp.append(log[i].getName());
                        //finally, draw the text.
                        drawContext.drawText(renderer,temp,pos[0],pos[1]-i*ChangeInstance.getDistance(),1,true);
                    }
            //to prevent some issues
            }else { ItemPickupLog.resetLog(); }
        }else {ItemPickupLog.resetLog();}
            
    }
}
