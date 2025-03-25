package com.github.thepurityofchaos.features.miscellaneous;

import com.github.thepurityofchaos.abstract_interfaces.ScreenInteractor;
import com.github.thepurityofchaos.storage.config.ConfigScreen;
import com.github.thepurityofchaos.utils.Utils;
import com.github.thepurityofchaos.utils.gui.GUIElement;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class SBIButton implements ScreenInteractor  {
    private static boolean shown = false;
    public static void interact(Screen screen){
        ScreenEvents.beforeRender(screen).register((currentScreen, drawContext, mouseX, mouseY, delta) -> {
            if(!shown){
            GUIElement ConfigButton = new GUIElement(currentScreen.width/2-40,currentScreen.height-32,80,32,button ->{
                ConfigScreen configScreen = new ConfigScreen();
                configScreen.init(currentScreen);
                MinecraftClient client = MinecraftClient.getInstance();
                client.setScreen(configScreen);
            });
            ConfigButton.setMessage(Text.of(Utils.getColorString('f')+"Open Config"));
            ConfigButton.setTooltip(Text.of(Utils.getColorString('f')+"Open the SkyblockImprovements Config"));
            Screens.getButtons(currentScreen).add(ConfigButton);
            shown = true;
            }
        });
        ScreenEvents.remove(screen).register((current)->{
            shown = false;
        });
        
    }
}
