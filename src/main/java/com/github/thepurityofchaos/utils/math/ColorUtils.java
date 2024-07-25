package com.github.thepurityofchaos.utils.math;

import java.util.List;

import com.github.thepurityofchaos.utils.Utils;

public class ColorUtils {
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
    public static int getAlpha(int n){
        return n >> 24 & 0xFF;
    }
    public static int getRed(int n){
        return n >> 16 & 0xFF;
    }
    public static int getGreen(int n){
        return n >> 8 & 0xFF;
    }
    public static int getBlue(int n){
        return n & 0xFF;
    }
    //min max clamping
    public static int clamp(int n, int min, int max){
        return Math.min(Math.max(n,min),max);
    }
    public static double norm(List<Double> colors){
        double sum = 0.0;
            for( double color : colors){
                sum+=color*color;
            }
        return Math.sqrt(sum);
    }
    public static double norm(List<Double> colors1, List<Double> colors2){
        if(colors1.size()!=colors2.size()) return -1.0;
        double sum = 0.0;
        for(int i=0; i<colors1.size(); i++){
            double color = colors1.get(i)-colors2.get(i);
            sum+=color*color;
        }
        return Math.sqrt(sum);
    }
    public static int rGBAToInt(String r, String g, String b, int a) {
        return rGBAToInt(Integer.parseInt(Utils.removeText(r)), Integer.parseInt(Utils.removeText(g)), Integer.parseInt(Utils.removeText(b)), a);
    }
}
