package com.xkball.xklibmc_example.network;

import com.xkball.xklibmc_example.XKLibMCExample;
import com.xkball.xklibmc_example.network.c2s.RequestServerChunk;
import com.xkball.xklibmc_example.network.s2c.SentChunkToClient;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@EventBusSubscriber
public class XKLibExampleNetwork {
    
    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        var register = event.registrar(XKLibMCExample.MODID);
        register.playToServer(RequestServerChunk.TYPE,RequestServerChunk.STREAM_CODEC,RequestServerChunk::handle);
        register.playToClient(SentChunkToClient.TYPE,SentChunkToClient.STREAM_CODEC,SentChunkToClient::handle);
    }
}
