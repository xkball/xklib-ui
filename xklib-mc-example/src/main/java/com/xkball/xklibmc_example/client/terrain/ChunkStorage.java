package com.xkball.xklibmc_example.client.terrain;

import com.mojang.blaze3d.vertex.TlsfAllocator;
import com.mojang.blaze3d.vertex.UberGpuBuffer;
import com.xkball.xklib.x3d.backend.vertex.BufferBuilder;
import com.xkball.xklib.x3d.backend.vertex.VertexFormat;
import com.xkball.xklib.x3d.backend.vertex.VertexFormats;
import com.xkball.xklibmc.annotation.NonNullByDefault;
import com.xkball.xklibmc.utils.ClientUtils;
import com.xkball.xklibmc.utils.VanillaUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.AABB;
import org.joml.GeometryUtils;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;

import java.util.ArrayList;
import java.util.List;

@NonNullByDefault
public class ChunkStorage {
    
    public final ChunkPos chunkPos;
    public final LevelChunkStorage parent;
    public final ChunkStorageData data;
    /**
     * 虽然没有初始值, 但是通过文件加载或编译产生的对象, 这两个字段均不为null, 为null则说明产生非法状态
     */
    @SuppressWarnings("NotNullFieldNotInitialized")
    public AABB chunkAABB;
    @SuppressWarnings("NotNullFieldNotInitialized")
    public ChunkHeightMap heightMap;
    public boolean dirty = false;
    
    public ChunkStorage(ChunkPos chunkPos, LevelChunkStorage parent) {
        this.chunkPos = chunkPos;
        this.parent = parent;
        this.data = new ChunkStorageData(chunkPos, new ArrayList<>());
    }
    
    public int getLodLevel(Vector3f camPos){
        var lenSqr = camPos.distanceSquared(chunkAABB.getCenter().toVector3f());
        if(lenSqr < 500 * 500){
            return 0;
        }
        else if(lenSqr < 1000 * 1000){
            return 1;
        }
        return 2;
    }
    
    public int facesCountByLod(int lodLevel){
        if(lodLevel == 1) return 64;
        if(lodLevel == 2) return 16;
        return 0;
    }
    
    public TlsfAllocator.@Nullable Allocation getRenderBuffer(int lodLevel){
        if(lodLevel <= 2) return this.parent.gpuBufferByLod.getAllocation(new ChunkPosLod(this.chunkPos, lodLevel));
        return null;
    }
    
    public void markDirty(){
        this.dirty = true;
        this.parent.markDirty();
    }
    
    public void writeData(List<ABlock.ABlockData> data){
        this.data.data.clear();
        this.data.data.addAll(data);
    }
    
    public void releaseData(){
        this.data.data.clear();
    }
    
    public void uploadGpuLod12(){
        removeFromUberBuffer(this.parent.gpuBufferByLod, new ChunkPosLod(this.chunkPos,1));
        removeFromUberBuffer(this.parent.gpuBufferByLod, new ChunkPosLod(this.chunkPos,2));
        this.uploadGpuLod(1,2);
        this.uploadGpuLod(2,4);
    }
    
