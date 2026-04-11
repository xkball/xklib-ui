package com.xkball.xklibmc.client.b3d.buffer;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.logging.LogUtils;
import com.xkball.xklibmc.api.client.b3d.ICloseOnExit;
import com.xkball.xklibmc.utils.ClientUtils;
import it.unimi.dsi.fastutil.ints.Int2IntLinkedOpenHashMap;
import org.slf4j.Logger;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

public class ManagedGpuBuffer implements ICloseOnExit<ManagedGpuBuffer> {
    
    public static final int BASE_SIZE = 4;
    private static final Logger LOGGER = LogUtils.getLogger();
    public final int chunkSize;
    public GpuBuffer gpuBuffer;

    private final Deque<Integer> freeChunks = new ArrayDeque<>();
    private final Int2IntLinkedOpenHashMap idToChunkIndex = new Int2IntLinkedOpenHashMap();
    private int nextId;
    private int capacityChunks;
    
    public ManagedGpuBuffer(int chunkSize) {
        this.chunkSize = chunkSize;
    }
    
    public int put(GpuBuffer buffer){
        return this.put(buffer.slice());
    }
    
    public int put(GpuBufferSlice buffer) {
        var srcSize = buffer.length();
        int id = allocateChunkId();
        try {
            var target = get(id).slice;
            if(srcSize < chunkSize){
                target = target.slice(0, srcSize);
            }
            ClientUtils.getCommandEncoder().copyToBuffer(buffer, target);
        } catch (RuntimeException e) {
            remove(id);
            throw e;
        }
        return id;
    }
    
    public int put(ByteBuffer buffer) {
        int id = allocateChunkId();
        try {
            ClientUtils.getCommandEncoder().writeToBuffer(get(id).slice, buffer);
        } catch (RuntimeException e) {
            remove(id);
            throw e;
        }
        return id;
    }
    
    public Chunk get(int id){
        int chunkIndex = idToChunkIndex.get(id);
        long offset = (long) chunkIndex * (long) chunkSize;
        return new Chunk(id, gpuBuffer.slice(offset, chunkSize));
    }
    
    public long getOffset(int id){
        int chunkIndex = idToChunkIndex.get(id);
        return  (long) chunkIndex * (long) chunkSize;
    }
    
    public void remove(int id){
        int chunkIndex = idToChunkIndex.remove(id);
        freeChunks.addLast(chunkIndex);
    }
    
    public void clear(){
        var iter = idToChunkIndex.keySet().iterator();
        while(iter.hasNext()){
            this.remove(iter.nextInt());
        }
    }
    
    @Override
    public void close() {
        this.gpuBuffer.close();
    }
    
    public record Chunk(int id, GpuBufferSlice slice) {
    
    }
    
    public long usedSize(){
        return (long) idToChunkIndex.size() * chunkSize;
    }

    private int allocateChunkId() {
        ensureInitialized();

        Integer chunkIndex = freeChunks.pollLast();
        if (chunkIndex == null) {
            growToAtLeast(capacityChunks + 1);
            chunkIndex = Objects.requireNonNull(freeChunks.pollLast());
        }
        int id = nextId;
        nextId += 1;
        idToChunkIndex.put(id, (int)chunkIndex);
        return id;
    }

    private void ensureInitialized() {
        if (gpuBuffer != null) {
            return;
        }
        nextId = 0;
        capacityChunks = BASE_SIZE;
        gpuBuffer = ClientUtils.getGpuDevice().createBuffer(() -> "managed_gpu_buffer", GpuBuffer.USAGE_COPY_DST | GpuBuffer.USAGE_COPY_SRC, bytesForChunks(capacityChunks));
        for (int i = 0; i < BASE_SIZE; i++) {
            freeChunks.addLast(i);
        }
    }

    private void growToAtLeast(int minChunks) {
        if (minChunks <= capacityChunks) {
            return;
        }

        int oldCapacity = capacityChunks;
        int newCapacity = oldCapacity;
        while (newCapacity < minChunks) {
            if (newCapacity > Integer.MAX_VALUE / 2) {
                newCapacity = minChunks;
                break;
            }
            newCapacity = Math.max(newCapacity * 2, BASE_SIZE);
        }

        long oldSize = bytesForChunks(oldCapacity);
        long newSize = bytesForChunks(newCapacity);
        GpuBuffer old = gpuBuffer;
        try {
            GpuBuffer next = ClientUtils.getGpuDevice().createBuffer(() -> "managed_gpu_buffer", GpuBuffer.USAGE_COPY_DST | GpuBuffer.USAGE_COPY_SRC | GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_MAP_WRITE, newSize);
            ClientUtils.getCommandEncoder().copyToBuffer(old.slice(0L, oldSize), next.slice(0L, oldSize));
            gpuBuffer = next;
            capacityChunks = newCapacity;
        } catch (RuntimeException e) {
            LOGGER.error("Failed to grow gpu buffer from {} to {} bytes", oldSize, newSize, e);
            throw e;
        }
        try {
            old.close();
        } catch (RuntimeException e) {
            LOGGER.error("Failed to close old gpu buffer", e);
        }

        for (int i = oldCapacity; i < newCapacity; i++) {
            freeChunks.addLast(i);
        }
    }

    private long bytesForChunks(int chunks) {
        return (long) chunks * (long) chunkSize;
    }
}
