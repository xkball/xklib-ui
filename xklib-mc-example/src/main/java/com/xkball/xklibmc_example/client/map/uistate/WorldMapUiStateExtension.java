package com.xkball.xklibmc_example.client.map.uistate;

import com.xkball.xklibmc_example.api.client.map.WorldMapExtension;
import com.xkball.xklibmc_example.client.terrain.LevelChunkStorage;

public class WorldMapUiStateExtension implements WorldMapExtension {

    @Override
    public String id() {
        return WorldMapUiStateStorage.EXTENSION_ID;
    }

    @Override
    public void onStorageLoaded(LevelChunkStorage storage) {
        if (storage.getExtensionStorage(WorldMapUiStateStorage.EXTENSION_ID) == null) {
            storage.registerExtensionStorage(new WorldMapUiStateStorage());
        }
    }
}
