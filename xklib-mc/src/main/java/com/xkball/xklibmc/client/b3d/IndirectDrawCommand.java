package com.xkball.xklibmc.client.b3d;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.xkball.xklibmc.utils.ClientUtils;

import java.nio.ByteBuffer;
import java.util.List;

public record IndirectDrawCommand(int count, int instanceCount, int firstIndex, int baseVertex, int baseInstance) {
  
    public IndirectDrawCommand(int count, int instanceCount){
        this(count, instanceCount, 0, 0, 0);
    }
    
    public void write(ByteBuffer buffer){
        buffer.putInt(count);
        buffer.putInt(instanceCount);
        buffer.putInt(firstIndex);
        buffer.putInt(baseVertex);
        buffer.putInt(baseInstance);
    }
    
    public static GpuBuffer buildCommandList(List<IndirectDrawCommand> commands){
        var byteBuffer = ByteBuffer.allocate(commands.size() * 5 * 4);
        for(var cmd :  commands){
            cmd.write(byteBuffer);
        }
        byteBuffer.flip();
        return ClientUtils.getGpuDevice().createBuffer(() -> "indirect draw command",GpuBuffer.USAGE_MAP_WRITE,byteBuffer);
    }
    
}
