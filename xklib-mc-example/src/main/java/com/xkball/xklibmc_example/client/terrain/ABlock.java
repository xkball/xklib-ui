package com.xkball.xklibmc_example.client.terrain;

import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.xkball.xklibmc.api.client.b3d.ISTD140Writer;
import net.minecraft.core.BlockPos;

public record ABlock(BlockPos pos, int color) implements ISTD140Writer {
    
    public ABlock(){
        this(new BlockPos(0,0,0),0);
    }
    
    @Override
    public void calculateSize(Std140SizeCalculator calculator) {
        calculator.putVec3().putInt();
    }
    
    @Override
    public void writeToBuffer(Std140Builder builder) {
        builder.putVec3(pos.getCenter().toVector3f());
        builder.putInt(color);
    }
}
