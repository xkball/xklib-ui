package com.xkball.xklibmc_example;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.TicketType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

@Mod(XKLibMCExample.MODID)
public class XKLibMCExample {

    public static final String MODID = "xklibmc_example";

    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<TicketType> TICKET_TYPE = DeferredRegister.create(BuiltInRegistries.TICKET_TYPE, XKLibMCExample.MODID);
    public static final DeferredHolder<TicketType, TicketType> MAP_GEOMATICS = TICKET_TYPE.register("map_geomatics", () -> new TicketType(TicketType.NO_TIMEOUT, TicketType.FLAG_LOADING));
    
    public XKLibMCExample(IEventBus modEventBus, ModContainer modContainer) {
        TICKET_TYPE.register(modEventBus);
        modContainer.registerConfig(ModConfig.Type.COMMON, ServerConfig.SPEC);
    }
    
}
