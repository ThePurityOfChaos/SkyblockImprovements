package com.github.thepurityofchaos.abstract_interfaces;

import com.github.thepurityofchaos.utils.Utils;
import com.github.thepurityofchaos.utils.gui.GUIElement;

import net.minecraft.text.Text;
/**
 * 
 * Implementing this Interface means that the class is a feature with a visual element, and can also be toggled.
 * 
 * <p> {@link #xVisual}: The GUIElement associated with this Feature.
 * 
 * <p> {@link #isEnabled}: Whether or not this Feature is enabled.
 * 
 * <p> {@link #getFeatureVisual}: Returns visual component.
 */
public abstract class Feature extends Toggleable {
    protected GUIElement visual = null;
    public GUIElement getFeatureVisual(){return visual;}
    @Override
    public void toggle() {
        super.toggle();
        updateFeatureVisuals();
    }
    private void updateFeatureVisuals(){
        visual.setTooltip(Text.of(Utils.getStringFromBoolean(isEnabled)));
    }
    public abstract void init();

}
