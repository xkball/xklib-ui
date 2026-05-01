package com.xkball.xklibmc_example.client.terrain;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.logging.LogUtils;
import com.xkball.xklibmc.api.client.mixin.IExtendedGpuDevice;
import com.xkball.xklibmc.utils.ClientUtils;
import net.minecraft.world.level.ChunkPos;
import org.slf4j.Logger;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TerrainTextureManager implements AutoCloseable {
    
    public static final int VIRTUAL_TEXTURE_SIZE = 16284;
    private static final int CHUNK_SIZE = 16;
    private static final int BYTES_PER_PIXEL = 4;
    private static final int CHUNKS_PER_AXIS = VIRTUAL_TEXTURE_SIZE / CHUNK_SIZE;
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final TextureFormat COLOR_TEXTURE_FORMAT = TextureFormat.RGBA8;
    private static final TextureFormat DEPTH_TEXTURE_FORMAT = TextureFormat.DEPTH32;
    
    private static final int textureUsage = GpuTexture.USAGE_COPY_DST | GpuTexture.USAGE_COPY_SRC | GpuTexture.USAGE_TEXTURE_BINDING;
    private final Map<VirtualTexturePos, VirtualTextures> texturesMap = new ConcurrentHashMap<>();
    
    public TerrainTextureManager() {
    
    }
    
    public boolean uploadChunk(ChunkStorage chunkStorage){
        return this.uploadChunk(chunkStorage.chunkPos, chunkStorage.heightMap);
    }
    
    public boolean uploadChunk(ChunkPos chunkPos, ChunkHeightMap heightMap){
        var uploadInfo = this.getUploadInfo(chunkPos);
        var textures = this.getOrCreateTextures(uploadInfo.texturePos());
        ByteBuffer colorUploadBuffer = null;
        ByteBuffer depthUploadBuffer = null;
        try {
            colorUploadBuffer = MemoryUtil.memAlloc(CHUNK_SIZE * CHUNK_SIZE * BYTES_PER_PIXEL);
            depthUploadBuffer = MemoryUtil.memAlloc(CHUNK_SIZE * CHUNK_SIZE * BYTES_PER_PIXEL);
            for (int z = 0; z < CHUNK_SIZE; z++) {
                for (int x = 0; x < CHUNK_SIZE; x++) {
                    var color = heightMap.getColor(x, z);
                    colorUploadBuffer.putInt(color);
                    var depth = heightMap.get(x, z);
                    depthUploadBuffer.putFloat(depth/1024f);
                }
            }
            colorUploadBuffer.flip();
            depthUploadBuffer.flip();
            ClientUtils.getCommandEncoder().writeToTexture(
                    textures.colorTexture(), colorUploadBuffer,
                    NativeImage.Format.RGBA, 0, 0,
                    uploadInfo.destX(), uploadInfo.destY(),
                    CHUNK_SIZE, CHUNK_SIZE
            );
            ClientUtils.getCommandEncoder().writeToTexture(
                    textures.depthTexture(), depthUploadBuffer,
                    NativeImage.Format.LUMINANCE, 0, 0,
                    uploadInfo.destX(), uploadInfo.destY(),
                    CHUNK_SIZE, CHUNK_SIZE
            );
            return true;
        } finally {
            if(colorUploadBuffer != null){
                MemoryUtil.memFree(colorUploadBuffer);
            }
            if(depthUploadBuffer != null){
                MemoryUtil.memFree(depthUploadBuffer);
            }
        }
    }
    
    public boolean uploadRegion(RegionStorage regionStorage){
        var success = true;
        for(var chunkStorage : regionStorage.chunks()){
            success = this.uploadChunk(chunkStorage) && success;
        }
        return success;
    }
    
    public VirtualTextures getTextures(ChunkPos chunkPos){
        return this.getOrCreateTextures(this.getTexturePos(chunkPos));
    }
    
    private VirtualTextures getOrCreateTextures(VirtualTexturePos materialPos){
        return this.texturesMap.computeIfAbsent(materialPos, this::createTextures);
    }
    
    private VirtualTextures createTextures(VirtualTexturePos materialPos){
        @SuppressWarnings({"removal", "UnstableApiUsage"})
        var device = IExtendedGpuDevice.cast(ClientUtils.getGpuDevice().getBackend());
        var colorTexture = device.xklib$createSparseTexture(
                "terrain_virtual_color_" + materialPos.x() + "_" + materialPos.z(),
                textureUsage, COLOR_TEXTURE_FORMAT, VIRTUAL_TEXTURE_SIZE, VIRTUAL_TEXTURE_SIZE, 1
        );
        var depthTexture = device.xklib$createSparseTexture(
                "terrain_virtual_depth_" + materialPos.x() + "_" + materialPos.z(),
                textureUsage, DEPTH_TEXTURE_FORMAT, VIRTUAL_TEXTURE_SIZE, VIRTUAL_TEXTURE_SIZE, 1
        );
        LOGGER.info("Created virtual terrain textures for material ({},{})", materialPos.x(), materialPos.z());
        return new VirtualTextures(colorTexture, depthTexture);
    }
    
    private VirtualTexturePos getTexturePos(ChunkPos chunkPos){
        return new VirtualTexturePos(
                Math.floorDiv(chunkPos.x(), CHUNKS_PER_AXIS),
                Math.floorDiv(chunkPos.z(), CHUNKS_PER_AXIS)
        );
    }
    
    private ChunkUploadInfo getUploadInfo(ChunkPos chunkPos){
        var materialPos = this.getTexturePos(chunkPos);
        var localChunkX = Math.floorMod(chunkPos.x(), CHUNKS_PER_AXIS);
        var localChunkZ = Math.floorMod(chunkPos.z(), CHUNKS_PER_AXIS);
        return new ChunkUploadInfo(materialPos, localChunkX * CHUNK_SIZE, localChunkZ * CHUNK_SIZE);
    }
    
    public void clear(){
        for(var textures : this.texturesMap.values()){
            textures.colorTexture().close();
            textures.depthTexture().close();
        }
        this.texturesMap.clear();
    }
    
    @Override
    public void close() {
        this.clear();
    }
    
    public record VirtualTextures(GpuTexture colorTexture, GpuTexture depthTexture){}
    
    private record VirtualTexturePos(int x, int z){}
    
    private record ChunkUploadInfo(VirtualTexturePos texturePos, int destX, int destY){}
    
}
