package com.github.thepurityofchaos.utils;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Utils {
    
    public static String getStringFromBoolean(boolean b){
        return b?" [ON] ":" [OFF] ";
    }
    public static String removeLowerCase(String s){
        Pattern p = Pattern.compile("[a-z]");
        Matcher m = p.matcher(s);
        return m.replaceAll("");
    }
    public static String clearArea(String s){
        return s.replace("Area:","").replace(" ","");
    }
    public static String clearRegion(String s){
        return s.replace("ф","").replace("⏣","").replace(" ","");
    }
    public static String getColorString(char c){
        return "§"+c;
    }
    public static String removeNbtCharacters(String s){
        return s.replace("{","").replace("}","").replace("\"","").replace("[","").replace("]","").replace(",","");
    }
}
