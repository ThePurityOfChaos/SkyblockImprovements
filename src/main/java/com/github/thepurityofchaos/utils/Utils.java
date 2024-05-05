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
    //the replacement is ridiculous, this is a horrendous implementation but I'm tired.
    public static boolean containsRegex(String s, String r){
        String m = s.replaceAll(r,"☠CONTAINS☠");
        return m.contains("☠CONTAINS☠");
    }
    public static String stripSpecial(String s){
        return s.replaceAll("[⸕✧☘✎❈❤❂❁☠α]","");
    }
    // from ColorHelper, 
    //a, r, g, and b should be 255 or less, otherwise indeterminate behavior may occur.
    public static int rGBAToInt(int r, int g, int b, int a){
        // << is left shift bits, so << 24 shifts left 24 and removes things out of range
        // | is bitwise or, which says if either this is 1 or the other is 1 accept it, this essentially concatenates an int onto the end if said int is less than 256.
        return a << 24 | r << 16 | g << 8 | b;
    }
    //modified from ColorHelper, it's just some bit shifts
    public static int[] intToRGBA(int n){
        int a,r,g,b;
        //>> is right shift bits, so >> 24 shifts it right 24 and removes everything that goes out of range
        //& 0xFF is bitwise and, which masks the bits by saying 'only if this bit is 1 and the bits in 0xFF are 1', 
        //0xFF is 11111111 so only the rightmost 8 bits are correct and will be kept.
        a = n >> 24 & 0xFF;
        r = n >> 16 & 0xFF;
        g = n >> 8 & 0xFF;
        b = n & 0xFF;
        return new int[]{r,g,b,a};
    }
    public static String normalizeDouble(double d){
        if((d==((int)d))){
            return Integer.toString((int)d);
        }
        return Double.toString(d);
        
    }
}
