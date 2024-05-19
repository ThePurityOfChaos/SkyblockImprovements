package com.github.thepurityofchaos.features.itempickuplog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.thepurityofchaos.SkyblockImprovements;
import com.github.thepurityofchaos.utils.inventory.ChangeInstance;
import com.github.thepurityofchaos.utils.processors.ScoreboardProcessor;
import com.github.thepurityofchaos.utils.screen.ScreenUtils;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;


//https://fabric.moddedmc.wiki/rendering/ helps here
public class IPLRender {
   
    public static void render(DrawContext drawContext, float tickDelta){
        SkyblockImprovements.push("SBI_ItemPickupLog");
        if(ItemPickupLog.getFeatureVisual()!=null){
            ButtonWidget location = ItemPickupLog.getFeatureVisual();
            int [] pos = new int[2];
            pos[0] = location.getX()+location.getWidth()/2;
            pos[1] = location.getY()+location.getHeight()/2;
            ItemPickupLog.determineChanges();
            ItemPickupLog.cleanLog();
            ChangeInstance[] log =  ItemPickupLog.getLog().toArray(new ChangeInstance[ItemPickupLog.getLog().size()]);

            //if you're joining or leaving Skyblock or The Rift, reset the log.
            if(Arrays.equals(ScoreboardProcessor.regionChange(), new boolean[2])){
                List<Text> logText = new ArrayList<>();
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
                    logText.add(temp);
                    }
                //finally, draw the text.
                if(logText.size()>0)
                    ScreenUtils.draw(drawContext, logText, null, pos[0], pos[1], -1, -1, 10, -ChangeInstance.getDistance(), 1074790416, -1, -1);
            //to prevent some issues
            }else { ItemPickupLog.resetLog(); }
        }else {ItemPickupLog.resetLog();}   
        SkyblockImprovements.pop();
    }
}
