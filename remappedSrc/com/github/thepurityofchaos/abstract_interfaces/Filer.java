package com.github.thepurityofchaos.abstract_interfaces;
/**
 * Implementing this Interface means that a class will be performing File I/O, usually but not limited to a Config.
 * 
 * <p> {@link #init init()}: Gets information from a JSON file and translates it into usable information.
 * 
 * <p> {@link #createFile createFile()}: Creates the file that is to be associated with {@link #init init()} and {@link #saveSettings saveSettings()}.
 * 
 * <p> {@link #saveSettings saveSettings()}: Saves relevant information to the JSON file for persistent storage.
 * 
 */
public interface Filer {
    public static void init(){}
    public static void createFile(){}
    public static void saveSettings(){}
}
