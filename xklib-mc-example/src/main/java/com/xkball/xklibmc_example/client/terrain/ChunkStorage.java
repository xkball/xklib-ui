package com.xkball.xklibmc_example.client.terrain;

import com.xkball.xklibmc.annotation.NonNullByDefault;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.AABB;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@NonNullByDefault
public class ChunkStorage {
    
    public final ChunkPos chunkPos;
    public final LevelChunkStorage parent;
    public final ChunkStorageData data;
    public @Nullable TerrainChunkBuffer bufferL1;
    public @Nullable TerrainChunkBuffer bufferL2;
    /**
     * 虽然没有初始值, 但是通过文件加载或编译产生的对象, 这两个字段均不为null, 为null则说明产生非法状态
     */
    @SuppressWarnings("NotNullFieldNotInitialized")
    public AABB chunkAABB;
    @SuppressWarnings("NotNullFieldNotInitialized")
    public ChunkHeightMap heightMap;
    public boolean dirty = false;
    public boolean onDisk = false;
    public boolean onMemL1 = false;
    public boolean onGpu = false;
    
    public ChunkStorage(ChunkPos chunkPos, LevelChunkStorage parent) {
        this.chunkPos = chunkPos;
        this.parent = parent;
        this.data = new ChunkStorageData(chunkPos, new ArrayList<>());
    }
    
    public int getLodLevel(Vector3f camPos){
        var lenSqr = camPos.distanceSquared(chunkAABB.getCenter().toVector3f());
        var limSqr = 800 * 800;
        return limSqr > lenSqr ? 0 : 1;
    }
    
    public @Nullable TerrainChunkBuffer getRenderBuffer(int lodLevel){
        if(lodLevel == 0) return this.bufferL1;
        if(lodLevel == 1) return this.bufferL2;
        return null;
    }
    
    public void markDirty(){
        this.dirty = true;
        this.parent.markDirty();
    }
    
    public void writeData(List<ABlock.ABlockData> data){
        this.data.data.clear();
        this.data.data.addAll(data);
        this.onMemL1 = true;
    }
    
    public void releaseData(){
        this.onMemL1 = false;
        this.data.data.clear();
    }
    
    public void uploadGpu(){
        if (this.parent.gpuBuffer == null) {
            return;
        }
        this.unloadGpu();
        var l1List = new ArrayList<ABlock>();
        var l2List = new ArrayList<ABlock>();
        var x0 = chunkPos.getMinBlockX();
        var z0 = chunkPos.getMinBlockZ();
        for(var b : this.data.data()){
            l1List.add(b.toABlock(x0,z0));
        }
        for (int i = 0; i < 256; i++) {
            var x = x0 + (i >> 4);
            var z = z0 + (i & 0xF);
            if((heightMap.color[i] & 0xFF000000) != 0){
                l2List.add(new ABlock(x, heightMap.heightMap[i],z,heightMap.color[i]));
            }
        }
        this.bufferL1 = new TerrainChunkBuffer(this.parent.gpuBuffer, l1List);
        this.bufferL2 = new TerrainChunkBuffer(this.parent.gpuBuffer, l2List);
        this.onGpu = true;
    }
    
    public void unloadGpu(){
        this.onGpu = false;
        if((bufferL1 == null && bufferL2 == null) || this.parent.gpuBuffer == null) return;
        if(bufferL1 != null){
            for(var entry : bufferL1.inChunkMap.int2IntEntrySet()) {
                this.parent.gpuBuffer.remove(entry.getIntKey());
            }
        }
        if(bufferL2 != null){
            for(var entry : bufferL2.inChunkMap.int2IntEntrySet()) {
                this.parent.gpuBuffer.remove(entry.getIntKey());
            }
        }
    }
    
    public record ChunkStorageData(ChunkPos pos, List<ABlock.ABlockData> data){
        
        public static final StreamCodec<ByteBuf, ChunkStorageData> STREAM_CODEC = StreamCodec.composite(
                ChunkPos.STREAM_CODEC,
                ChunkStorageData::pos,
                ByteBufCodecs.collection(ArrayList::new, ABlock.ABlockData.STREAM_CODEC),
                ChunkStorageData::data,
                ChunkStorageData::new
        );
    }
    
}
