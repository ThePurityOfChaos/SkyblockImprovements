package com.github.thepurityofchaos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.thepurityofchaos.features.itempickuplog.ItemPickupLog;

import net.fabricmc.api.ClientModInitializer;

public class SkyblockImprovements implements ClientModInitializer {
	private static final Logger LOGGER = LoggerFactory.getLogger(SkyblockImprovements.class);
	private static boolean logEnabled = true;
	@Override
	public void onInitializeClient() {
		
		LOGGER.info("Entered SkyblockImprovements");
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		ItemPickupLog.init();

	}
	public static void toggleLog(){
		logEnabled = !logEnabled;
	}
	public static boolean logEnabled(){
		return logEnabled;
	}
}