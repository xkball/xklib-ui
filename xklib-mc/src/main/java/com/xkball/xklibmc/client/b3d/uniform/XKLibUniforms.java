package com.xkball.xklibmc.client.b3d.uniform;

import com.xkball.xklibmc.api.client.b3d.UpdateWhen;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class XKLibUniforms {
    
    public static final UpdatableUBO SCREEN_SIZE = new UpdatableUBO.UBOBuilder("screen_size")
            .closeOnExit()
            .updateWhen(UpdateWhen.EveryFrame)
            .putVec2("ScreenSize", () -> Minecraft.getInstance().getWindow().getWidth(), () -> Minecraft.getInstance().getWindow().getHeight())
            .build();
    
    public static final UpdatableUBO SSAO_SAMPLERS = new UpdatableUBO.UBOBuilder("ssao_samplers")
            .closeOnExit()
            .updateWhen(UpdateWhen.Reload)
            .putCustom(b -> {
                var random = RandomSource.create(114514 * 943);
                for (int i = 0; i < 64; i++) {
                    var x = Mth.frac(random.nextFloat()) * 2 - 1;
                    var y = Mth.frac(random.nextFloat()) * 2 - 1;
                    var z = Mth.frac(random.nextFloat()) * 2 - 1;
                    var vec = new Vector3f(x, y, z);
                    vec.normalize(Mth.frac(random.nextFloat()));
                    var s = i/64f;
                    vec.mul(Mth.lerp(s * s, 0.1f, 1));
                    b.putVec3("p"+i, () -> vec);
                }
            })
            .build();
    
    public static final UpdatableUBO SSAO_ROTATE = new UpdatableUBO.UBOBuilder("ssao_rotate")
            .closeOnExit()
            .updateWhen(UpdateWhen.Reload)
            .putCustom(b -> {
                var random = RandomSource.create(114514 * 943);
                for (int i = 0; i < 16; i++) {
                    var x = Mth.frac(random.nextFloat());
                    var y = Mth.frac(random.nextFloat());
                    var z = Mth.frac(random.nextFloat());
                    var q = new Quaternionf().rotateXYZ(x, y, z);
                    var mat = new Matrix4f().rotate(q);
                    b.putMat4f("p"+i, () -> mat);
                }
            })
            .build();
}
