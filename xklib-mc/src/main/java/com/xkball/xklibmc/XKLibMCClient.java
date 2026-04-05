package com.xkball.xklibmc;

import com.xkball.xklibmc.ui.XKLibBaseScreen;
import com.xkball.xklibmc.ui.widget.WidgetWrapper;
import com.xkball.xklib.ui.deco.Background;
import com.xkball.xklib.ui.widget.Label;
import com.xkball.xklib.ui.widget.Widget;
import com.xkball.xklib.ui.widget.container.ContainerWidget;
import com.xkball.xklib.ui.widget.container.SplitContainer;
import com.xkball.xklib.ui.layout.TextScale;
import dev.vfyjxf.taffy.geometry.TaffySize;
import dev.vfyjxf.taffy.style.AlignContent;
import dev.vfyjxf.taffy.style.AlignItems;
import dev.vfyjxf.taffy.style.FlexDirection;
import dev.vfyjxf.taffy.style.LengthPercentage;
import dev.vfyjxf.taffy.style.Overflow;
import dev.vfyjxf.taffy.style.TaffyDimension;
import dev.vfyjxf.taffy.style.TaffyDisplay;
import dev.vfyjxf.taffy.geometry.TaffyPoint;
import dev.vfyjxf.taffy.style.TaffyStyle;
import dev.vfyjxf.taffy.style.TextAlign;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
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

import java.util.concurrent.atomic.AtomicInteger;

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
        return createScrollableTestPanel();
    }
    
    public static Widget createScrollableTestPanel(){
        // Create a scrollable column panel to test MC widgets
        var root = new ContainerWidget();
        root.asTreeRoot();
        
        // Configure root as a scrollable column container
        var rootStyle = new TaffyStyle();
        rootStyle.flexDirection = FlexDirection.COLUMN;
        rootStyle.alignContent = AlignContent.CENTER;
        rootStyle.alignItems = AlignItems.CENTER;
        rootStyle.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.percent(1f));
        rootStyle.overflow = new TaffyPoint<>(Overflow.VISIBLE, Overflow.SCROLL);  // Enable vertical scrolling
        rootStyle.scrollbarWidth = 8;
        rootStyle.gap = new TaffySize<>(LengthPercentage.length(10), LengthPercentage.length(10));
        root.setStyle(rootStyle);
        
        // Add title label
        var titleLabel = new Label("MC Widget Compatibility Test", TextAlign.CENTER, 0xFFFFFFFF);
        titleLabel.addDecoration(new Background(0xFF4A4A4A));
        var titleStyle = new TaffyStyle();
        titleStyle.size = new TaffySize<>(TaffyDimension.percent(0.5f), TaffyDimension.length(30));
        titleStyle.flexShrink = 0;
        root.addChild(titleLabel, titleStyle);
        
        // Add section label for Buttons
        var buttonSectionLabel = new Label("Button Tests:", TextAlign.LEFT, 0xFFAAAAAA);
        var buttonSectionStyle = new TaffyStyle();
        buttonSectionStyle.size = new TaffySize<>(TaffyDimension.percent(0.5f), TaffyDimension.length(20));
        buttonSectionStyle.flexShrink = 0;
        root.addChild(buttonSectionLabel, buttonSectionStyle);
        
        // Add multiple buttons to test
        AtomicInteger clickCount = new AtomicInteger(0);
        for (int i = 0; i < 5; i++) {
            final int buttonIndex = i;
            var buttonWrapper = WidgetWrapper.button("Test Button " + (i + 1), btn -> {
                clickCount.incrementAndGet();
                btn.setMessage(Component.literal("Clicked " + clickCount.get() + " times!"));
            });
            var buttonStyle = new TaffyStyle();
            buttonStyle.size = new TaffySize<>(TaffyDimension.percent(0.5f), TaffyDimension.length(48));
            buttonStyle.flexShrink = 0;
            root.addChild(buttonWrapper, buttonStyle);
        }
        
        // Add section label for EditBox
        var editSectionLabel = new Label("EditBox Tests:", TextAlign.LEFT, 0xFFAAAAAA);
        var editSectionStyle = new TaffyStyle();
        editSectionStyle.size = new TaffySize<>(TaffyDimension.percent(0.5f), TaffyDimension.length(40));
        editSectionStyle.flexShrink = 0;
        root.addChild(editSectionLabel, editSectionStyle);
        
        // Add multiple EditBox widgets
        for (int i = 0; i < 3; i++) {
            var editBoxWrapper = WidgetWrapper.editBox("Enter text here... (" + (i + 1) + ")", 100);
            var editBoxStyle = new TaffyStyle();
            editBoxStyle.size = new TaffySize<>(TaffyDimension.percent(0.5f), TaffyDimension.length(24));
            editBoxStyle.flexShrink = 0;
            root.addChild(editBoxWrapper, editBoxStyle);
        }
        
        // Add section label for MultiLineEditBox
        var multiLineSectionLabel = new Label("MultiLineEditBox Tests:", TextAlign.LEFT, 0xFFAAAAAA);
        var multiLineSectionStyle = new TaffyStyle();
        multiLineSectionStyle.size = new TaffySize<>(TaffyDimension.percent(0.5f), TaffyDimension.length(20));
        multiLineSectionStyle.flexShrink = 0;
        root.addChild(multiLineSectionLabel, multiLineSectionStyle);
        
        // Add multiple MultiLineEditBox widgets
        for (int i = 0; i < 3; i++) {
            var multiLineWrapper = WidgetWrapper.multiLineTextWidget();
            var multiLineStyle = new TaffyStyle();
            multiLineStyle.size = new TaffySize<>(TaffyDimension.percent(0.5f), TaffyDimension.length(80));
            multiLineStyle.flexShrink = 0;
            root.addChild(multiLineWrapper, multiLineStyle);
        }
        
        // Add more items to make the panel scrollable
        var spacerLabel = new Label("Scroll down for more content...", TextAlign.CENTER, 0xFF888888);
        var spacerStyle = new TaffyStyle();
        spacerStyle.size = new TaffySize<>(TaffyDimension.percent(0.5f), TaffyDimension.length(200));
        spacerStyle.flexShrink = 0;
        root.addChild(spacerLabel, spacerStyle);
        
        // Add bottom buttons
        var bottomButton = WidgetWrapper.button("Close Test Panel", btn -> {
            Minecraft.getInstance().setScreen(null);
        });
        var bottomStyle = new TaffyStyle();
        bottomStyle.size = new TaffySize<>(TaffyDimension.percent(0.5f), TaffyDimension.length(30));
        bottomStyle.flexShrink = 0;
        root.addChild(bottomButton, bottomStyle);
        
        return root;
    }
    
}
