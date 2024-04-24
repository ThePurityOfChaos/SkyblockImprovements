package com.github.thepurityofchaos.utils;

public class Utils {
    
    public static String getStringFromBoolean(boolean b){
        return b?" [ON] ":" [OFF] ";
    }
    public static String removeLowerCase(String s){
        return s.replaceAll("[a-z]","");
    }
    public static String removeText(String s){
        return s.replaceAll("[A-Za-z]","");
    }
    public static String clearArea(String s){
        return s.replace("Area:","").replace(" ","");
    }
    public static String clearRegion(String s){
        return s.replaceAll("[ф⏣ ]","");
    }
    public static String getColorString(char c){
        return "§"+c;
    }
    public static String removeCommas(String s){
        return s.replace(",","");
    }
    public static String addCommas(String s){
        try{
            Integer i = Integer.parseInt(s);
            return String.format("%,d",i);
        }catch(Exception e){
                return "";
        }  
    }
    public static String stripSpecial(String s){
        return s.replaceAll("[⸕✧☘✎❈❤❂❁☠α]","");
    }
}
