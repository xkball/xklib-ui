package com.xkball.xklibmc_example.client.terrain;

import net.minecraft.world.level.ChunkPos;

public record ChunkPosLod(ChunkPos chunkPos, int lodLevel) {
}
