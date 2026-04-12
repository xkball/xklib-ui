package com.xkball.xklibmc_example;

import com.xkball.xklib.ui.render.IComponent;
import com.xkball.xklib.ui.render.IGUIGraphics;
import com.xkball.xklib.ui.widget.Widget;
import com.xkball.xklibmc.ui.XKLibBaseScreen;
import com.xkball.xklibmc_example.client.render.pip.WorldTerrainPipRenderer;
import com.xkball.xklibmc_example.ui.widget.WorldTerrainWidget;
import com.xkball.xklibmc_example.ui.widget.WorldTerrainWidgetInner;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterPictureInPictureRenderersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;

@Mod(value = XKLibMCExample.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = XKLibMCExample.MODID, value = Dist.CLIENT)
public class XKLibMCExampleClient {
    
    public XKLibMCExampleClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
    
    }
    
    @SubscribeEvent
    public static void onItemUse(UseItemOnBlockEvent event){
        if(event.getLevel().isClientSide() && event.getItemStack().getItem() == Items.BONE && Minecraft.getInstance().screen == null){
            var w = createTestWidget();
            var screen = new XKLibBaseScreen();
            screen.addScreenLayer(w);
            Minecraft.getInstance().setScreen(screen);
        }
    }
    
    public static Widget createTestWidget(){
        return XKLibBaseScreen.biPanelFrame(IComponent.literal("test"),new Widget(),new WorldTerrainWidget());
    }
    
    @SubscribeEvent
    public static void onRegPIP(RegisterPictureInPictureRenderersEvent event){
        event.register(WorldTerrainPipRenderer.WorldTerrainState.class, WorldTerrainPipRenderer::new);
    }
    
}
