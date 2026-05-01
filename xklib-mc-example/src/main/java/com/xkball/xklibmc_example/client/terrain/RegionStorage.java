package com.xkball.xklibmc_example.client.terrain;

import com.xkball.xklibmc.utils.VanillaUtils;
import com.xkball.xklibmc_example.utils.CodecUtils;
import io.netty.buffer.Unpooled;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class RegionStorage {
    
    public static final int REGION_SHIFT = 5;
    public static final int REGION_SIZE = 1 << REGION_SHIFT;
    
    public final AABB aabb;
    public final RegionPos regionPos;
    private final Map<ChunkPos, ChunkStorage> chunkMap = new LinkedHashMap<>();
    
    public RegionStorage(RegionPos regionPos, int minHeight, int maxHeight) {
        this.regionPos = regionPos;
        var minX = regionPos.getMinX();
        var minZ = regionPos.getMinZ();
        this.aabb = new AABB(minX, minHeight, minZ, minX + 512, maxHeight, minZ + 512);
    }
    
    public static RegionPos toRegionPos(ChunkPos chunkPos){
        return RegionPos.ofChunk(chunkPos);
    }
    
    public @Nullable ChunkStorage getChunk(ChunkPos chunkPos){
        return this.chunkMap.get(chunkPos);
    }
    
    public @Nullable ChunkStorage putChunk(ChunkStorage chunkStorage){
        return this.chunkMap.put(chunkStorage.chunkPos, chunkStorage);
    }
    
    public boolean containsChunk(ChunkPos chunkPos){
        return this.chunkMap.containsKey(chunkPos);
    }
    
    public Collection<ChunkStorage> chunks(){
        return this.chunkMap.values();
    }
    
    public boolean hasDirtyChunk(){
        for(var chunkStorage : this.chunkMap.values()){
            if(chunkStorage.dirty){
                return true;
            }
        }
        return false;
    }
    
    public Path getFile(Path directory){
        return directory.resolve(this.regionPos.x()+","+this.regionPos.z());
    }
    
    public void saveToFile(Path directory, LevelChunkStorage levelStorage, Logger logger){
        var file = this.getFile(directory).toFile();
        if(!file.exists()){
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            }catch (IOException e){
                logger.error("Failed to create region file {}", file.getAbsolutePath(), e);
                throw new RuntimeException(e);
            }
        }
        var x0 = this.regionPos.x();
        var z0 = this.regionPos.z();
        logger.info("Saving map at {}, chunk from ({},{}) to ({},{})", levelStorage.dimension, x0 << REGION_SHIFT, z0 << REGION_SHIFT, (x0 << REGION_SHIFT) + REGION_SIZE, (z0 << REGION_SHIFT) + REGION_SIZE);
        try (var output = new FileOutputStream(file)){
            var byteBuf = Unpooled.buffer();
            byteBuf.writeInt(LevelChunkStorage.VERSION);
            for (int dx = 0; dx < REGION_SIZE; dx++) {
                for (int dz = 0; dz < REGION_SIZE; dz++) {
                    var pos = new ChunkPos((x0 << REGION_SHIFT) + dx, (z0 << REGION_SHIFT) + dz);
                    var storage = this.getChunk(pos);
                    if(storage != null){
                        storage.dirty = false;
                        byteBuf.writeBoolean(true);
                        CodecUtils.AABB_STREAM_CODEC.encode(byteBuf, storage.chunkAABB);
                        ChunkHeightMap.STREAM_CODEC.encode(byteBuf, storage.heightMap);
                        ChunkStorage.ChunkStorageData.STREAM_CODEC.encode(byteBuf,storage.data);
                    }
                    else{
                        byteBuf.writeBoolean(false);
                    }
                }
            }
            output.write(VanillaUtils.gzip(byteBuf.array(), 0, byteBuf.readableBytes()));
        } catch (IOException e) {
            logger.error("Failed to save region file {}", file.getAbsolutePath(), e);
            throw new RuntimeException(e);
        }
    }
    
    public static @Nullable RegionStorage loadFromFile(Path path, LevelChunkStorage levelStorage, Logger logger){
        var fileName = path.getFileName().toString();
        var n = fileName.split(",");
        if(n.length != 2){
            return null;
        }
        int x;
        int z;
        try {
            x = Integer.parseInt(n[0]);
            z = Integer.parseInt(n[1]);
        } catch(NumberFormatException e){
            return null;
        }
        logger.info("Loading map at {}, chunk from ({},{}) to ({},{})", levelStorage.dimension, x << REGION_SHIFT, z << REGION_SHIFT, (x << REGION_SHIFT) + REGION_SIZE, (z << REGION_SHIFT) + REGION_SIZE);
        try {
            var bytes = Files.readAllBytes(path);
            bytes = VanillaUtils.unGzip(bytes);
            var byteBuf = Unpooled.buffer(bytes.length);
            byteBuf.writeBytes(bytes);
            var version = byteBuf.readInt();
            if(version != LevelChunkStorage.VERSION){
                logger.error("Version mismatch");
                return null;
            }
            var regionStorage = new RegionStorage(new RegionPos(x, z), levelStorage.minHeight,  levelStorage.maxHeight);
            for (int dx = 0; dx < REGION_SIZE; dx++) {
                for (int dz = 0; dz < REGION_SIZE; dz++) {
                    if(byteBuf.readBoolean()){
                        var chunkPos = new ChunkPos((x << REGION_SHIFT) + dx, (z << REGION_SHIFT) + dz);
                        var storage = new ChunkStorage(chunkPos, levelStorage);
                        storage.chunkAABB = CodecUtils.AABB_STREAM_CODEC.decode(byteBuf);
                        storage.heightMap = ChunkHeightMap.STREAM_CODEC.decode(byteBuf);
                        var data = ChunkStorage.ChunkStorageData.STREAM_CODEC.decode(byteBuf);
                        assert data.pos().equals(chunkPos);
                        storage.writeData(data.data());
                        regionStorage.putChunk(storage);
                    }
                }
            }
            return regionStorage;
        } catch (Exception e) {
            logger.error("Failed to load region file {}", path, e);
            return null;
        }
    }
    
}
