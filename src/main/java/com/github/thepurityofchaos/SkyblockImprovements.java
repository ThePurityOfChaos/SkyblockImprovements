package com.github.thepurityofchaos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.thepurityofchaos.config.Config;
import com.github.thepurityofchaos.features.itempickuplog.ItemPickupLog;
import com.github.thepurityofchaos.features.packswapper.PackSwapper;

import net.fabricmc.api.ClientModInitializer;
/*
 * The main class of the mod, through which the rest of the system functions.
 */
public class SkyblockImprovements implements ClientModInitializer {
	private static final Logger LOGGER = LoggerFactory.getLogger(SkyblockImprovements.class);
	@Override
	public void onInitializeClient() {
		LOGGER.info("Entered SkyblockImprovements");
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.

		
		ItemPickupLog.init();
		
		PackSwapper.init();
		//call this last, since it changes the settings from defaults.
		Config.init();
	}
	
}