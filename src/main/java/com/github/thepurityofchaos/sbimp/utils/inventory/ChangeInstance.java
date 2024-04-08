package com.github.thepurityofchaos.sbimp.utils.inventory;

import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.NbtCompound;
    //https://github.com/BiscuitDevelopment/SkyblockAddons/blob/main/src/main/java/codes/biscuit/skyblockaddons/features/ItemDiff.java#L8 
    //assisted with this class. Most of it is similar, but NBTTagCompound no longer exists in 1.20.4. 
    //Additionally, I'm adding a feature to allow the player to customize the duration of the log- something which the original did not have.
public class ChangeInstance {
    
    //Duration of the item (ms)
    public static long maxLifespan = 3000;

    private String name;

    private NbtCompound data;

    private long currTime;

    private int count = 0;

    public static void setLifespan(int newLifespan){
        maxLifespan = newLifespan;
    }
    
    public ChangeInstance(String name, int count, @Nullable NbtCompound data){
        this.name = name;
        this.data = data;
        this.addToInstance(count);

    }
    public void addToInstance(int change){
        this.count +=change;
        //delete this if the net change is 0
        this.currTime = (this.count==0)?(this.currTime-maxLifespan):System.currentTimeMillis();
    }

    public long getCurrentLifespan(){
        return System.currentTimeMillis() - this.currTime;
    }

    public String getName(){
        return name;
    }
    public NbtCompound getData(){
        return data;
    }
    public int getCount(){
        return count;
    }





}
