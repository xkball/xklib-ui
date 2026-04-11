package com.xkball.xklibmc_example;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(XKLibMCExample.MODID)
public class XKLibMCExample {

    public static final String MODID = "xklibmc_example";

    public static final Logger LOGGER = LogUtils.getLogger();
    
    public XKLibMCExample(IEventBus modEventBus, ModContainer modContainer) {
    }


}
