package com.xkball.xklibmc_example.client.terrain;

import net.minecraft.world.level.ChunkPos;

public record RegionPos(int x, int z) {
    
    public static RegionPos ofChunk(ChunkPos chunkPos){
        return new RegionPos(chunkPos.x() >> RegionStorage.REGION_SHIFT, chunkPos.z() >> RegionStorage.REGION_SHIFT);
    }
    
}
