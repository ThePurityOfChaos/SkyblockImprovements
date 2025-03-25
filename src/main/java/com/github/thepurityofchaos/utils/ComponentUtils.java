package com.github.thepurityofchaos.utils;


import java.util.List;
import java.util.UUID;

import com.github.thepurityofchaos.utils.screen.GeneratorScreen;
import com.google.gson.Gson;
import com.mojang.authlib.properties.Property;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;

public class ComponentUtils {

    public static final Gson GSON = new Gson();

    public static Text getNamefromItemStack(ItemStack stack){
        Text name = stack.getCustomName();
        return name==null?Text.of(""):name;
    }
    public static List<Text> getLorefromItemStack(ItemStack stack){
        if(stack== null) return null;
        if(stack.get(DataComponentTypes.LORE)==null) return null;
        return stack.get(DataComponentTypes.LORE).lines();
    }
    public static String getTextureFromSkull(ItemStack stack){
        ProfileComponent data = stack.get(DataComponentTypes.PROFILE);
        if(data==null) return "";
        try{
        Property p = data.properties().get("textures").iterator().next();
        return p.value();
        }catch(Exception e){
        return "";
        }
    }
    public static UUID getUUIDFromSkull(ItemStack stack){
        ProfileComponent data = stack.get(DataComponentTypes.PROFILE);
        if(data==null) return null;
        return data.gameProfile().getId();
    }
    
    public static String getStringFromLore(LoreComponent lore, boolean parseLastElement) {
        if(lore==null) return "No lore found!";
        // Get and process the lore
        List<Text> loreArray = lore.lines();
        StringBuilder loreBuilder = new StringBuilder();
        for (int i= 0; i<loreArray.size()-2; i++) {
            loreBuilder.append(convertTextToString(loreArray.get(i))).append("\\n");
        }
        //remove extraneous \n
        loreBuilder.append(convertTextToString(loreArray.get(loreArray.size()-2)));
        if(parseLastElement) loreBuilder.append(convertTextToString(loreArray.get(loreArray.size()-1)));

        return loreBuilder.toString();
    }
    public static Pair<Integer,String> getRarityAndTypeFromLore(LoreComponent lore) {
        List<Text> lines = lore.lines();
        int rarity = 0;
        String lastLine = (lines.get(lines.size()-1)).getString();
        for(int i=0; i<GeneratorScreen.rarities.length; i++){
            if(lastLine.contains(GeneratorScreen.rarities[i])){
                rarity = i;
            }
        }
        String loreResult = convertTextToString(lines.get(lines.size()-1));
        loreResult = loreResult.replace(GeneratorScreen.rarities[rarity],"");
        if(loreResult.contains("&k")){
            loreResult =loreResult.replace("&k","");
            
            rarity+=GeneratorScreen.rarities.length;
        }
        loreResult = loreResult.replaceAll("&[A-F0-9a-z]","");
        
        return new Pair<Integer,String>(rarity,loreResult);
    }

    public static String convertTextToString(Text text) {
        StringBuilder sb = new StringBuilder();
        processText(sb, text);
        return sb.toString();
    }
    private static void processText(StringBuilder sb, Text text) {
        Style style = text.getStyle();

        if (style.getColor() != null) {
            sb.append(getColorCode(style.getColor().getName()));
        }
        if (style.isBold()) {
            sb.append("&l");
        }
        if (style.isItalic()) {
            sb.append("&o");
        }
        if (style.isUnderlined()) {
            sb.append("&n");
        }
        if (style.isStrikethrough()) {
            sb.append("&m");
        }
        if (style.isObfuscated()) {
            sb.append("&k");
        }

        sb.append(text.copyContentOnly().getString());

        for (Text sibling : text.getSiblings()) {
            processText(sb, sibling);
        }
    }

    private static String getColorCode(String color) {
        switch (color) {
            case "black": return "&0";
            case "dark_blue": return "&1";
            case "dark_green": return "&2";
            case "dark_aqua": return "&3";
            case "dark_red": return "&4";
            case "dark_purple": return "&5";
            case "gold": return "&6";
            case "gray": return "&7";
            case "dark_gray": return "&8";
            case "blue": return "&9";
            case "green": return "&a";
            case "aqua": return "&b";
            case "red": return "&c";
            case "light_purple": return "&d";
            case "yellow": return "&e";
            case "white": return "&f";
            default: return "";
        }
    }
}
