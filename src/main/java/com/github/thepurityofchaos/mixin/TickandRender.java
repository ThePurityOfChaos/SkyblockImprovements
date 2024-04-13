package com.github.thepurityofchaos.mixin;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.github.thepurityofchaos.SkyblockImprovements;
import com.github.thepurityofchaos.config.Config;
import com.github.thepurityofchaos.features.itempickuplog.IPLRender;
import com.github.thepurityofchaos.features.packswapper.PSRender;
import com.github.thepurityofchaos.utils.scoreboard.ScoreboardProcessor;
import com.github.thepurityofchaos.utils.scoreboard.TabListProcessor;




@Mixin(SkyblockImprovements.class)
public class TickandRender {
    
    //Inject into the mod's initializer. If this isn't done, causes an EXCEPTION_ACCESS_VIOLATION.
    @Inject(at = @At("TAIL"), method = "onInitializeClient", remap = false)
    private void onInitializeClient(CallbackInfo info){
        //Render all
        HudRenderCallback.EVENT.register((drawContext, tickDelta)->{
            //Process Scoreboard & Tab List for this Tick 
            //(Used for multiple events- piggybacking on HudRenderCallback, 
            //makes it so that creating a new ticking system is not necessary)
            ScoreboardProcessor.processScoreboard();
            TabListProcessor.processTabList();
            
            //Item Pickup Log
            if(Config.getFeatureEnabled("ItemPickupLog"))
                IPLRender.render(drawContext,tickDelta);

            //Pack Swapper
            if(Config.getFeatureEnabled("PackSwapper"))
                PSRender.render(drawContext, tickDelta);
        });
        
        //Econ

        //HRet
    }   
}
