package com.xkball.xklibmc.client.b3d.uniform;

import com.xkball.xklibmc.api.client.b3d.UpdateWhen;
import net.minecraft.client.Minecraft;

public class XKLibUniforms {
    
    public static final UpdatableUBO SCREEN_SIZE = new UpdatableUBO.UBOBuilder("screen_size")
            .closeOnExit()
            .updateWhen(UpdateWhen.EveryFrame)
            .putVec2("ScreenSize", () -> Minecraft.getInstance().getWindow().getWidth(), () -> Minecraft.getInstance().getWindow().getHeight())
            .build();
    
}
