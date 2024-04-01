package com.sbimp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sbimp.sb.utils.gui.GUIScreen;

import net.fabricmc.api.ClientModInitializer;

public class SkyblockImprovements implements ClientModInitializer {
	private static final Logger LOGGER = LoggerFactory.getLogger(SkyblockImprovements.class);
	private GUIScreen screen;
	@Override
	public void onInitializeClient() {
		LOGGER.info("Entered SkyblockImprovements");
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
	}
}