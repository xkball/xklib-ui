package com.xkball.xklibmc;

import com.mojang.brigadier.Command;
import com.xkball.xklibmc.annotation.NonNullByDefault;
import com.xkball.xklibmc.ui.screen.TexturePreviewScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@NonNullByDefault
@Mod(value = XKLibMC.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = XKLibMC.MODID, value = Dist.CLIENT)
public class XKLibMCClient {
    
    public XKLibMCClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
    
    }

    @SubscribeEvent
    public static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("xklib")
                        .then(Commands.literal("texture_preview")
                                .executes(context -> {
                                    var minecraft = Minecraft.getInstance();
                                    minecraft.execute(() -> minecraft.setScreen(new TexturePreviewScreen()));
                                    return Command.SINGLE_SUCCESS;
                                }))
        );
    }
    
    public static long tickCount = 0;
    
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event) {
        if(!Minecraft.getInstance().isPaused()) tickCount+=1;
    }
    
}
