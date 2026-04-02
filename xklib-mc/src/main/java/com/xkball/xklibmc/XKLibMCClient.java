package com.xkball.xklibmc;

import com.xkball.xklibmc.ui.XKLibBaseScreen;
import com.xkball.xklib.ui.deco.Background;
import com.xkball.xklib.ui.widget.Widget;
import com.xkball.xklib.ui.widget.container.ContainerWidget;
import com.xkball.xklib.ui.widget.container.SplitContainer;
import dev.vfyjxf.taffy.geometry.TaffySize;
import dev.vfyjxf.taffy.style.TaffyDimension;
import dev.vfyjxf.taffy.style.TaffyStyle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
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
            var w = createWidget();
            var screen = new XKLibBaseScreen();
            screen.addScreenLayer(w);
            Minecraft.getInstance().setScreen(screen);
        }
    }
    
    public static Widget createWidget(){
        var root = new SplitContainer(false);
        root.asTreeRoot();
        
        var left = colorPanel(0xFF1A6B5A);
        var leftStyle = new TaffyStyle();
        leftStyle.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.percent(1f));
        root.setPanel(0, left, leftStyle);
        
        var right = colorPanel(0xFF6B3A1A);
        var rightStyle = new TaffyStyle();
        rightStyle.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.percent(1f));
        root.setPanel(1, right, rightStyle);
        
        return root;
    }
    
    private static ContainerWidget colorPanel(int color) {
        var w = new ContainerWidget();
        w.addDecoration(new Background(color));
        return w;
    }
}
