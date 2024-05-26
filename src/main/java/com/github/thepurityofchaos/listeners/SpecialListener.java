package com.github.thepurityofchaos.listeners;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.github.thepurityofchaos.SkyblockImprovements;
import com.github.thepurityofchaos.interfaces.Listener;
import com.github.thepurityofchaos.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class SpecialListener implements Listener {
    //testing listener to figure out how to modify messages that come in and send them back out. This listener makes all my messages purple :)
    private static Map<String, Character> specialNameMap = new HashMap<>();
    //load name list
    public static void init(){
         try{
            Type nameMap = new TypeToken<Map<String,Character>>(){}.getType();
            Identifier allNames = new Identifier("sbimp","info/names.json");
            ResourceManager source = MinecraftClient.getInstance().getResourceManager();
            if(source!=null){
            Gson gson = new Gson();
            InputStream stream = source.getResource(allNames).get().getInputStream();
            BufferedReader r = new BufferedReader(new InputStreamReader(stream));
            JsonObject defaultParser = JsonParser.parseReader(r).getAsJsonObject();
            specialNameMap = (gson.fromJson(defaultParser, nameMap));
            r.close();

            }
        }catch(Exception x){
            x.printStackTrace();
        }
    }

    public static Text isMyMessage(Text message){
        if(message==null) return null;
        //if one of the gamertags
        SkyblockImprovements.push("SBI_SpecialListener");
        for(String name : specialNameMap.keySet()){
            if(message.getString().contains(name)){
                SkyblockImprovements.pop();
                return SpecialListener.recolorMessage(message, name, Style.EMPTY.withColor(Formatting.byCode(specialNameMap.get(name))));
            }
        }
        //if something else
        SkyblockImprovements.pop();
        return null;
    }
    public static Text recolorMessage(Text message, String recolorPoint, Style newStyle){
        MutableText newMessage = Text.empty();
        //recolor portions of text depending on message content. 

        //First, check the main body of the message.
        MutableText currentMessage = MutableText.of(message.getContent());
        String messageString = currentMessage.getString();
        int i = messageString.indexOf(recolorPoint);
        if(i!=-1){
            appendMessage(messageString, newMessage, currentMessage.getStyle(), i, newStyle, recolorPoint);
        }else{
            newMessage.append(currentMessage.copyContentOnly().setStyle(message.getStyle()));
        }
        //then, check the siblings.
        for(Text sibling : message.getSiblings()){
            String siblingString = sibling.getString();
            int index = siblingString.indexOf(recolorPoint);
            if(index!=-1){
                appendMessage(siblingString, newMessage, sibling.getStyle(), index, newStyle, recolorPoint);
            }else{
                newMessage.append(sibling.copy());
            }
        }
        return newMessage;
    }
    private static boolean appendMessage(String messageString, MutableText newMessage, Style currentStyle, int index, Style newStyle, String recolorPoint){
        String former = messageString.substring(0, index);
        String central = messageString.substring(index,index+recolorPoint.length());
        String latter = messageString.substring(index+recolorPoint.length());
        //if me, override text color :)
        if(messageString.contains("ThePurityOfChaos"))
            latter = latter.replace(Utils.getColorString('f'),Utils.getColorString('d'));
        
        newMessage.append(Text.literal(former).setStyle(currentStyle));
        newMessage.append(Text.literal(central).setStyle(newStyle));
        newMessage.append(Text.literal(latter).setStyle(currentStyle));
        return true;
    }

}
