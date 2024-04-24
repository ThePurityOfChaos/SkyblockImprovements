package com.github.thepurityofchaos.utils.processors;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;

public class NbtProcessor {
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
}