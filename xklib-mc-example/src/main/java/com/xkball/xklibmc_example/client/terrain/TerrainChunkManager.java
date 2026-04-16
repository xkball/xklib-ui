package com.xkball.xklibmc_example.client.terrain;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.xkball.xklibmc.XKLibMCClient;
import com.xkball.xklibmc.api.client.b3d.ICloseOnExit;
import com.xkball.xklibmc.client.b3d.IndirectDrawCommand;
import com.xkball.xklibmc.utils.VanillaUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.joml.Vector3f;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@EventBusSubscriber(Dist.CLIENT)
public class TerrainChunkManager implements ICloseOnExit<TerrainChunkManager> {
    
    public static final int BLOCK_SIZE = new ABlock().byteSize();
    public static final TerrainChunkManager INSTANCE = new TerrainChunkManager();
    
    public final Map<ResourceKey<Level>, LevelChunkStorage> storageMap = new HashMap<>();
    public final Queue<Runnable> updateQueue = new ConcurrentLinkedQueue<>();
    
    
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event) {
        if(!Minecraft.getInstance().isPaused() && Minecraft.getInstance().level != null){
            INSTANCE.runUpdateFor10ms();
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
            var s = new LevelChunkStorage(level.dimension());
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
    
    public RenderInfo gatherRenderInfo(Frustum frustum, Vector3f camPos){
        var level = Minecraft.getInstance().level;
        if(level == null) return RenderInfo.empty();
        var storage = this.storageMap.get(level.dimension());
        if(storage == null || storage.gpuBuffer == null) return RenderInfo.empty();
        boolean[] renderTheDir = new boolean[6];
        var cmdList1 = new ArrayList<IndirectDrawCommand>();
        var cmdList2 = new ArrayList<IndirectDrawCommand>();
        for(var chunkStorage : storage.chunkMap.values()){
            var lod = chunkStorage.getLodLevel(camPos);
            var chunkBuffer = chunkStorage.getRenderBuffer(lod);
            if(chunkBuffer == null) continue;
            var aabb = chunkStorage.chunkAABB;
            if(!frustum.isVisible(aabb)) continue;
            for (int i = 0; i < 6; i++) {
                renderTheDir[i] = dirToFace(VanillaUtils.DIRECTIONS[i], aabb, camPos).dot(VanillaUtils.DIRECTIONS[i].getUnitVec3f()) < 0;
            }
            for(var entry_ : chunkBuffer.inChunkMap.int2IntEntrySet()){
                int offset = (int) storage.gpuBuffer.getOffset(entry_.getIntKey()) / BLOCK_SIZE;
                for (int i = 0; i < 6; i++) {
                    if(renderTheDir[i]) {
                        var cmd = new IndirectDrawCommand(6, entry_.getIntValue(),i*6,0, offset);
                        if(lod == 0) cmdList1.add(cmd);
                        else if(lod == 1) cmdList2.add(cmd);
                    }
                }
            }
        }
        var cmdBuffer1 = IndirectDrawCommand.buildCommandList(cmdList1);
        var cmdBuffer2 = IndirectDrawCommand.buildCommandList(cmdList2);
        return new RenderInfo(storage.gpuBuffer.gpuBuffer,cmdList1.size(), cmdBuffer1, cmdList2.size(), cmdBuffer2);
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
    
    public void runUpdateFor10ms() {
        var time = System.nanoTime();
        while (!updateQueue.isEmpty() && System.nanoTime() - time < 10_000_000L){
            Objects.requireNonNull(updateQueue.poll()).run();
        }
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
        this.updateQueue.add(runnable);
    }
    
    public void submitUpdate(ChunkPos chunkPos, boolean force){
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
                storage = new LevelChunkStorage(dimNew);
                this.storageMap.put(dimNew, storage);
            }
            var chunkOld = storage.chunkMap.get(chunkPos);
            if(chunkOld != null && chunkOld.onMemL1 && !force) return;
            var chunk = LevelChunkStorage.COMPLIER_L1.compile(storage,level_,chunkPos);
            if(chunk != null) {
                storage.chunkMap.put(chunkPos,chunk);
                storage.uploadChunk(chunkPos);
            }
        };
        this.updateQueue.add(task);
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
        this.updateQueue.clear();
    }
    
    
    @Override
    public void close() {
        for(var storage : this.storageMap.values()){
            storage.unloadGpu();
        }
    }
    
    public long getMemAlloc(){
        var result = 0L;
        for(var storage : this.storageMap.values()){
            if(storage.gpuBuffer != null){
                result += storage.gpuBuffer.gpuBuffer.size();
            }
        }
        return result;
    }
    
    public long getMemUsed(){
        var result = 0L;
        for(var storage : this.storageMap.values()){
            if(storage.gpuBuffer != null){
                result += storage.gpuBuffer.usedSize();
            }
        }
        return result;
    }
    
    public record RenderInfo(GpuBuffer blockBuffer, int drawCountLod0, GpuBuffer commandLod0, int drawCountLod1, GpuBuffer commandLod1) implements AutoCloseable{
        
        public static RenderInfo empty(){
            return new RenderInfo(null,0,null,0,null);
        }
        
        @Override
        public void close() {
            if(this.commandLod0 != null){
                this.commandLod0.close();
            }
            if(this.commandLod1 != null){
                this.commandLod1.close();
            }
        }
        
        public boolean isEmpty(){
            return this.drawCountLod0 == 0 && drawCountLod1 == 0;
        }
    }
    
}
