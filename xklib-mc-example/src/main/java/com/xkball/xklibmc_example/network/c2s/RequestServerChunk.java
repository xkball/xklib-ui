package com.xkball.xklibmc_example.network.c2s;

import com.xkball.xklibmc.annotation.NonNullByDefault;
import com.xkball.xklibmc.utils.VanillaUtils;
import com.xkball.xklibmc_example.network.s2c.SentChunkToClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

@NonNullByDefault
public record RequestServerChunk(List<ChunkPos> pos, boolean generate) implements CustomPacketPayload {
    
    public static final CustomPacketPayload.Type<RequestServerChunk> TYPE = new Type<>(VanillaUtils.modRL("request_server_chunk"));
    
    public static final StreamCodec<ByteBuf, RequestServerChunk> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.collection(ArrayList::new, ChunkPos.STREAM_CODEC),
            RequestServerChunk::pos,
            ByteBufCodecs.BOOL,
            RequestServerChunk::generate,
            RequestServerChunk::new
    );
    
    
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    
    public void handle(IPayloadContext context) {
        if(!(context.player() instanceof ServerPlayer serverPlayer) || !(context.player().level() instanceof ServerLevel level)) return;
        for(var p : this.pos){
            context.enqueueWork(() -> {
                //TODO 想办法加载区块但是不生成新区块
                var chunk = level.getChunk(p.x(), p.z());
                Thread.startVirtualThread(() -> PacketDistributor.sendToPlayer(serverPlayer,new SentChunkToClient(p, chunk)));
            });
           
        }
   
    }
}
