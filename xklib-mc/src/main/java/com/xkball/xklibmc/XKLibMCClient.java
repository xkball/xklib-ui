package com.xkball.xklibmc;

import com.xkball.xklib.ui.render.IComponent;
import com.xkball.xklibmc.ui.XKLibBaseScreen;
import com.xkball.xklib.ui.widget.Widget;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;

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
    public static void onItemUse(UseItemOnBlockEvent event){
        if(event.getLevel().isClientSide() && event.getItemStack().getItem() == Items.BONE){
            var w = createTestWidget();
            var screen = new XKLibBaseScreen();
            screen.addScreenLayer(w);
            Minecraft.getInstance().setScreen(screen);
        }
    }
    
    public static Widget createTestWidget(){
        return XKLibBaseScreen.biPanelFrame(IComponent.literal("test"),new Widget(),new Widget());
    }
    

    
}
