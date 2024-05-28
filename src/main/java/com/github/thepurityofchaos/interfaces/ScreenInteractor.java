package com.github.thepurityofchaos.interfaces;

import net.minecraft.client.gui.screen.Screen;
/**
 * Implementing this Interface means that a class will be displaying something on a Screen.
 * 
 * <p> {@link #interact(Screen screen)}: Interacts in some way with the inputted Screen, most commonly but not limited to displaying something.
 */
public interface ScreenInteractor {
    public static void interact(Screen screen){}
}
