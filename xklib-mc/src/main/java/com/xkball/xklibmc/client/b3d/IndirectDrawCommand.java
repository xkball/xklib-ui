package com.xkball.xklibmc.client.b3d;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.xkball.xklibmc.utils.ClientUtils;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;

public record IndirectDrawCommand(int count, int instanceCount, int firstIndex, int baseVertex, int baseInstance, int px, int pz) {
  
    public IndirectDrawCommand(int count, int instanceCount, int firstIndex, int baseVertex, int baseInstance){
        this(count, instanceCount, firstIndex, baseVertex, baseInstance, 0, 0);
    }
    
    public IndirectDrawCommand(int count, int instanceCount){
        this(count, instanceCount, 0, 0, 0, 0, 0);
    }
    
    public void write(ByteBuffer buffer){
        buffer.putInt(count);
        buffer.putInt(instanceCount);
        buffer.putInt(firstIndex);
        buffer.putInt(baseVertex);
        buffer.putInt(baseInstance);
        buffer.putInt(px);
        buffer.putInt(pz);
    }
    
    public static GpuBuffer buildCommandList(Collection<IndirectDrawCommand> commands){
        if(commands.isEmpty()){
            return ClientUtils.getGpuDevice().createBuffer(() -> "indirect draw command", GpuBuffer.USAGE_COPY_DST,28);
        }
        var byteBuffer = MemoryUtil.memAlloc(commands.size() * 7 * 4);
        for(var cmd :  commands){
            cmd.write(byteBuffer);
        }
        byteBuffer.flip();
        var result = ClientUtils.getGpuDevice().createBuffer(() -> "indirect draw command", GpuBuffer.USAGE_COPY_DST,byteBuffer);
        MemoryUtil.memFree(byteBuffer);
        return result;
    }
    
}
