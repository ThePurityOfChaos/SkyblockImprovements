package com.github.thepurityofchaos.mixin;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.github.thepurityofchaos.SkyblockImprovements;
import com.github.thepurityofchaos.features.economic.BatFirework;
import com.github.thepurityofchaos.features.economic.Bingo;
import com.github.thepurityofchaos.features.economic.GenericProfit;
import com.github.thepurityofchaos.features.itempickuplog.ItemPickupLog;
import com.github.thepurityofchaos.features.packswapper.PackScreen;
import com.github.thepurityofchaos.features.packswapper.PackSwapper;
import com.github.thepurityofchaos.features.retexturer.Retexturer;
import com.github.thepurityofchaos.storage.Sacks;
import com.github.thepurityofchaos.storage.config.ConfigScreen;
import com.github.thepurityofchaos.storage.config.EcoConfig;
import com.github.thepurityofchaos.storage.config.IPLConfig;
import com.github.thepurityofchaos.storage.config.PSConfig;
import com.github.thepurityofchaos.utils.inventory.ChangeInstance;
import com.github.thepurityofchaos.utils.math.ColorUtils;
import com.github.thepurityofchaos.utils.screen.GeneratorScreen;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

/**
 * A mixin to bind ALL commands related to SkyblockImprovements. 
 */
