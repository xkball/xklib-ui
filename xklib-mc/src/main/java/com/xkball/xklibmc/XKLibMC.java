package com.xkball.xklibmc;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;

@Mod(XKLibMC.MODID)
public class XKLibMC {

    public static final String MODID = "xklibmc";

    public static final Logger LOGGER = LogUtils.getLogger();
    
    public XKLibMC(IEventBus modEventBus, ModContainer modContainer) {
        
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }


}
