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
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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
        if (!(context.player() instanceof ServerPlayer serverPlayer) || !(context.player().level() instanceof ServerLevel level))
            return;
        context.enqueueWork(() -> {
            var t = new Ticket(XKLibMCExample.MAP_GEOMATICS.get(), ChunkLevel.byStatus(generate ? ChunkStatus.FULL : ChunkStatus.EMPTY));
            Thread.startVirtualThread(() -> {
                var chunkFutures = new ArrayList<CompletableFuture<Void>>(1024);
                for (int i = 0; i < this.pos.size(); i++) {
                    var p = this.pos.get(i);
                    var future = CompletableFuture.runAsync(() -> {
                                level.getChunkSource().addTicket(t, p);
                                level.getChunkSource().runDistanceManagerUpdates();
                            }, level.getServer())
                            .thenCombineAsync(
                                    level.getChunkSource().getChunkFuture(p.x(), p.z(), generate ? ChunkStatus.FULL : ChunkStatus.EMPTY, true),
                                    (_, it) -> it)
                            .thenAcceptAsync(it -> {
                                it.ifSuccess(chunkAccess -> {
                                    if (chunkAccess.getPersistedStatus().isOrAfter(ChunkStatus.FULL)) {
                                        if (chunkAccess instanceof ImposterProtoChunk ipc) {
                                            var pack = new SentChunkToClient(p, ipc.getWrapped());
                                            CompletableFuture.runAsync(() -> PacketDistributor.sendToPlayer(serverPlayer, pack));
                                            
                                        }
                                        if (chunkAccess instanceof LevelChunk levelChunk) {
                                            var pack = new SentChunkToClient(p, levelChunk);
                                            CompletableFuture.runAsync(() -> PacketDistributor.sendToPlayer(serverPlayer, pack));
                                        }
                                    }
                                });
                                level.getChunkSource().ticketStorage.removeTicket(t, p);
                            }, level.getServer());
                    chunkFutures.add(future);
                    if (chunkFutures.size() == 1024 || i == this.pos.size() - 1) {
                        CompletableFuture.allOf(chunkFutures.toArray(CompletableFuture[]::new)).join();
                        chunkFutures.clear();
                    }
                }
            });
        });
        
    }
}