@Mixin(SkyblockImprovements.class)
public class CommandsMetaMixin {
    //Inject into the mod's initializer. If this isn't done, causes an EXCEPTION_ACCESS_VIOLATION.
    /**
     * Command registration superstructure.
     *
     * @see https://docproject.github.io/fabricmc_fabric/net/fabricmc/fabric/api/client/screen/v1/ScreenEvents.html
     *    
     * @param info
     */
    @Inject(at = @At("HEAD"), method = "onInitializeClient", remap = false)
    private void onInitializeClient(CallbackInfo info){
        //base command to access SBI's User Interface 
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
                            Sacks.getInstance().toggle();
                            IPLConfig.saveSettings();
                            return 1;
                        }
                ))
                .then(ClientCommandManager.literal("toggleSackMessage")
                        .executes(context ->{
                            IPLConfig.toggleRemoval();
                            return 1;
                        }
                ))
                .executes(context ->{
                    ItemPickupLog.getInstance().toggle();
                    IPLConfig.saveSettings();
                    return 1;
                }
            ))
            //Pack Swapper
            .then(ClientCommandManager.literal("PackSwapper")
                .then(ClientCommandManager.literal("setColorCode")
                    .then(ClientCommandManager.argument("color_code_char", StringArgumentType.word())
                        .executes(context ->{
                            PackSwapper.getInstance().setRegionColor(StringArgumentType.getString(context, "color_code_char").charAt(0));
                            PSConfig.saveSettings();
                            return 1;
                        }   
                )))
                .then(ClientCommandManager.literal("toggleRPHelper")
                    .executes(context ->{
                        PackSwapper.getInstance().togglePackHelper();
                        PSConfig.saveSettings();
                        return 1;
                    }
                ))
                .then(ClientCommandManager.literal("toggleRender")
                    .executes(context ->{
                        PackSwapper.getInstance().toggleRenderComponent();
                        PSConfig.saveSettings();
                        return 1;
                    }
                ))
                .then(ClientCommandManager.literal("toggleDebugInfo")
                    .executes(context ->{
                        PackSwapper.getInstance().toggleDebugInfo();
                        PSConfig.saveSettings();
                        return 1;
                    }
                ))
                .then(ClientCommandManager.literal("config")
                    .executes(context ->{
                        context.getSource().getClient().send(() -> context.getSource().getClient().setScreen(
                            new PackScreen().initAsPackMap(null,PackSwapper.getInstance().getFullRegionMap())
                            ));
                        PSConfig.saveSettings();
                        return 1;
                    }
                ))
                .executes(context ->{
                    PackSwapper.getInstance().toggle();
                    PSConfig.saveSettings();
                    return 1;
                }
            ))
            //Economic Helpers
            .then(ClientCommandManager.literal("EconomicHelpers")
            
            .then(ClientCommandManager.literal("setColorCode")
                .then(ClientCommandManager.argument("color_code_char",StringArgumentType.word())
                    .executes(context ->{
                        EcoConfig.setColorCode(StringArgumentType.getString(context, "color_code_char").charAt(0));
                        EcoConfig.saveSettings();
                        return 1;
                    }
            )))
            //Bat Firework Helper
            .then(ClientCommandManager.literal("BatFirework")
                    
                .then(ClientCommandManager.literal("resetProfit")
                    .executes(context ->{
                        BatFirework.getInstance().resetProfit();
                        return 1;
                    }
            ))            
                .executes(context ->{
                    BatFirework.getInstance().toggle();
                    EcoConfig.saveSettings();
                    return 1;
                }
            ))
            //Math Helper
            .then(ClientCommandManager.literal("toggleMathHelper")
                    .executes(context ->{
                        EcoConfig.toggleMath();
                        return 1;
                    }

            ))
            //Generic Profit Helper
            .then(ClientCommandManager.literal("resetProfit")
                    .executes(context ->{
                        GenericProfit.getInstance().resetProfit();
                        return 1;
                    }

            ))
            //Bingo
            .then(ClientCommandManager.literal("Bingo")  
                .then(ClientCommandManager.literal("showCommunity")
                    .executes(context ->{
                        Bingo.getInstance().toggleCommunity();
                        return 1;
                    }
            ))            
                .executes(context ->{
                    Bingo.getInstance().toggle();
                    EcoConfig.saveSettings();
                    return 1;
                }
            ))
        )
        //Helmer Retexturer
        .then(ClientCommandManager.literal("HelmetRetexturer")
            
        .then(ClientCommandManager.literal("setColor")
            .then(ClientCommandManager.argument("Red 0-255",IntegerArgumentType.integer())
                .then(ClientCommandManager.argument("Green 0-255",IntegerArgumentType.integer())
                    .then(ClientCommandManager.argument("Blue 0-255",IntegerArgumentType.integer())
                        .executes(context ->{
                            Retexturer.getInstance().changeColor(ColorUtils.rGBAToInt(
                            (int)(IntegerArgumentType.getInteger(context, "Red 0-255")), 
                            (int)(IntegerArgumentType.getInteger(context, "Green 0-255")), 
                            (int)(IntegerArgumentType.getInteger(context, "Blue 0-255")), 
                            255));
                            return 1;
                        }
        )))))
        .then(ClientCommandManager.literal("setK")
            .then(ClientCommandManager.argument("K 2-16",IntegerArgumentType.integer())
                .executes(context ->{
                    Retexturer.getInstance().changeK((int)(IntegerArgumentType.getInteger(context, "K 2-16")));
                    return 1;
                }
        )))
        .executes(context ->{
            Retexturer.getInstance().toggleRecolor();
            return 1;
        }))
        //Debug Features
        .then(ClientCommandManager.literal("EXPERIMENTAL_TOGGLE_DEBUG_FEATURES")
            .executes(context ->{
                SkyblockImprovements.EXPERIMENTAL_TOGGLE_DEBUG_FEATURES();
                return 1;
            }
        ))



        //default execution
        .executes(context -> {
            //Create and Display Config Screen
            ConfigScreen screen = new ConfigScreen();
            screen.init(null);
            //this specific portion took me about 9 hours. It's ridiculous. I tried MinecraftClient.getInstance().setScreen(), context.getSource().getClient().setScreen(), 
            //looked anywhere and everywhere online. until finally THIS worked.
            context.getSource().getClient().send(() -> context.getSource().getClient().setScreen(screen)); 
            return 1;
        })));
    ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher
        .register(ClientCommandManager.literal("gen")
            .then(ClientCommandManager.literal("item")
                .executes(context ->{
                    GeneratorScreen screen = GeneratorScreen.getInstance();
                    screen.init(null);
                    context.getSource().getClient().send(() -> context.getSource().getClient().setScreen(screen)); 
                    return 1;
                }
            ))
        ));
    }
    
}
