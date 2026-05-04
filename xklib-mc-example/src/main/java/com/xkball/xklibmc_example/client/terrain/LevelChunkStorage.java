package com.xkball.xklibmc_example.client.terrain;

import com.mojang.blaze3d.GraphicsWorkarounds;
import com.mojang.blaze3d.vertex.UberGpuBuffer;
import com.mojang.logging.LogUtils;
import com.xkball.xklibmc.client.b3d.buffer.ManagedGpuBuffer;
import com.xkball.xklibmc.utils.ClientUtils;
import com.xkball.xklibmc.utils.VanillaUtils;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.fml.loading.FMLPaths;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class LevelChunkStorage {
    
    public static final int VERSION = 1;
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final ChunkComplier COMPLIER =  new ChunkComplier();
    
    public final int minHeight;
    public final int maxHeight;
    public final String saveName;
    public final boolean compatibleMode;
    public final ResourceKey<Level> dimension;
    public UberGpuBuffer<ChunkPos> gpuBufferBlockData;
    public EnumMap<Direction, UberGpuBuffer<ChunkPos>> gpuBufferByFace = new EnumMap<>(Direction.class);
    public UberGpuBuffer<ChunkPosLod> gpuBufferByLodFullMesh;
    public TerrainTextureManager terrainTextureManager = new TerrainTextureManager();
    private final List<UberGpuBuffer<?>> gpuBuffers = new ArrayList<>();
    public final Map<RegionPos, RegionStorage> regionMap = new LinkedHashMap<>();
    public boolean dirty = false;
    
    public LevelChunkStorage(ResourceKey<Level> dimension, int minHeight, int maxHeight, boolean compatibleMode) {
        this.dimension = dimension;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.compatibleMode = compatibleMode;
        this.saveName = ClientUtils.getSaveOrServerName();
        this.createBuffer();
    }
    
    public void createBuffer(){
        this.unloadGpu();
        this.gpuBuffers.clear();
        var gpuDevice = ClientUtils.getGpuDevice();
        var gpuWorkaround = GraphicsWorkarounds.get(gpuDevice);
        this.gpuBufferBlockData = new UberGpuBuffer<>("terrain_block_data", 64, 64 * 1024 * 1024, 16, gpuDevice, 8 * 1024 * 1024, gpuWorkaround);
        this.gpuBuffers.add(this.gpuBufferBlockData);
        for(var dir : VanillaUtils.DIRECTIONS){
            this.gpuBufferByFace.put(dir, new UberGpuBuffer<>("terrain_"+dir+"_index",64, 64 * 1024 * 1024, 4, gpuDevice, 8 * 1024 * 1024, gpuWorkaround));
        }
        this.gpuBufferByLodFullMesh = new UberGpuBuffer<>("terrain_lod",64, 64 * 1024 * 1024, 20/*DefaultVertexFormat.POSITION_COLOR_NORMAL.getVertexSize()*/, gpuDevice, 8 * 1024 * 1024, gpuWorkaround);
        this.gpuBuffers.addAll(gpuBufferByFace.values());
        this.gpuBuffers.add(gpuBufferByLodFullMesh);
    }
    
    public List<UberGpuBuffer<?>> getGpuBuffers(){
        return this.gpuBuffers;
    }
    
    public void markDirty(){
        this.dirty = true;
    }
    
    public void unloadGpu(){
        for(var b : this.gpuBuffers){
            b.close();
        }
        this.terrainTextureManager.close();
        this.markDirty();
    }
    
    public void saveFile(){
        if(!this.dirty) return;
        this.dirty = false;
        for(var entry : this.regionMap.entrySet()){
            if(entry.getValue().hasDirtyChunk()){
                this.saveRegion(entry.getKey());
            }
        }
    }
    
    public int getHeight(int x, int z){
        var chunkPos = new ChunkPos(x >> 4, z >> 4);
        var chunk = this.getChunk(chunkPos);
        if(chunk == null) return -64;
        return chunk.heightMap.get(x,z);
    }
    
    public int getColor(int x, int z){
        var chunkPos = new ChunkPos(x >> 4, z >> 4);
        var chunk = this.getChunk(chunkPos);
        if(chunk == null) return 0;
        return chunk.heightMap.getColor(x,z);
    }
    
    public void loadFile(){
        var dir = this.getDirectory().toFile();
        if(!dir.exists() ||!dir.isDirectory()) return;
        var files = dir.listFiles();
        if(files == null) return;
        var taskList = new ArrayList<CompletableFuture<Void>>();
        for(var file : files){
            taskList.add(CompletableFuture.runAsync(() -> {
                var regionStorage = RegionStorage.loadFromFile(file.toPath(), this, LOGGER);
                if(regionStorage == null){
                    return;
                }
                TerrainChunkManager.INSTANCE.taskQueue.submitMain(() -> {
                    for(var chunkStorage : regionStorage.chunks()){
                        if(this.containsChunk(chunkStorage.chunkPos)){
                            continue;
                        }
                        this.putChunk(chunkStorage);
                        chunkStorage.uploadGpu0();
                    }
                });
            }, TerrainChunkManager.INSTANCE.taskQueue.workers));

        }
        var task = CompletableFuture.allOf(taskList.toArray(CompletableFuture[]::new));
        task.thenRunAsync(() -> {
            TerrainChunkManager.INSTANCE.taskQueue.submitMain(() -> {
                for(var chunkStorage : this.getChunks()){
                    TerrainChunkManager.INSTANCE.taskQueue.submitMain( () -> {
                        if(!this.containsChunk(chunkStorage.chunkPos)) return;
                        if(compatibleMode){
                            chunkStorage.uploadGpuLodFullMesh();
                        }
                        else chunkStorage.uploadToTexture();
                    });
                }
            });
        },TerrainChunkManager.INSTANCE.taskQueue.workers);
    }
    
    public void saveRegion(RegionPos regionPos){
        var regionStorage = this.regionMap.get(regionPos);
        if(regionStorage == null){
            return;
        }
        regionStorage.saveToFile(this.getDirectory(), this, LOGGER);
    }
    
    public RegionStorage getOrCreateRegion(RegionPos regionPos){
        return this.regionMap.computeIfAbsent(regionPos, rp -> new RegionStorage(rp, this.minHeight, this.maxHeight));
    }
    
    public RegionStorage getRegion(RegionPos regionPos){
        return this.regionMap.get(regionPos);
    }
    
    public @Nullable ChunkStorage getChunk(ChunkPos chunkPos){
        var region = this.getRegion(RegionStorage.toRegionPos(chunkPos));
        if(region == null){
            return null;
        }
        return region.getChunk(chunkPos);
    }
    
    public boolean containsChunk(ChunkPos chunkPos){
        return this.getChunk(chunkPos) != null;
    }
    
    public void putChunk(ChunkStorage chunkStorage){
        this.getOrCreateRegion(RegionStorage.toRegionPos(chunkStorage.chunkPos)).putChunk(chunkStorage);
    }
    
    public List<ChunkStorage> getChunks(){
        var list = new ArrayList<ChunkStorage>();
        for(var regionStorage : this.regionMap.values()){
            list.addAll(regionStorage.chunks());
        }
        return list;
    }
    
    public Path getDirectory(){
        var dim = dimension.identifier();
        return FMLPaths.GAMEDIR.get().resolve("x3dmap").resolve(this.saveName).resolve(dim.getNamespace()).resolve(dim.getPath());
    }
    
}
