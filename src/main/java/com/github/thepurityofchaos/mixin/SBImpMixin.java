package com.github.thepurityofchaos.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//Example Mixin, used exclusively for reference. Don't include this in the jar file.

@Mixin(MinecraftClient.class)
//Include this in sbimp.mixins.json. 
public class SBImpMixin {
	@Inject(at = @At("HEAD"), method = "run")
	private void run(CallbackInfo info) {
		// This code is injected into the start of MinecraftClient.run()
		//currently has no use, but may be useful in the future.
	}
}