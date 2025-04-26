package com.github.thepurityofchaos.abstract_interfaces;
/**
 * Extending this class means that the class is toggleable; 
 */
public abstract class Toggleable {
    protected boolean isEnabled = false;
    public boolean isEnabled(){return isEnabled;}
    public void toggle(){
        isEnabled = !isEnabled;
    }
    public void enable(){
        isEnabled = true;
    }
    public void disable(){
        isEnabled = false;
    }
}
