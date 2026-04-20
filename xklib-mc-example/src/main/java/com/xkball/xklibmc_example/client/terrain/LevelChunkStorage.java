package com.xkball.xklibmc_example.client.terrain;

import com.mojang.blaze3d.GraphicsWorkarounds;
import com.mojang.blaze3d.vertex.UberGpuBuffer;
import com.mojang.logging.LogUtils;
import com.xkball.xklibmc.client.b3d.buffer.ManagedGpuBuffer;
import com.xkball.xklibmc.utils.ClientUtils;
import com.xkball.xklibmc.utils.VanillaUtils;
import com.xkball.xklibmc_example.utils.CodecUtils;
import io.netty.buffer.Unpooled;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.fml.loading.FMLPaths;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class LevelChunkStorage {
    
    public static final int VERSION = 1;
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final int BLOCK_SIZE = new ABlock().byteSize();
    public static final ChunkComplier COMPLIER =  new ChunkComplier();
    
    public final String saveName;
    public final ResourceKey<Level> dimension;
    public @Nullable ManagedGpuBuffer gpuBuffer;
    public final EnumMap<Direction, UberGpuBuffer<ChunkPos>> gpuBufferByFace = new EnumMap<>(Direction.class);
    public final Map<ChunkPos, ChunkStorage> chunkMap = new LinkedHashMap<>();
    public boolean dirty = false;
    
    public LevelChunkStorage(ResourceKey<Level> dimension) {
        this.dimension = dimension;
        this.gpuBuffer = createGpuBuffer();
        this.saveName = ClientUtils.getSaveOrServerName();
        var gpuDevice = ClientUtils.getGpuDevice();
        var gpuWorkaround = GraphicsWorkarounds.get(gpuDevice);
        for(var dir : VanillaUtils.DIRECTIONS){
            gpuBufferByFace.put(dir, new UberGpuBuffer<>("terrain_"+dir,64, 134217728, 16, gpuDevice, 33554432, gpuWorkaround));
        }
    }
    
    public void unloadChunk(ChunkPos chunkPos) {
        if(gpuBuffer == null) return;
        var chunkStorage = this.chunkMap.get(chunkPos);
        if(chunkStorage != null) {
            chunkStorage.unloadGpu();
        }
    }
    
    public void uploadChunk(ChunkPos chunkPos) {
        if(gpuBuffer == null) return;
        this.unloadChunk(chunkPos);
        var chunkStorage = this.chunkMap.get(chunkPos);
        if(chunkStorage == null) return;
        chunkStorage.uploadGpu();
    }
    
    public void markDirty(){
        this.dirty = true;
    }
    
    public void unloadGpu(){
        if(gpuBuffer != null){
            this.gpuBuffer.close();
            this.gpuBuffer = null;
        }
        for(var chunkStorage : chunkMap.values()){
            chunkStorage.bufferL1 = null;
            chunkStorage.onGpu = false;
        }
        this.markDirty();
    }
    
    public void saveFile(){
        if(!this.dirty) return;
        this.dirty = false;
        for(var entry : chunkMap.entrySet()){
            if(entry.getValue().dirty) this.saveRegion(entry.getKey());
        }
    }
    
    public void loadFile(){
        if(this.gpuBuffer == null){
            this.gpuBuffer = createGpuBuffer();
        }
        var dir = this.getDirectory().toFile();
        if(!dir.exists() ||!dir.isDirectory()) return;
        var files = dir.listFiles();
        if(files == null) return;
        for(var file : files){
            Thread.startVirtualThread(() -> {
                var name = file.getName();
                var n = name.split(",");
                if(n.length != 2) return;
                int x;
                int z;
                try {
                    x = Integer.parseInt(n[0]);
                    z = Integer.parseInt(n[1]);
                } catch(NumberFormatException e){
                    return;
                }
                LOGGER.info("Loading map at {}, chunk from ({},{}) to ({},{})",this.dimension,x << 4,z << 4,(x << 4) + 16,(z << 4) + 16);
                try {
                    var bytes = Files.readAllBytes(file.toPath());
                    bytes = VanillaUtils.unGzip(bytes);
                    var byteBuf = Unpooled.buffer(bytes.length);
                    byteBuf.writeBytes(bytes);
                    var version = byteBuf.readInt();
                    if(version != VERSION){
                        LOGGER.error("Version mismatch");
                        return;
                    }
                    for (int dx = 0; dx < 16; dx++) {
                        for (int dz = 0; dz < 16; dz++) {
                            if(byteBuf.readBoolean()){
                                var chunkPos = new ChunkPos((x << 4) + dx, (z << 4) + dz);
                                var storage = new ChunkStorage(chunkPos, this);
                                storage.chunkAABB = CodecUtils.AABB_STREAM_CODEC.decode(byteBuf);
                                storage.heightMap = ChunkHeightMap.STREAM_CODEC.decode(byteBuf);
                                var data = ChunkStorage.ChunkStorageData.STREAM_CODEC.decode(byteBuf);
                                assert data.pos().equals(chunkPos);
                                storage.onDisk = true;
                                storage.onMemL1 = true;
                                storage.writeData(data.data());
                                TerrainChunkManager.INSTANCE.submitTask(
                                        () -> {
                                            if(this.chunkMap.containsKey(chunkPos)) return;
                                            this.chunkMap.put(chunkPos, storage);
                                            this.uploadChunk(chunkPos);
                                        }
                                );
                            }
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("Failed to load chunk from file.", e);
                }
            });

        }
    }
    
    public void saveRegion(ChunkPos chunkPos){
        var x0 = chunkPos.x() >> 4;
        var z0 = chunkPos.z() >> 4;
        var file = this.getFile(chunkPos).toFile();
        if(!file.exists()){
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            }catch (IOException e){
                throw new RuntimeException(e);
            }
        }
        LOGGER.info("Saving map at {}, chunk from ({},{}) to ({},{})",this.dimension,x0 << 4,z0 << 4,(x0 << 4) + 16,(z0 << 4) + 16);
        try (var output = new FileOutputStream(file)){
            var byteBuf = Unpooled.buffer();
            byteBuf.writeInt(1);
            for (int dx = 0; dx < 16; dx++) {
                for (int dz = 0; dz < 16; dz++) {
                    var pos = new ChunkPos((x0 << 4) + dx, (z0 << 4) + dz);
                    var storage = this.chunkMap.get(pos);
                    if(storage != null){
                        storage.dirty = false;
                        storage.onDisk = true;
                        byteBuf.writeBoolean(true);
                        CodecUtils.AABB_STREAM_CODEC.encode(byteBuf, storage.chunkAABB);
                        ChunkHeightMap.STREAM_CODEC.encode(byteBuf, storage.heightMap);
                        ChunkStorage.ChunkStorageData.STREAM_CODEC.encode(byteBuf,storage.data);
                    }
                    else byteBuf.writeBoolean(false);
                }
            }
            output.write(VanillaUtils.gzip(byteBuf.array(), 0,  byteBuf.readableBytes()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public Path getFile(ChunkPos chunkPos){
        var x0 = chunkPos.x() >> 4;
        var z0 = chunkPos.z() >> 4;
        return getDirectory().resolve(x0+","+z0);
    }
    
    public Path getDirectory(){
        var dim = dimension.identifier();
        return FMLPaths.GAMEDIR.get().resolve("x3dmap").resolve(this.saveName).resolve(dim.getNamespace()).resolve(dim.getPath());
    }
    
    private ManagedGpuBuffer createGpuBuffer(){
        return new ManagedGpuBuffer(BLOCK_SIZE * 16 * 16);
    }
}
