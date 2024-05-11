package com.github.thepurityofchaos;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.thepurityofchaos.config.Config;
import com.github.thepurityofchaos.features.economic.ChocolateFactory;
import com.github.thepurityofchaos.features.itempickuplog.ItemPickupLog;
import com.github.thepurityofchaos.features.packswapper.PackSwapper;
import com.github.thepurityofchaos.listeners.ScreenListener;
import com.github.thepurityofchaos.listeners.SpecialListener;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
/*
 * The main class of the mod, through which the rest of the system functions.
 */
public class SkyblockImprovements implements ClientModInitializer {
	private static final Logger LOGGER = LoggerFactory.getLogger(SkyblockImprovements.class);
	public static final Path FILE_LOCATION = FabricLoader.getInstance().getConfigDir().resolve("sbimp");
	public static final Path RESOURCE_PACK_LOCATION = FabricLoader.getInstance().getGameDir().resolve("resourcepacks");
	public static final String VERSION = FabricLoader.getInstance().getModContainer("sbimp")
	.map(modInfo -> modInfo.getMetadata().getVersion().getFriendlyString()).orElse("null");
	
	@Override
	public void onInitializeClient() {
		LOGGER.info("Entered SkyblockImprovements");
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.

		ItemPickupLog.init();
		PackSwapper.init();
		ScreenListener.init();
		ChocolateFactory.init();

		//this entrypoint is necessary for initializing features that require the player to be in a world
		ClientPlayConnectionEvents.JOIN.register((handler, sender, client)->{
			SpecialListener.init();
		});

		//call this last, since it changes the settings from defaults.
		Config.init();
	}
	
}