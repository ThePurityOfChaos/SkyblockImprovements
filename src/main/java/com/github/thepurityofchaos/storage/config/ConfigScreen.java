package com.github.thepurityofchaos.storage.config;

import org.jetbrains.annotations.Nullable;

import com.github.thepurityofchaos.features.economic.BatFirework;
import com.github.thepurityofchaos.features.economic.Bingo;
import com.github.thepurityofchaos.features.economic.GenericProfit;
import com.github.thepurityofchaos.features.itempickuplog.ItemPickupLog;
import com.github.thepurityofchaos.features.packswapper.PackScreen;
import com.github.thepurityofchaos.features.packswapper.PackSwapper;
import com.github.thepurityofchaos.storage.Sacks;
import com.github.thepurityofchaos.utils.gui.GUIElement;
import com.github.thepurityofchaos.utils.gui.MenuElement;
import com.github.thepurityofchaos.utils.gui.MenuScreen;
import com.github.thepurityofchaos.utils.gui.TextFieldElement;
import com.github.thepurityofchaos.utils.inventory.ChangeInstance;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

/**
 * The Config Screen allows the player to modify the display location of any feature added to this screen.
 * 
 * <p> {@link #init(Nullable Screen)}: Creates the screen with every included feature's visual component. 
 * 
 * <p> {@link #modifyElementLocation(String element, int[] location)} : Sets the element's position to the first two integers in location. Expects an int array of size 2.
 */
