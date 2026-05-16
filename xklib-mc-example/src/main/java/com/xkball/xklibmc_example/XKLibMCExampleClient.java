package com.xkball.xklibmc_example;

import com.xkball.xklibmc_example.client.render.pip.WorldTerrainPipRenderer;
import com.xkball.xklibmc_example.client.map.minimap.MinimapHudRenderer;
import com.xkball.xklibmc_example.ui.WorldTerrainScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterPictureInPictureRenderersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;

@Mod(value = XKLibMCExample.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = XKLibMCExample.MODID, value = Dist.CLIENT)
public class XKLibMCExampleClient {
    
    public XKLibMCExampleClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        container.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
    
    }
    
    @SubscribeEvent
    public static void onItemUse(UseItemOnBlockEvent event){
        if(event.getLevel().isClientSide() && event.getItemStack().getItem() == Items.BONE && Minecraft.getInstance().screen == null){
            Minecraft.getInstance().setScreen(new WorldTerrainScreen());
        }
    }
    
    @SubscribeEvent
    public static void onRegPIP(RegisterPictureInPictureRenderersEvent event){
        event.register(WorldTerrainPipRenderer.WorldTerrainState.class, WorldTerrainPipRenderer::new);
    }

    @SubscribeEvent
    public static void onRegisterGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.CROSSHAIR, Identifier.fromNamespaceAndPath(XKLibMCExample.MODID, "minimap"), MinimapHudRenderer::render);
    }
    
}
