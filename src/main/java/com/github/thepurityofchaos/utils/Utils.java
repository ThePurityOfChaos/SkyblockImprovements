package com.github.thepurityofchaos.utils;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        }catch(Exception x){
            try{
                Double d = Double.parseDouble(s);
                return String.format("%,.3f",d);
            }catch(Exception e){
                return "";
            }
        }
    }
    //the replacement is ridiculous, this is a horrendous implementation but I'm tired.
    public static boolean containsRegex(String s, String r){
        String m = s.replaceAll(r,"☠CONTAINS☠");
        return m.contains("☠CONTAINS☠");
    }
    public static String stripSpecial(String s){
        return s.replaceAll("[⸕✧☘✎❈❤❂❁☠α]","");
    }
    public static String normalizeDouble(double d){
        if((d==((int)d))){
            return Integer.toString((int)d);
        }
        return Double.toString(d);    
    }
    public static String asciify(String s){
        //https://stackoverflow.com/questions/8519669/how-can-non-ascii-characters-be-removed-from-a-string
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        return s.replaceAll("[^\\x00-\\x7F]", "");
    }
    public static <K,V> List<List<Map.Entry<K,V>>> allPairs(Map<K,V> map, boolean truePairs){
        List<Map.Entry<K,V>> entries = new ArrayList<>(map.entrySet());
        List<List<Map.Entry<K,V>>> result = new ArrayList<>();
        for(int i=0; i<entries.size(); i++){
            //0 for true pairs
            for(int j=truePairs?0:i+1; j<entries.size(); j++){
                List<Map.Entry<K,V>> pair = new ArrayList<>();
                pair.add(entries.get(i));
                pair.add(entries.get(j));
                result.add(pair);
            }
        }
        return result;
    }
}
