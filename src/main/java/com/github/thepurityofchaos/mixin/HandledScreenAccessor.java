package com.github.thepurityofchaos.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;


import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;

/**
 * MIXIN: Injects into ChatScreen to get the current ChatField.
 */
@Mixin(HandledScreen.class)
public interface HandledScreenAccessor {
    
    @Accessor("focusedSlot")
    Slot getFocusedSlot();
}