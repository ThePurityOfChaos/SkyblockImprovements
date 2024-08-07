package com.github.thepurityofchaos;

import java.nio.file.Path;

import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.thepurityofchaos.features.economic.ChocolateFactory;
import com.github.thepurityofchaos.features.itempickuplog.ItemPickupLog;
import com.github.thepurityofchaos.features.packswapper.PackSwapper;
import com.github.thepurityofchaos.features.retexturer.RTRender;
import com.github.thepurityofchaos.listeners.ScreenListener;
import com.github.thepurityofchaos.storage.config.Config;
import com.github.thepurityofchaos.utils.processors.InventoryProcessor;
import com.github.thepurityofchaos.utils.processors.SpecialProcessor;
import com.github.thepurityofchaos.utils.screen.GeneratorScreen;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.profiler.Profiler;
/**
 * The main class of the mod, through which the rest of the system functions.
 * 
 * <p> {@link #onInitializeClient()}: Initializes all features, then initializes the Config to change those features' settings.
 * 
 * <p> DEBUG FEATURES:
 * 
 * <p> {@link #EXPERIMENTAL_TOGGLE_DEBUG_FEATURES()}: Toggles debug mode.
 * 
 * <p> {@link #DEBUG()}: Returns whether debug mode is on or off.
 * 
 * <p> {@link #push(String)}: Pushes information to the game profiler.
 * 
 * <p> {@link #pop()}: Pops information from the game profiler. MUST be called after {@link #push(String)}.
 * 
 * 
 */
public class SkyblockImprovements implements ClientModInitializer {
	private static final Logger LOGGER = LoggerFactory.getLogger(SkyblockImprovements.class);
	public static final Path FILE_LOCATION = FabricLoader.getInstance().getConfigDir().resolve("sbimp");
	public static final Path RESOURCE_PACK_LOCATION = FabricLoader.getInstance().getGameDir().resolve("resourcepacks");
	public static final String VERSION = FabricLoader.getInstance().getModContainer("sbimp")
	.map(modInfo -> modInfo.getMetadata().getVersion().getFriendlyString()).orElse("null");
	private static Profiler GAME_PROFILER = null;
	private static boolean DEBUG = false;
	private static KeyBinding openItemGen;
	
	@Override
	public void onInitializeClient() {
		LOGGER.info("Entered SkyblockImprovements");
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.

		ItemPickupLog.getInstance().init();
		PackSwapper.getInstance().init();
		ScreenListener.init();
		ChocolateFactory.getInstance().init();

		//this entrypoint is necessary for initializing features that require the player to be in a world
		ClientPlayConnectionEvents.JOIN.register((handler, sender, client)->{
			SpecialProcessor.init();
			RTRender.setKnownIdentifiers();
			GAME_PROFILER = MinecraftClient.getInstance().getProfiler();
		});
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client)->{
			Config.saveSettings();
		});

		//entrypoint for keybindings
		 openItemGen = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.sbimp.openItemGen", // Translation key
            InputUtil.Type.KEYSYM, // Key type
            GLFW.GLFW_KEY_RIGHT_CONTROL, // Default key
            "category.sbimp.general" // Category
        ));

		//call this last, since it changes the settings from defaults.
		Config.init();
	}
	public static boolean onScreenKeyPressed(HandledScreen<?> screen, int keyCode, int scanCode, int modifiers) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (openItemGen.matchesKey(keyCode, scanCode)) {
			GeneratorScreen.parseItemToGenerator(InventoryProcessor.getHoveredItem(client));
			GeneratorScreen newScreen = GeneratorScreen.getInstance();
			newScreen.init(screen);
			client.send(() -> client.setScreen(newScreen));
			return true;
		}
		return false;
	}

	/*
	 * Pushes debug information to the client's Profiler for debug purposes.
	 */
	public static void push(String s){
		if(!DEBUG||GAME_PROFILER==null||!MinecraftClient.getInstance().getDebugHud().shouldShowRenderingChart()) return;
		GAME_PROFILER.push(s);
	}
	public static void pop(){
		if(!DEBUG||GAME_PROFILER==null||!MinecraftClient.getInstance().getDebugHud().shouldShowRenderingChart()) return;
		GAME_PROFILER.pop();
	}
	public static void EXPERIMENTAL_TOGGLE_DEBUG_FEATURES(){
		DEBUG = !DEBUG;
	}
	public static boolean DEBUG(){
		return DEBUG;
	}
}