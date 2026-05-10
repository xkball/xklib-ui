package com.xkball.xklibmc_example.network.c2s;

import com.xkball.xklibmc.annotation.NonNullByDefault;
import com.xkball.xklibmc.utils.VanillaUtils;
import com.xkball.xklibmc_example.XKLibMCExample;
import com.xkball.xklibmc_example.network.s2c.SentChunkToClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ChunkLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.Ticket;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
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
        context.enqueueWork(() -> {
            var t = new Ticket(XKLibMCExample.MAP_GEOMATICS.get(), ChunkLevel.byStatus(ChunkStatus.EMPTY));
            for (var p : this.pos) {
                level.getChunkSource().addTicket(t, p);
            }
            level.getChunkSource().runDistanceManagerUpdates();
            Thread.startVirtualThread(() -> {
                for(var p : this.pos){
                    level.getChunkSource()
                            .getChunkFuture(p.x(),p.z(), ChunkStatus.EMPTY, true)
                            .thenAcceptAsync(it -> it.ifSuccess( chunkAccess -> {
                                if (chunkAccess.getPersistedStatus().isOrAfter(ChunkStatus.FULL)) {
                                    if(chunkAccess instanceof ImposterProtoChunk ipc){
                                        PacketDistributor.sendToPlayer(serverPlayer,new SentChunkToClient(p, ipc.getWrapped()));
                                    }
                                }
                                level.getServer().submit(() -> level.getChunkSource().ticketStorage.removeTicket(t, p));
                            }));
                }
            });
        });
        
    }
}
