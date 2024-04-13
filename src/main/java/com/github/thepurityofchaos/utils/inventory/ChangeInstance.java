package com.github.thepurityofchaos.utils.inventory;

import net.minecraft.text.Text;
    //https://github.com/BiscuitDevelopment/SkyblockAddons/blob/main/src/main/java/codes/biscuit/skyblockaddons/features/ItemDiff.java#L8 
    //assisted with this class. Most of it is similar, but NBTTagCompound no longer exists in 1.20.4. 
    //Additionally, I'm adding a feature to allow the player to customize the duration of the log- something which the original did not have.
public class ChangeInstance {
    
    //Duration of the item (ms)
    private static long maxLifespan = 3000;

    private Text name;

    private long currTime;

    private int count = 0;

    private boolean fromSacks;

    private static char colorCode = 'e';

    private static int distance = 8;
    
    public ChangeInstance(Text name, int count, boolean fromSacks){
        this.name = name;
        this.addToInstance(count);
        this.fromSacks = fromSacks;

    }
    public void addToInstance(int change){
        this.count +=change;
        //delete this if the net change is 0
        this.currTime = (this.count==0)?(this.currTime-maxLifespan):System.currentTimeMillis();
    }

    public long getCurrentLifespan(){
        return System.currentTimeMillis() - this.currTime;
    }

    public Text getName(){
        return name;
    }
    public int getCount(){
        return count;
    }
    public boolean isFromSacks(){
        return fromSacks;
    }
    public static void setColorCode(char newColorCode){
            colorCode = newColorCode;      
    }
    public static char getColorCode(){
        return colorCode;
    }
    public static void setDistance(int newDistance){
            distance = newDistance;
    }
    public static int getDistance(){
        return distance;
    }
    public static long getMaxLifespan(){
        return maxLifespan;
    }
    public static void setLifespan(int newLifespan){
        maxLifespan = newLifespan*1000;
    }
}