    private void uploadGpuLod(int lodLevel, int step){
        var x0 = chunkPos.getMinBlockX();
        var z0 = chunkPos.getMinBlockZ();
        var minY = this.parent.minHeight;
        try (var builder = BufferBuilder.start(VertexFormat.Mode.TRIANGLES, VertexFormats.POSITION_NORMAL_COLOR)){
            for (int x = 0; x < 16; x+=step) {
                for (int z = 0; z < 16; z+=step) {
                    var h = this.heightMap.get(x,z);
                    if(h == minY){
                        for (int i = 0; i < 6; i++) {
                            builder.addVertex(0,0,0).setNormal(0,0,0).setColor(0);
                        }
                        continue;
                    }
                    var c = this.heightMap.getColor(x,z);
                    var nx = x + step;
                    var nz = z + step;
                    int h01, h10, h11, c01, c10, c11;
                    if(nx < 16 && nz < 16){
                        h01 = this.heightMap.get(nx,z);  c01 = this.heightMap.getColor(nx,z);
                        h10 = this.heightMap.get(x,nz);  c10 = this.heightMap.getColor(x,nz);
                        h11 = this.heightMap.get(nx,nz); c11 = this.heightMap.getColor(nx,nz);
                    }
                    else{
                        h01 = this.parent.getHeight(x0 + nx, z0 + z);  c01 = this.parent.getColor(x0 + nx, z0 + z);
                        h10 = this.parent.getHeight(x0 + x, z0 + nz);  c10 = this.parent.getColor(x0 + x, z0 + nz);
                        h11 = this.parent.getHeight(x0 + nx, z0 + nz); c11 = this.parent.getColor(x0 + nx, z0 + nz);
                    }
                    if(h01 == minY) h01 = h;
                    if(h10 == minY) h10 = h;
                    if(h11 == minY) h11 = h;
                    var p0 = new Vector3f(x0 + x, h, z0 + z);
                    var p1 = new Vector3f(x0 + x, h10, z0 + nz);
                    var p2 = new Vector3f(x0 + nx, h01, z0 + z);
                    var p3 = new Vector3f(x0 + nx, h11, z0 + nz);
                    var normal = new Vector3f();
                    var normal1 = new Vector3f();
                    GeometryUtils.normal(p0, p1, p2, normal);
                    GeometryUtils.normal(p2, p1, p3, normal1);
                    builder.addVertex(p0).setNormal(normal).setColor(c);
                    builder.addVertex(p1).setNormal(normal).setColor(c10);
                    builder.addVertex(p2).setNormal(normal).setColor(c01);
                    builder.addVertex(p2).setNormal(normal1).setColor(c01);
                    builder.addVertex(p1).setNormal(normal1).setColor(c10);
                    builder.addVertex(p3).setNormal(normal1).setColor(c11);
                }
            }
            var buffer = builder.build();
            var gpuBuffer = this.parent.gpuBufferByLod;
            var success = gpuBuffer.addAllocation(new ChunkPosLod(this.chunkPos,lodLevel),(_) -> {}, buffer);
            if(!success){
                gpuBuffer.uploadStagedAllocations(ClientUtils.getGpuDevice(), ClientUtils.getCommandEncoder());
                gpuBuffer.addAllocation(new ChunkPosLod(this.chunkPos,lodLevel),(_) -> {}, buffer);
            }
        }
    }
    
    public void uploadGpuLod0(){
        for(var b : this.parent.gpuBufferByFace.values()){
            removeFromUberBuffer(b, this.chunkPos);
        }
        var l1List = new ArrayList<ABlock>();
        var x0 = chunkPos.getMinBlockX();
        var z0 = chunkPos.getMinBlockZ();
        for(var b : this.data.data()){
            l1List.add(b.toABlock(x0,z0));
        }
        for(var dir : VanillaUtils.DIRECTIONS){
            var list = new ArrayList<ABlock>();
            for (ABlock a : l1List) {
                if ((a.mask() & (1 << dir.get3DDataValue())) > 0) {
                    list.add(a);
                }
            }
            
            var buffer = MemoryUtil.memAlloc(list.size() * 16);
            for(var ab : list){
                buffer.putFloat(ab.x());
                buffer.putFloat(ab.y());
                buffer.putFloat(ab.z());
                buffer.putInt(ab.color());
            }
            buffer.flip();
            var gpuBuffer = this.parent.gpuBufferByFace.get(dir);
            var success = gpuBuffer.addAllocation(this.chunkPos,(_) -> {}, buffer);
            if(!success){
                gpuBuffer.uploadStagedAllocations(ClientUtils.getGpuDevice(), ClientUtils.getCommandEncoder());
                gpuBuffer.addAllocation(this.chunkPos,(_) -> {}, buffer);
            }
            MemoryUtil.memFree(buffer);
        }
    }
    
    public void unloadGpu(){
        for(var b : this.parent.gpuBufferByFace.values()){
            removeFromUberBuffer(b, this.chunkPos);
        }
        removeFromUberBuffer(this.parent.gpuBufferByLod, new ChunkPosLod(this.chunkPos,1));
        removeFromUberBuffer(this.parent.gpuBufferByLod, new ChunkPosLod(this.chunkPos,2));
    }
    
    private static <T> void removeFromUberBuffer(UberGpuBuffer<T> buffer, T key){
        if(buffer.getAllocation(key) == null) return;
        buffer.removeAllocation(key);
        buffer.uploadStagedAllocations(ClientUtils.getGpuDevice(), ClientUtils.getCommandEncoder());
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
