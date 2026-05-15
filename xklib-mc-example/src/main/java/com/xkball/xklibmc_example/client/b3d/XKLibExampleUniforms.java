package com.xkball.xklibmc_example.client.b3d;

import com.xkball.xklibmc.client.b3d.uniform.UpdatableUBO;
import org.joml.Vector2f;

public class XKLibExampleUniforms {
    
    public static final UpdatableUBO LEVEL_DATA = new UpdatableUBO.UBOBuilder("level_data")
            .closeOnExit()
            .putVec2("max_min_height", Vector2f::new)
            .build();
}
