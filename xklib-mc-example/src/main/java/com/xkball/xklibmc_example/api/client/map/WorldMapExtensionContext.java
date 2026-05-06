package com.xkball.xklibmc_example.api.client.map;

import com.xkball.xklibmc_example.client.terrain.TerrainChunkManager;

public record WorldMapExtensionContext(TerrainChunkManager terrainChunkManager, WorldMapExtensionRegistry registry) {
}