public class ConfigScreen extends MenuScreen {
    private static boolean initialized = false;  
    public void init(@Nullable Screen parent){
        defineElements();
        center(true);
        //Item Pickup Log
        this.addElement("ItemPickupLog",ItemPickupLog.getInstance().getFeatureVisual());

        //Pack Swapper
        this.addElement("PackSwapper",PackSwapper.getInstance().getFeatureVisual());

        //Bat Firework
        this.addElement("BatFirework",BatFirework.getInstance().getFeatureVisual());

        //Generic Profit
        this.addElement("GenericProfit", GenericProfit.getInstance().getFeatureVisual());

        //Bingo Tasks
        this.addElement("Bingo", Bingo.getInstance().getFeatureVisual());

        //generic
        //this.addElement("name"),Feature.getFeatureVisual();
        
        super.init(parent);
    }
    /**
     * Takes in the name of an element and an integer array of at least size 2. Throws an IllegalArgumentException if the integer array contains only a single element.
     * @param element
     * @param location
     * @throws IllegalArgumentException
     */
    public void modifyElementLocation(String element, int[] location) throws IllegalArgumentException{
        this.getElement(element).setPosition(location[0], location[1]);
    }
    /**
     * Closes the Screen, saving the Config's changes.
     */
    public void close(){
        // only call saveSettings when the config screen closes, to minimize writing needed
        Config.saveSettings();
        super.close();
    }
    private void defineElements(){
        if(!initialized){
            //Item Pickup Log
            //define elements
            GUIElement iplSackElement = new GUIElement(0, 0, 96, 32, button ->{
                Sacks.getInstance().toggle();
            });
            GUIElement iplMessageElement = new GUIElement(0, 0, 96, 32, button ->{
                IPLConfig.toggleRemoval();
            });
            GUIElement iplToggleElement = new GUIElement(0, 0, 96, 32, button ->{
                ItemPickupLog.getInstance().toggle();
            });
            TextFieldElement iplColorCodeElement = new TextFieldElement(0, 0, 64, 32,Text.of(ChangeInstance.getColorCode()+""));
            iplColorCodeElement.setPressAction(button->{
                try{
                ChangeInstance.setColorCode(iplColorCodeElement.getText().charAt(0));
                IPLConfig.saveSettings();
                }catch(Exception e){
                    iplColorCodeElement.setText(ChangeInstance.getColorCode()+"");
                }
            });
            TextFieldElement iplDistanceElement = new TextFieldElement(0, 0, 64, 32,Text.of(ChangeInstance.getDistance()+""));
            iplDistanceElement.setPressAction(button->{
                try{
                    ChangeInstance.setDistance(Integer.parseInt(iplDistanceElement.getText()));
                    IPLConfig.saveSettings();
                }catch(Exception e){
                    iplDistanceElement.setText(ChangeInstance.getDistance()+"");
                }
            });
            //messages
            iplToggleElement.setMessage(Text.of("Toggle Feature"));
            iplSackElement.setMessage(Text.of("Sack Numbers"));
            iplMessageElement.setMessage(Text.of("Remove Message"));
            iplColorCodeElement.setMessage(Text.of("Color Code"));
            iplDistanceElement.setMessage(Text.of("Distance"));

            //tooltips
            iplToggleElement.setTooltip(Text.of("Whether or not to show the Item Pickup Log."));
            iplSackElement.setTooltip(Text.of("Whether or not to Show Sack Numbers."));
            iplMessageElement.setTooltip(Text.of("Whether or not to remove the Sacks Message when it is sent."));
            iplColorCodeElement.setTooltip(Text.of("Color Code for the Sack items."));
            iplDistanceElement.setTooltip(Text.of("Vertical Distance between items."));

            //add them in
            MenuElement IPLVisual = (MenuElement)ItemPickupLog.getInstance().getFeatureVisual();
            IPLVisual.addSubElement(iplToggleElement);
            IPLVisual.addSubElement(iplSackElement);
            IPLVisual.addSubElement(iplMessageElement);
            IPLVisual.addSubElement(iplColorCodeElement);
            IPLVisual.addSubElement(iplDistanceElement);

            //Pack Swapper
            //define elements
            GUIElement psToggle = new GUIElement(0, 0, 96, 32, button ->{
                PackSwapper.getInstance().toggleRenderComponent();
            });
            GUIElement psRPHelper = new GUIElement(0, 0, 96, 32, button ->{
                PackSwapper.getInstance().togglePackHelper();
            });
            GUIElement psDebugInfo = new GUIElement(0, 0, 96, 32, button ->{
                PackSwapper.getInstance().toggleDebugInfo();
            });
            GUIElement psConfig = new GUIElement(0, 0, 96, 32, button ->{
                PackScreen screen = new PackScreen();
                screen.initAsPackMap(this,PackSwapper.getInstance().getFullRegionMap());
                client.setScreen(screen);
            });
            TextFieldElement psColorCode = new TextFieldElement(0, 0, 96, 32, Text.of(PackSwapper.getInstance().getRegionColor()+""));
            psColorCode.setPressAction(button->{
                try{
                    PackSwapper.getInstance().setRegionColor(psColorCode.getText().charAt(0));
                    PSConfig.saveSettings();
                }catch(Exception e){
                    psColorCode.setText(PackSwapper.getInstance().getRegionColor()+"");
                }
            });
            //messages
            psConfig.setMessage(Text.of("Open Config"));
            psToggle.setMessage(Text.of("Toggle Visual"));
            psRPHelper.setMessage(Text.of("Toggle Resource Pack Helper"));
            psDebugInfo.setMessage(Text.of("Toggle Debug Info"));
            //tooltips
            psConfig.setTooltip(Text.of("Opens the detailed pack swapper config."));
            psToggle.setTooltip(Text.of("Toggles the current area and region visual."));
            psRPHelper.setTooltip(Text.of("Toggles the Resource Pack Helper's additional description."));
            psColorCode.setTooltip(Text.of("Color Code for the Pack Swapper."));
            psDebugInfo.setTooltip(Text.of("Toggles the debug chat message for when a region change is detected and a new pack is loaded."));
            //add them in
            MenuElement PSVisual = (MenuElement)PackSwapper.getInstance().getFeatureVisual();
            PSVisual.addSubElement(psConfig);
            PSVisual.addSubElement(psToggle);
            PSVisual.addSubElement(psDebugInfo);
            PSVisual.addSubElement(psRPHelper);
            PSVisual.addSubElement(psColorCode);

            //Generic Profit
            //define elements
            GUIElement gpToggle = new GUIElement(0, 0, 96, 32, button ->{
                GenericProfit.getInstance().toggle();
            });
            GUIElement gpReset = new GUIElement(0, 0, 96, 32, button ->{
                GenericProfit.getInstance().resetProfit();
            });
            GUIElement ecoMathToggle = new GUIElement(0, 0, 96, 32, button ->{
                EcoConfig.toggleMath();
            });
            TextFieldElement ecoColorCode = new TextFieldElement(0, 0, 96, 32,Text.of(EcoConfig.getColorCode()+""));
            ecoColorCode.setPressAction(button->{
                try{
                    EcoConfig.setColorCode(ecoColorCode.getText().charAt(0));
                    EcoConfig.saveSettings();
                }catch(Exception e){
                    ecoColorCode.setText(EcoConfig.getColorCode()+"");
                }
            });
            //messages
            gpToggle.setMessage(Text.of("Toggle Feature"));
            gpReset.setMessage(Text.of("Reset Profit"));
            ecoMathToggle.setMessage(Text.of("Toggle Math Helper"));
            //tooltips
            gpToggle.setTooltip(Text.of("Whether or not to show the Generic Profit Widget."));
            gpReset.setTooltip(Text.of("Resets the widget, returning profit and time to 0."));
            ecoMathToggle.setTooltip(Text.of("Whether or not to show the Math Helper when typing in chat."));
            ecoColorCode.setTooltip(Text.of("Color Code for many Economic Widgets."));
            
            //add them in
            MenuElement GPVisual = (MenuElement)GenericProfit.getInstance().getFeatureVisual();
            GPVisual.addSubElement(gpToggle);
            GPVisual.addSubElement(gpReset);
            GPVisual.addSubElement(ecoMathToggle);
            GPVisual.addSubElement(ecoColorCode);

            //Bingo
            //define elements
            GUIElement bngToggle = new GUIElement(0, 0, 96, 32, button ->{
                Bingo.getInstance().toggle();
            });
            GUIElement bngCommunity = new GUIElement(0, 0, 96, 32, button ->{
                Bingo.getInstance().toggleCommunity();
            });
            //messages
            bngToggle.setMessage(Text.of("Toggle Feature"));
            bngCommunity.setMessage(Text.of("Toggle Community Goals"));
            //tooltips
            bngToggle.setTooltip(Text.of("Whether or not to show the current Bingo Goals. Only active while on a bingo profile!"));
            bngCommunity.setTooltip(Text.of("Whether or not to show community goals in the Goal List."));
            //add them in
            MenuElement BNGVisual = (MenuElement)Bingo.getInstance().getFeatureVisual();
            BNGVisual.addSubElement(bngToggle);
            BNGVisual.addSubElement(bngCommunity);

            //Bat Firework
            //define elements
            GUIElement bfToggle = new GUIElement(0, 0, 96, 32, button ->{
                BatFirework.getInstance().toggle();
            });
            GUIElement bfReset = new GUIElement(0, 0, 96, 32, button ->{
                BatFirework.getInstance().resetProfit();
            });
            //messages
            bfToggle.setMessage(Text.of("Toggle Feature"));
            bfReset.setMessage(Text.of("Reset Profit"));
            //tooltips
            bfToggle.setTooltip(Text.of("Whether or not to show the Bat Firework Widget."));
            bfReset.setTooltip(Text.of("Resets the widget, returning profit and time to 0."));
            //add them in
            MenuElement BFVisual = (MenuElement)BatFirework.getInstance().getFeatureVisual();
            BFVisual.addSubElement(bfToggle);
            BFVisual.addSubElement(bfReset);
        }
        initialized = true;
    }

    

}
