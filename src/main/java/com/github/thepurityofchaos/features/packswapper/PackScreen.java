package com.github.thepurityofchaos.features.packswapper;

import org.jetbrains.annotations.Nullable;

import java.util.Map;

import com.github.thepurityofchaos.utils.Utils;
import com.github.thepurityofchaos.utils.gui.GUIElement;
import com.github.thepurityofchaos.utils.gui.GUIScreen;
import com.github.thepurityofchaos.utils.processors.ScoreboardProcessor;
import com.github.thepurityofchaos.utils.processors.TabListProcessor;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;


/**
 * The Config Screen for each Pack.
 * <p> {@link #initAsPackMap(Screen, Map)}: Initialize a Screen which contains buttons that map to a Pack.
 * <p> {@link #initAsAreaMap(Screen, Map)}: Area Sub-screen for each Pack in the PackMap.
 * <p> {@link #initAsRegionMap(Screen, Map)}: Region Sub-screen for each Region in the AreaMap. 
 * 
 */
public class PackScreen extends GUIScreen {
    private static int verticalPerHorizontal = 8;
    private static int buttonWidth = 128;
    private static int buttonHeight = 32;
    private static int buttonOffset = 32;

    //Pack Screen. These three methods were originally intended to be an overload of init(), but Java uses type erasure so it became ambiguous.
    public Screen initAsPackMap(@Nullable Screen parent, Map<String,Map<String,Map<String,Boolean>>> map){
        int index = 0;
        for(Map.Entry<String,Map<String,Map<String,Boolean>>> entry : map.entrySet()){
            if(entry.getValue() instanceof Map){
                GUIElement element = new GUIElement((index/verticalPerHorizontal)*buttonWidth+buttonOffset, (index%verticalPerHorizontal)*buttonHeight+buttonOffset, buttonWidth, buttonHeight, button ->{
                    PackScreen s = (PackScreen)new PackScreen().initAsAreaMap(this,entry.getValue());
                    client.setScreen(s);
                    
                });
                element.setMessage(Text.of(Utils.getColorString(PackSwapper.getRegionColor())+entry.getKey().replace("file/_","").replace(".zip","")));
                addElement(entry.getKey(),element);
            }
            index++;
        }
        init(parent);
        return this;
    }

    //Area Screens.
    public Screen initAsAreaMap(@Nullable Screen parent, Map<String,Map<String,Boolean>> map){
        int index = 0;
        for(Map.Entry<String,Map<String,Boolean>> entry : map.entrySet()){
            if(entry.getValue() instanceof Map){
                GUIElement element = new GUIElement((index/verticalPerHorizontal)*buttonWidth+buttonOffset, (index%verticalPerHorizontal)*buttonHeight+buttonOffset, buttonWidth, buttonHeight, button ->{
                    PackScreen s = (PackScreen)new PackScreen().initAsRegionMap(this,entry.getValue());
                    client.setScreen(s);
                });
                element.setMessage(Text.of(Utils.getColorString(PackSwapper.getRegionColor())+entry.getKey()));
                addElement(entry.getKey(),element);
            }
            index++;
        }
        init(parent);
        return this;
    }

    //Region Screens.
    public Screen initAsRegionMap(@Nullable Screen parent, Map<String,Boolean> map){
        int index = 0;
        for(Map.Entry<String,Boolean> entry : map.entrySet()){
            if(entry.getValue() instanceof Boolean){
                GUIElement element = new GUIElement((index/verticalPerHorizontal)*buttonWidth+buttonOffset,(index%verticalPerHorizontal)*buttonHeight+buttonOffset,buttonWidth,buttonHeight,button->{
                    map.put(entry.getKey(),!map.get(entry.getKey()));
                    getElement(entry.getKey()).setMessage(Text.of(Utils.getColorString(PackSwapper.getRegionColor())+(entry.getKey().equals("")?"All Regions":entry.getKey())+Utils.getStringFromBoolean(entry.getValue())));
                });
            element.setMessage(Text.of(Utils.getColorString(PackSwapper.getRegionColor())+(entry.getKey().equals("")?"All Regions":entry.getKey())+Utils.getStringFromBoolean(entry.getValue())));
            addElement(entry.getKey(),element);
            }
            index++;
        }
        init(parent);
        return this;
    }
    
    public void close(){
        PackSwapper.manipulatePacks(TabListProcessor.getArea().getString(), ScoreboardProcessor.getRegion().getString());
        super.close();
    }
}
