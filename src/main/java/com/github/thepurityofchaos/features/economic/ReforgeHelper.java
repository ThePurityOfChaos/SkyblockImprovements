package com.github.thepurityofchaos.features.economic;

import java.util.List;

import com.github.thepurityofchaos.utils.NbtUtils;
import com.github.thepurityofchaos.utils.Utils;

import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class ReforgeHelper {
    private static Text currentReforge = Text.literal("");
    private static char colorCode = 'e';
    public static void processList(List<ItemStack> inventory){
        for(ItemStack item : inventory){
            getDataFromItemStack(item);
        }
    }
    private static void getDataFromItemStack(ItemStack item){
        Text name = NbtUtils.getNamefromItemStack(item);
        List<Text> lore = NbtUtils.getLorefromItemStack(item);
        if(lore==null)
            return;
        if(!name.getString().contains("Reforge")&&!name.getString().contains("Close")){
            currentReforge = Text.of(Utils.getColorString(colorCode)+name.getString().split(" ")[0]);
        }
    }
    public static Text getReforge(){
        return currentReforge;
    }
    public static void setReforge(Text newReforge){
        currentReforge = newReforge;
    }
    public static void setColorCode(char c){
        colorCode = c;
    }
}
