package com.xkball.xklibmc_example.client.terrain;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.xkball.xklibmc.XKLibMCClient;
import com.xkball.xklibmc.api.client.b3d.ICloseOnExit;
import com.xkball.xklibmc.client.b3d.IndirectDrawCommand;
import com.xkball.xklibmc.utils.ClientUtils;
import com.xkball.xklibmc.utils.VanillaUtils;
import com.xkball.xklibmc_example.network.c2s.RequestServerChunk;
import com.xkball.xklibmc_example.utils.DualQueueThreadPool;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@EventBusSubscriber(Dist.CLIENT)
public class TerrainChunkManager implements ICloseOnExit<TerrainChunkManager> {
    
    public static final TerrainChunkManager INSTANCE = new TerrainChunkManager();
    
    public final Map<ResourceKey<Level>, LevelChunkStorage> storageMap = new ConcurrentHashMap<>();
    public final DualQueueThreadPool taskQueue = new DualQueueThreadPool();
    public boolean compatibleMode = false;
    
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event) {
        if(!Minecraft.getInstance().isPaused() && Minecraft.getInstance().level != null){
            INSTANCE.taskQueue.runFor10ms();
            for(var s : INSTANCE.storageMap.values()){
                for(var b : s.getGpuBuffers()){
                    if(!b.stagedAllocations.isEmpty()) {
                        b.uploadStagedAllocations(ClientUtils.getGpuDevice(), ClientUtils.getCommandEncoder());
                    }
                }
            }
        }
        if(XKLibMCClient.tickCount % 1200 == 0){
            for(var s : INSTANCE.storageMap.values()){
                s.saveFile();
            }
        }
    }
    
    @SubscribeEvent
    public static void onClientLoggedIn(ClientPlayerNetworkEvent.LoggingIn event){
        var level = Minecraft.getInstance().level;
        if(level == null) return;
        INSTANCE.setCloseOnExit();
        if(!INSTANCE.storageMap.containsKey(level.dimension())){
            var s = new LevelChunkStorage(level.dimension(),level.getMinY(), INSTANCE.compatibleMode);
            s.loadFile();
            INSTANCE.storageMap.put(level.dimension(), s);
        }
        else{
            INSTANCE.storageMap.get(level.dimension()).loadFile();
        }
    }
    
    @SubscribeEvent
    public static void onClientLoggedOut(ClientPlayerNetworkEvent.LoggingOut event){
        var level = Minecraft.getInstance().level;
        INSTANCE.unloadLevel(level);
    }
    
    public TerrainChunkManager() {
    
    }
    
    public @Nullable LevelChunkStorage getCurrentLevelChunkStorage(){
        if(Minecraft.getInstance().level == null) return null;
        return this.storageMap.get(Minecraft.getInstance().level.dimension());
    }
    
    public RenderInfo gatherRenderInfo(Frustum frustum, boolean cullNear, Vector3f camPos, Vector3f camTar, int baseLodDistance){
        var level = Minecraft.getInstance().level;
        if(level == null) return RenderInfo.empty();
        var storage = this.storageMap.get(level.dimension());
        if(storage == null ) return RenderInfo.empty();
        var gather1 = new RenderInfoBlockGather();
        var gather2 = new RenderInfoBlockGather();
        var gatherLod = new RenderInfoBlockGather[]{new RenderInfoBlockGather(), new RenderInfoBlockGather(), new RenderInfoBlockGather()};
        for(var chunkStorage : storage.getChunks()){
            var aabb = chunkStorage.chunkAABB;
            if(!frustum.isVisible(aabb)) continue;
            if(cullNear && new Vector2f((float) Mth.lerp(0.5f, aabb.minX, aabb.maxX), (float) Mth.lerp(0.5f, aabb.minZ, aabb.maxZ)).sub(new Vector2f(camTar.x, camTar.z)).lengthSquared() < 64 * 64) continue;
            var lod = chunkStorage.getLodLevel(baseLodDistance, camPos);
            if(lod < 0) continue;
            if(lod == 0 && !compatibleMode){
                for (int i = 0; i < 6; i++) {
                    var dir = VanillaUtils.DIRECTIONS[i];
                    if(!(dirToFace(dir, aabb, camPos).dot(dir.getUnitVec3f()) < 0)) continue;
                    var gpuBuffer = storage.gpuBufferByFace.get(dir);
                    var alloc = gpuBuffer.getAllocation(chunkStorage.chunkPos);
                    if(alloc == null) continue;
                    var buffer = gpuBuffer.getGpuBuffer(alloc);
                    var offset = alloc.getOffsetFromHeap() / 16;
                    var size = alloc.getSize() / 16;
                    var cmd = new IndirectDrawCommand(6, (int) size, i*6,0, (int) offset);
                    gather1.add(buffer,cmd);
                }
            }
            else {
                var alloc = chunkStorage.getLodBufferFullMesh(lod);
                if(alloc == null) continue;
                var buffer = storage.gpuBufferByLodFullMesh.getGpuBuffer(alloc);
                var cmd = new IndirectDrawCommand(6 * chunkStorage.facesCountByLodFullMesh(lod), 1, (int) (alloc.getOffsetFromHeap() / 20),0, 0);
                gather2.add(buffer,cmd);
            }
        }
        return new RenderInfo(gather1.finishGather(), gather2.finishGather(), gatherLod[0].finishGatherFirstBuffer(), gatherLod[1].finishGatherFirstBuffer(), gatherLod[2].finishGatherFirstBuffer());
    }
    
    Vector3f dirToFace(Direction dir, AABB aabb, Vector3f pos){
        var centerX = (float) (aabb.maxX + aabb.minX) / 2;
        var centerY = (float) (aabb.maxY + aabb.minY) / 2;
        var centerZ = (float) (aabb.maxZ + aabb.minZ) / 2;
        var center =  switch (dir){
            case DOWN -> new Vector3f(centerX, (float) aabb.maxY, centerZ);
            case UP -> new Vector3f(centerX, (float) aabb.minY, centerZ);
            case NORTH -> new Vector3f(centerX, centerY, (float) aabb.maxZ);
            case SOUTH -> new Vector3f(centerX, centerY, (float) aabb.minZ);
            case WEST -> new Vector3f((float) aabb.maxX, centerY, centerZ);
            case EAST -> new Vector3f((float) aabb.minX, centerY, centerZ);
        };
        return center.sub(pos,center).normalize();
    }
    
    public void submitUpdate(BlockPos center, int range, boolean force){
        var centerChunk = ChunkPos.containing(center);
        for(var dx = -range; dx <= range; dx++){
            for(var dz = -range; dz <= range; dz++){
                this.submitUpdate(new ChunkPos(centerChunk.x()+dx,centerChunk.z()+dz), force);
            }
        }
    }
    
    public void submitTask(Runnable runnable){
        this.taskQueue.submitWorker(runnable);
    }
    
    public void submitUpdate(ChunkPos chunkPos, boolean force){
        this.submitUpdate(null, chunkPos, force);
    }
    
    public void submitUpdate(@Nullable LevelChunk chunk, ChunkPos chunkPos, boolean force){
        var level = Minecraft.getInstance().level;
        if(level == null) return;
        var dim = level.dimension();
        Runnable task = () -> {
            var level_ = Minecraft.getInstance().level;
            if(level_ == null) return;
            var dimNew = level_.dimension();
            if(dimNew != dim) return;
            var storage = this.storageMap.get(dimNew);
            if(storage == null){
                storage = new LevelChunkStorage(dimNew,level_.getMinY(), INSTANCE.compatibleMode);
                this.storageMap.put(dimNew, storage);
            }
            var chunkOld = storage.getChunk(chunkPos);
            if(chunkOld != null && !force) return;
            ChunkStorage chunkStorage;
            if(chunk == null) chunkStorage = LevelChunkStorage.COMPLIER.compile(storage,level_,chunkPos);
            else chunkStorage = LevelChunkStorage.COMPLIER.compile(storage, level_, chunk, chunkPos, true);
            if(chunkStorage != null) {
                LevelChunkStorage finalStorage = storage;
                this.taskQueue.submitMain(() -> {
                    finalStorage.putChunk(chunkStorage);
                    chunkStorage.uploadGpu0();
                    for (int dx = 0; dx < 2; dx++) {
                        for (int dz = 0; dz < 2; dz++) {
                            var cp = finalStorage.getChunk(new ChunkPos(chunkPos.x()-dx,chunkPos.z()-dz));
                            if(cp == null) continue;
                            cp.uploadGpuLodFullMesh();
                        }
                    }
                });
            }
        };
        this.taskQueue.submitWorker(task);
    }
    
    public void unloadLevel(Level level){
        if(level != null){
            var storage = this.storageMap.get(level.dimension());
            if(storage != null){
                storage.unloadGpu();
                storage.saveFile();
            }
            this.storageMap.remove(level.dimension());
        }
        this.taskQueue.clear();
    }
    
    
    @Override
    public void close() {
        for(var storage : this.storageMap.values()){
            storage.unloadGpu();
        }
        this.taskQueue.shutdown();
    }
    
    public long getMemAlloc(){
        var result = 0L;
        for(var storage : this.storageMap.values()){
            for(var b : storage.getGpuBuffers()){
                for(var p : b.nodes){
                    result += p.getFirst().totalMemorySize;
                }
            }
            result += storage.gpuBufferLod.gpuBuffer.size();
        }
        return result;
    }
    
    public long getMemUsed(){
        var result = 0L;
        return result;
    }
    
    public record RenderInfo(List<RenderInfoBlock> lod0, List<RenderInfoBlock> lodFullMesh,@Nullable RenderInfoBlock lod1,@Nullable RenderInfoBlock lod2,@Nullable RenderInfoBlock lod3) implements AutoCloseable{
        
        public static RenderInfo empty(){
            return new RenderInfo(null, null, null, null, null);
        }
        
        @Override
        public void close() {
            if(lod0 != null){
                for(var v : lod0) {
                    v.commandBuffer.close();
                }
            }
            if(lodFullMesh != null){
                for(var v : lodFullMesh) {
                    v.commandBuffer.close();
                }
            }
            if(lod1 != null){
                lod1.commandBuffer.close();
            }
            if(lod2 != null){
                lod2.commandBuffer.close();
            }
            if(lod3 != null){
                lod3.commandBuffer.close();
            }
        }
    }
    
    public record RenderInfoBlock(GpuBuffer drawBuffer, int drawCount, GpuBuffer commandBuffer){
    
    }
    
    public static class RenderInfoBlockGather{
        public Map<GpuBuffer,ArrayList<IndirectDrawCommand>> cmdMap = new IdentityHashMap<>();
        
        public void add(GpuBuffer buffer, IndirectDrawCommand command){
            cmdMap.compute(buffer, (_,v) -> {
                if(v == null){
                    v = new ArrayList<>();
                }
                v.add(command);
                return v;
            });
        }
        
        public List<RenderInfoBlock> finishGather(){
            var renderInfoList = new ArrayList<RenderInfoBlock>();
            for(var entry :  cmdMap.entrySet()){
                var buffer = entry.getKey();
                var list = entry.getValue();
                renderInfoList.add(new RenderInfoBlock(buffer, list.size(), IndirectDrawCommand.buildCommandList(list)));
            }
            return renderInfoList;
        }
        
        public @Nullable RenderInfoBlock finishGatherFirstBuffer(){
            var list = finishGather();
            return list.isEmpty() ? null : list.getFirst();
        }
    }
    
}
