package com.github.thepurityofchaos.mixin;

import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.github.thepurityofchaos.SkyblockImprovements;
import com.github.thepurityofchaos.features.economic.EcoRender;
import com.github.thepurityofchaos.features.itempickuplog.IPLRender;
import com.github.thepurityofchaos.features.itempickuplog.ItemPickupLog;
import com.github.thepurityofchaos.features.packswapper.PSRender;
import com.github.thepurityofchaos.features.packswapper.PackSwapper;
import com.github.thepurityofchaos.features.retexturer.RTRender;
import com.github.thepurityofchaos.utils.processors.ScoreboardProcessor;
import com.github.thepurityofchaos.utils.processors.TabListProcessor;



/**
 * MIXIN: Injects into the HudRenderCallback to piggyback off of the Scheduler.
 */
@Mixin(SkyblockImprovements.class)
public class TickandRender {
    
    //Inject into the mod's initializer. If this isn't done, causes an EXCEPTION_ACCESS_VIOLATION.
    @Inject(at = @At("TAIL"), method = "onInitializeClient", remap = false)
    private void onInitializeClient(CallbackInfo info){
        //Render all
        HudLayerRegistrationCallback.EVENT.register(layeredDrawer -> layeredDrawer.attachLayerBefore(IdentifiedLayer.MISC_OVERLAYS, Identifier.of("sbimp","default-sbimp-layer"), TickandRender::render));
    }
    public static void render(DrawContext context, RenderTickCounter counter){
        //Process Scoreboard & Tab List for this Tick 
            //(Used for multiple events- piggybacking on HudLayerRegistrationCallback 
            //makes it so that creating a new ticking system is not necessary)
            ScoreboardProcessor.processScoreboard();
            TabListProcessor.processTabList();
            //Item Pickup Log
            if(ItemPickupLog.getInstance().isEnabled())
                IPLRender.render(context, counter);

            //Pack Swapper
            if(PackSwapper.getInstance().isEnabled())
                PSRender.render(context, counter);
            //Economic Features
            EcoRender.render(context, counter);
            
            //Retexturer(s)
            RTRender.render();

    }   
}
