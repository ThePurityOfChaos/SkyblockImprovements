package com.github.thepurityofchaos.utils.processors;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.thepurityofchaos.SkyblockImprovements;
import com.github.thepurityofchaos.abstract_interfaces.MessageProcessor;
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

public class SpecialProcessor implements MessageProcessor {
    //testing listener to figure out how to modify messages that come in and send them back out. This listener makes all my messages purple :)
    private static Map<String, Character> specialNameMap = new HashMap<>();
    private static List<String> cognitoHazardList = new ArrayList<>();

    static{
        cognitoHazardList.add("you lost the game");
    }
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
        Text result = message;
        SkyblockImprovements.push("SBI_SpecialListener");
        for(String name : specialNameMap.keySet()){
            if(message.getString().contains(name)){
                result = SpecialProcessor.recolorMessage(result, name, Style.EMPTY.withColor(Formatting.byCode(specialNameMap.get(name))));
            }
        }
        //if cognitohazardous
        for(String cognitohazard : cognitoHazardList){
            if(message.getString().contains(cognitohazard)){
                result = SpecialProcessor.removeCognitohazards(result, cognitohazard);
            }
        }
        SkyblockImprovements.pop();
        if(result!=message) return result;
        return null;
        
    }
    private static MutableText removeCognitohazards(Text message, String cognitohazard) {
        MutableText newMessage = Text.empty();
        //recolor portions of text depending on message content. 

        //First, check the main body of the message.
        MutableText currentMessage = MutableText.of(message.getContent());
        String messageString = currentMessage.getString();
        int i = messageString.indexOf(cognitohazard);
        if(i!=-1){
            appendMessage(messageString, newMessage, currentMessage.getStyle(), i, cognitohazard);
        }else{
            newMessage.append(currentMessage.copyContentOnly().setStyle(message.getStyle()));
        }
        //then, check the siblings.
        for(Text sibling : message.getSiblings()){
            String siblingString = sibling.getString();
            int index = siblingString.indexOf(cognitohazard);
            if(index!=-1){
                appendMessage(siblingString, newMessage, sibling.getStyle(), index, cognitohazard);
            }else{
                newMessage.append(sibling.copy());
            }
        }
        return newMessage;
    }

    public static MutableText recolorMessage(Text message, String recolorPoint, Style newStyle){
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
        
        newMessage.append(Text.literal(former).setStyle(currentStyle));
        newMessage.append(Text.literal(central).setStyle(newStyle));
        //if me, override text color :)
        if(messageString.contains("ThePurityOfChaos:"))
            newMessage.append(Text.literal(Utils.getColorString('d')+latter).setStyle(Style.EMPTY.withColor(Formatting.LIGHT_PURPLE)));
        else{
            newMessage.append(Text.literal(latter).setStyle(currentStyle)); 
        }
        return true;
    }
    private static boolean appendMessage(String messageString, MutableText newMessage, Style currentStyle, int index, String cognitohazard){
        String former = messageString.substring(0, index);
        String central = messageString.substring(index,index+cognitohazard.length());
        String latter = messageString.substring(index+cognitohazard.length());
        central = central.replace(cognitohazard,"[REDACTED]");
        newMessage.append(Text.literal(former).setStyle(currentStyle));
        newMessage.append(Text.literal(central).setStyle(Style.EMPTY.withColor(Formatting.DARK_RED)));
        //if me, override text color :)
        if(messageString.contains("ThePurityOfChaos:"))
            newMessage.append(Text.literal(Utils.getColorString('d')+latter).setStyle(Style.EMPTY.withColor(Formatting.LIGHT_PURPLE)));
        else{
            newMessage.append(Text.literal(latter).setStyle(currentStyle)); 
        }
        
        return true;
    }

}
