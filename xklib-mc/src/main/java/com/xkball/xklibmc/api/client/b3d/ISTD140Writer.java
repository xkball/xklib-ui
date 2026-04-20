package com.xkball.xklibmc.api.client.b3d;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.xkball.xklibmc.utils.ClientUtils;
import org.lwjgl.system.MemoryUtil;

import java.util.List;

public interface ISTD140Writer {
    
    void calculateSize(Std140SizeCalculator calculator);
    
    void writeToBuffer(Std140Builder builder);
    
    default GpuBuffer buildStd140Block(){
      return batchBuildStd140Block(List.of(this));
    }
    
    default int byteSize(){
        var calculator = new Std140SizeCalculator();
        this.calculateSize(calculator);
        return calculator.get();
    }
    
    static <T extends ISTD140Writer> GpuBuffer batchBuildStd140Block(List<T> list){
        if(list.isEmpty()){
            return ClientUtils.getGpuDevice().createBuffer(() -> "std140buffer",GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_COPY_SRC,1);
        }
        var calculator = new Std140SizeCalculator();
        for(var it :  list){
            it.calculateSize(calculator);
        }
        var size = calculator.get();
        var buffer = MemoryUtil.memAlloc(size);
        var builder = Std140Builder.intoBuffer(buffer);
        for(var it : list){
            it.writeToBuffer(builder);
        }
        var result = ClientUtils.getGpuDevice().createBuffer(() -> "std140buffer",GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_COPY_SRC,builder.get());
        MemoryUtil.memFree(buffer);
        return result;
    }
}
