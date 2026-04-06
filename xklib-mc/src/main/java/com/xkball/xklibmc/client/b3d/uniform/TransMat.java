package com.xkball.xklibmc.client.b3d.uniform;

import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.xkball.xklibmc.api.client.b3d.ISTD140Writer;
import org.joml.Matrix4f;

public record TransMat(Matrix4f transformMatrix) implements ISTD140Writer {
    
    @Override
    public void calculateSize(Std140SizeCalculator calculator) {
        calculator.putMat4f();
    }
    
    @Override
    public void writeToBuffer(Std140Builder builder) {
        builder.putMat4f(transformMatrix);
    }
}
