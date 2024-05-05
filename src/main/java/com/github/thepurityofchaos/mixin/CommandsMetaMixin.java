package com.github.thepurityofchaos.mixin;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.github.thepurityofchaos.SkyblockImprovements;
import com.github.thepurityofchaos.config.ConfigScreen;
import com.github.thepurityofchaos.config.EcoConfig;
import com.github.thepurityofchaos.config.IPLConfig;
import com.github.thepurityofchaos.config.PSConfig;
import com.github.thepurityofchaos.features.economic.BatFirework;
import com.github.thepurityofchaos.features.packswapper.PackScreen;
import com.github.thepurityofchaos.features.packswapper.PackSwapper;
import com.github.thepurityofchaos.storage.Sacks;
import com.github.thepurityofchaos.utils.inventory.ChangeInstance;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;


@Mixin(SkyblockImprovements.class)
public class CommandsMetaMixin {
    //Inject into the mod's initializer. If this isn't done, causes an EXCEPTION_ACCESS_VIOLATION.

    @SuppressWarnings("resource")
    @Inject(at = @At("HEAD"), method = "onInitializeClient", remap = false)
    private void onInitializeClient(CallbackInfo info){
        //basic command to access SBI's User Interface 
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher
        .register(ClientCommandManager.literal("sbi")

            //ItemPickupLog
            .then(ClientCommandManager.literal("ItemPickupLog")
                .then(ClientCommandManager.literal("setDistance")
                    .then(ClientCommandManager.argument("distance_between_instances",IntegerArgumentType.integer())
                        .executes(context ->{
                            ChangeInstance.setDistance(IntegerArgumentType.getInteger(context,"distance_between_instances"));
                            IPLConfig.saveSettings();
                            return 1;
                        }
                )))
                .then(ClientCommandManager.literal("setDuration")
                    .then(ClientCommandManager.argument("value_in_seconds",IntegerArgumentType.integer())
                        .executes(context ->{
                            ChangeInstance.setLifespan((int)(IntegerArgumentType.getInteger(context, "value_in_seconds")));
                            IPLConfig.saveSettings();
                            return 1;
                        }
                )))
                .then(ClientCommandManager.literal("setColorCode")
                    .then(ClientCommandManager.argument("color_code_char",StringArgumentType.word())
                        .executes(context ->{
                            ChangeInstance.setColorCode(StringArgumentType.getString(context, "color_code_char").charAt(0));
                            IPLConfig.saveSettings();
                            return 1;
                        }
                )))
                .then(ClientCommandManager.literal("showSackAmounts")
                        .executes(context ->{
                            Sacks.toggleFeature();
                            IPLConfig.saveSettings();
                            return 1;
                        }
                ))
                .executes(context ->{
                    IPLConfig.toggleFeature();
                    IPLConfig.saveSettings();
                    return 1;
                }
            ))
            .then(ClientCommandManager.literal("PackSwapper")
                .then(ClientCommandManager.literal("setColorCode")
                    .then(ClientCommandManager.argument("color_code_char", StringArgumentType.word())
                        .executes(context ->{
                            PackSwapper.setRegionColor(StringArgumentType.getString(context, "color_code_char").charAt(0));
                            PSConfig.saveSettings();
                            return 1;
                        }   
                )))
                .then(ClientCommandManager.literal("toggleRPHelper")
                    .executes(context ->{
                        PackSwapper.togglePackHelper();
                        PSConfig.saveSettings();
                        return 1;
                    }
                ))
                .then(ClientCommandManager.literal("toggleRender")
                    .executes(context ->{
                        PackSwapper.toggleRenderComponent();
                        PSConfig.saveSettings();
                        return 1;
                    }
                ))
                .then(ClientCommandManager.literal("toggleDebugInfo")
                    .executes(context ->{
                        PackSwapper.toggleDebugInfo();
                        PSConfig.saveSettings();
                        return 1;
                    }
                ))
                .then(ClientCommandManager.literal("config")
                    .executes(context ->{
                        context.getSource().getClient().send(() -> context.getSource().getClient().setScreen(
                            new PackScreen().initAsPackMap(null,PackSwapper.getFullRegionMap())
                            ));
                        PSConfig.saveSettings();
                        return 1;
                    }
                ))
                .executes(context ->{
                    PSConfig.toggleFeature();
                    PSConfig.saveSettings();
                    return 1;
                }
            ))
            .then(ClientCommandManager.literal("EconomicHelpers")
            
            .then(ClientCommandManager.literal("setColorCode")
                .then(ClientCommandManager.argument("color_code_char",StringArgumentType.word())
                    .executes(context ->{
                        EcoConfig.setColorCode(StringArgumentType.getString(context, "color_code_char").charAt(0));
                        EcoConfig.saveSettings();
                        return 1;
                    }
            )))
            .then(ClientCommandManager.literal("BatFirework")
                    
                .then(ClientCommandManager.literal("resetProfit")
                    .executes(context ->{
                        BatFirework.resetProfit();
                        return 1;
                    }
                ))            
                .executes(context ->{
                    BatFirework.toggleFeature();
                    EcoConfig.saveSettings();
                    return 1;
                }
            ))
            .then(ClientCommandManager.literal("toggleMathHelper")
                    .executes(context ->{
                        EcoConfig.toggleMath();
                        return 1;
                    }

            ))
            
            .executes(context ->{
                EcoConfig.toggleFeature();
                EcoConfig.saveSettings();
                return 1;
            }
        ))




        //default execution
        .executes(context -> {
            //https://docproject.github.io/fabricmc_fabric/net/fabricmc/fabric/api/client/screen/v1/ScreenEvents.html 

            //Create and Display Config Screen
            ConfigScreen screen = new ConfigScreen();
            screen.init(null);
            //this specific portion took me about 9 hours. It's ridiculous. I tried MinecraftClient.getInstance().setScreen(), context.getSource().getClient().setScreen(), 
            //looked anywhere and everywhere online. until finally THIS worked.
            context.getSource().getClient().send(() -> context.getSource().getClient().setScreen(screen)); 
            return 1;
        })));
  
    }
}
