package com.github.thepurityofchaos.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.github.thepurityofchaos.utils.screen.GeneratorScreen;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;

public class NbtUtils {

    public static final Gson GSON = new Gson();

    public static String removeNbtCharacters(String s){
        return s.replace("{","").replace("}","").replace("\"","").replace("[","").replace("]","").replace(",","");
    }

    public static Text getNamefromItemStack(ItemStack stack){
        NbtCompound data = stack.getNbt();
        if(data!=null && data.contains("display",NbtElement.COMPOUND_TYPE)){
            NbtCompound displayData = data.getCompound("display");
            if(displayData.contains("Name")){
                return Text.Serialization.fromJson(displayData.getString("Name"));
            }
        }
        return Text.of("");
    }
    public static List<Text> getLorefromItemStack(ItemStack stack){
        NbtCompound data = stack.getNbt();
        if(data!=null && data.contains("display",NbtElement.COMPOUND_TYPE)){
            NbtCompound displayData = data.getCompound("display");
            if(displayData.contains("Lore")){
                List<Text> result = new ArrayList<>();
                NbtList lore = displayData.getList("Lore", NbtElement.STRING_TYPE);
                for(int i=0; i<lore.size(); i++){
                    result.add(Text.Serialization.fromJson(lore.getString(i)));
                }
                return result;
            }
        }
        return null;
    }
    public static String getTextureFromSkull(ItemStack stack){
        if(!stack.hasNbt()){
            return null;
        }
        NbtCompound data = stack.getNbt();
        if(!data.contains("SkullOwner")){
            return null;
        }
        NbtCompound skullData = (NbtCompound) data.get("SkullOwner");
        if(!skullData.contains("Properties")){
            return null;
        }
        NbtCompound properties = (NbtCompound) skullData.get("Properties");
        if(!properties.contains("textures")){
            return null;
        }
        NbtList textures = (NbtList) properties.get("textures");
        NbtCompound value = (NbtCompound) textures.get(0);
        return value.getString("Value");
    }
    public static UUID getUUIDFromSkull(ItemStack stack){
        if(!stack.hasNbt()){
            return null;
        }
        NbtCompound data = stack.getNbt();
        if(!data.contains("SkullOwner")){
            return null;
        }
        NbtCompound skullData = (NbtCompound) data.get("SkullOwner");
        if(skullData.containsUuid("Id"))
            return skullData.getUuid("Id");
        return null;
    }
    
    public static String getStringFromLore(String nbtJson, boolean parseLastElement) {
        JsonObject nbt = JsonParser.parseString(nbtJson).getAsJsonObject();
        JsonObject display = getDisplay(nbt);
        if (display == null) {
            return "&4No display data found.";
        }

        // Get and process the lore
        JsonArray loreArray = display.getAsJsonArray("Lore");
        StringBuilder loreBuilder = new StringBuilder();
        for (int i= 0; i<loreArray.size()-2; i++) {
            loreBuilder.append(processJsonText(loreArray.get(i).getAsString())).append("\\n");
        }
        //remove extraneous \n
        loreBuilder.append(processJsonText(loreArray.get(loreArray.size()-2).getAsString()));
        if(parseLastElement) loreBuilder.append(processJsonText(loreArray.get(loreArray.size()-1).getAsString()));

        return loreBuilder.toString();
    }
    public static Pair<Integer,String> getRarityAndTypeFromLore(String nbtJson) {
        JsonObject nbt = JsonParser.parseString(nbtJson).getAsJsonObject();
        JsonObject display = getDisplay(nbt);
        if (display == null) {
            return new Pair<>(0,"");
        }

        // Get and process the lore
        JsonArray loreArray = display.getAsJsonArray("Lore");
        int rarity = 0;
        String lore = (loreArray.get(loreArray.size()-1)).getAsString();
        for(int i=0; i<GeneratorScreen.rarities.length; i++){
            if(lore.contains(GeneratorScreen.rarities[i])){
                rarity = i;
            }
        }
        String loreResult = processJsonText(lore,true);
        loreResult = loreResult.replace(GeneratorScreen.rarities[rarity],"");
        if(loreResult.contains("&k")){
            loreResult =loreResult.replace("&k","");
            rarity+=GeneratorScreen.rarities.length;
        }
        
        return new Pair<Integer,String>(rarity,loreResult);
    }
    public static String getStringFromName(String nbtJson){
        JsonObject nbt = JsonParser.parseString(nbtJson).getAsJsonObject();
        JsonObject display = getDisplay(nbt);
        if (display == null) {
            return "";
        }
        // Get and process the name
        return processJsonText(display.get("Name").getAsString());

    }
    private static JsonObject getDisplay(JsonObject nbt){
        JsonObject display = null;
        if (nbt.has("tag")) {
            JsonObject tag = nbt.getAsJsonObject("tag");
            if (tag.has("display")) {
                display = tag.getAsJsonObject("display");
            }
        } else if (nbt.has("display")) {
            // Directly check for "display" in the main object if "tag" is not present
            display = nbt.getAsJsonObject("display");
        }
        return display;
    }

    private static String processJsonText(String jsonText, boolean parseSpecial) {
        JsonObject jsonObject = JsonParser.parseString(jsonText).getAsJsonObject();
        return parseJsonElement(jsonObject, parseSpecial);
    }
    private static String processJsonText(String jsonText) {
        JsonObject jsonObject = JsonParser.parseString(jsonText).getAsJsonObject();
        return parseJsonElement(jsonObject, false);
    }

    private static String parseJsonElement(JsonObject jsonObject, boolean parseSpecial) {
        StringBuilder result = new StringBuilder();
        if (jsonObject.has("color")&& !parseSpecial) {
            result.append(getColorCode(jsonObject.get("color").getAsString()));
        }
        if (jsonObject.has("bold") && jsonObject.get("bold").getAsBoolean() && !parseSpecial) {
            result.append("&l");
        }
        if (jsonObject.has("italic") && jsonObject.get("italic").getAsBoolean() && !parseSpecial) {
            result.append("&o");
        }
        if (jsonObject.has("obfuscated") && jsonObject.get("obfuscated").getAsBoolean()) {
            result.append("&k");
        }
        if (jsonObject.has("text")) {
            result.append(jsonObject.get("text").getAsString());
        }
        if (jsonObject.has("extra")) {
            JsonArray extraArray = jsonObject.getAsJsonArray("extra");
            for (JsonElement extraElement : extraArray) {
                result.append(parseJsonElement(extraElement.getAsJsonObject(), parseSpecial));
            }
        }
        return result.toString();
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
