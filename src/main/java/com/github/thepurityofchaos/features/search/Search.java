package com.github.thepurityofchaos.features.search;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.thepurityofchaos.abstract_interfaces.Feature;
import com.github.thepurityofchaos.mixin.HandledScreenAccessor;
import com.github.thepurityofchaos.utils.ComponentUtils;
import com.github.thepurityofchaos.utils.Utils;
import com.github.thepurityofchaos.utils.gui.TextFieldElement;
import com.github.thepurityofchaos.utils.math.ColorUtils;

public class Search extends Feature {
    private List<Pair<Slot,Character>> highlightedItems = new ArrayList<>();
    private String currentQuery = "";
    private static Search instance = new Search();
    private int refreshRate = 20;
    private int curr = 0;


    public void searchInventory(String query, List<Slot> inventory){
        //
        if(query.equals("")) return;
        String[] terms = query.split("\\|");
        for(int slot = 0; slot<inventory.size(); slot++){
            ItemStack item = inventory.get(slot).getStack();
            if(item==null) continue;

            for(String term : terms){
                String color = getColor(term);
                String cleanTerm = stripColor(term).toLowerCase();

                if(cleanTerm.startsWith("lore:")){
                    String loreQuery = cleanTerm.substring(5);
                    if(contains(item,loreQuery)){
                        highlight(inventory.get(slot),color);
                    }
                }else{
                    if(item.getName().getString().toLowerCase().contains(cleanTerm)){
                        highlight(inventory.get(slot),color);
                    }
                }
            }
        }
    }

    private String getColor(String term){
        Matcher matcher = Pattern.compile("&[0-9a-fA-F]").matcher(term);
        if(matcher.find()){
            return matcher.group();
        }
        return "&a";
    }

    private String stripColor(String term){
        return term.replaceAll("&[0-9a-fA-F]","");
    }

    private boolean contains(ItemStack item, String query){
        List<Text> lore = ComponentUtils.getLoreFromItemStack(item);
        if(lore==null) return false;
        for(Text text: lore){
            if(text.getString().toLowerCase().contains(query)){
                return true;
            }
        }
        return false;
    }
    private void highlight(Slot slot, String colorCode){
        if(colorCode.length()<2) return;
        char color = colorCode.charAt(1);
        highlightedItems.add(new Pair<>(slot,color));
    }

    @Override
    public void init() {
        visual = new TextFieldElement(480, 128, 128, 16, Text.of(""));
        visual.setTooltip(Text.of(Utils.getColorString('a')+"Search your inventory.\nUse '|' to separate queries.\nUse 'LORE:' to search lore."));
        ((TextFieldElement)visual).setMessage(Text.of(Utils.getColorString('7')+"Search..."));
    }
    public static Search getInstance(){
        return instance;
    }
    public void interact(Screen screen){
        Screens.getButtons(screen).add(Search.getInstance().getFeatureVisual());
        Search.getInstance().getFeatureVisual().setPosition(screen.width/2-64, screen.height/4);
        ScreenEvents.afterRender(screen).register((currentScreen, drawContext, mouseX, mouseY, delta) -> {
            String newQuery = ((TextFieldElement)Search.getInstance().getFeatureVisual()).getText();
            if(Search.getInstance().getFeatureVisual().isFocused()){screen.setFocused(Search.getInstance().getFeatureVisual());}else{screen.setFocused(null);}
            //refresh when a change occurs OR every second
            if(!Objects.equals(currentQuery, newQuery) || curr>=refreshRate){
                highlightedItems.clear();
                curr=0;
                searchInventory(newQuery, ((GenericContainerScreen)screen).getScreenHandler().slots);
            }
            curr++;
            for(Pair<Slot,Character> item : highlightedItems){
                int x = item.getLeft().x + ((HandledScreenAccessor)screen).getX();
                int y = item.getLeft().y + ((HandledScreenAccessor)screen).getY();
                drawContext.fill(x, y, x + 16, y + 16, 1000, ColorUtils.getColorFromCode(item.getRight()));    
            }
            currentQuery = newQuery;
        });
    }
    
}
