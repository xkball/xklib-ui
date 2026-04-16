package com.xkball.xklibmc.client.b3d.uniform;

import com.xkball.xklibmc.api.client.b3d.UpdateWhen;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector4f;

public class XKLibUniforms {
    
    public static final UpdatableUBO SCREEN_SIZE = new UpdatableUBO.UBOBuilder("screen_size")
            .closeOnExit()
            .updateWhen(UpdateWhen.EveryFrame)
            .putVec2("ScreenSize", () -> Minecraft.getInstance().getWindow().getWidth(), () -> Minecraft.getInstance().getWindow().getHeight())
            .build();
    
    public static final UpdatableUBO INVERSE_PROJ_MAT = new UpdatableUBO.UBOBuilder("invProjMat")
            .closeOnExit()
            .putMat4f("invMat", Matrix4f::new)
            .putMat4f("projMat", Matrix4f::new)
            .putVec4("camDir",Vector4f::new)
            .putVec4("camPos",Vector4f::new)
            .build();
    
    //并不需要向球心聚拢: 采样范围在单位方块内
    public static final UpdatableUBO SSAO_DATA = new UpdatableUBO.UBOBuilder("ssao_samplers")
            .closeOnExit()
            .updateWhen(UpdateWhen.Reload)
            .putCustom(b -> {
                var random = RandomSource.create(943);
                for (int i = 0; i < 64; i++) {
                    var x = Mth.frac(random.nextFloat()) * 2 - 1;
                    var y = Mth.frac(random.nextFloat()) * 2 - 1;
                    var z = Mth.frac(random.nextFloat()) * 2 - 1;
                    var vec = new Vector4f(x, y, z, 1);
                    vec.normalize(i/64f);
                    b.putVec4("p"+i, () -> vec);
                }
                for (int i = 0; i < 16; i++) {
                    var x = Mth.frac(random.nextFloat()) * Math.PI * 2;
                    var y = Mth.frac(random.nextFloat()) * Math.PI * 2;
                    var z = Mth.frac(random.nextFloat()) * Math.PI * 2;
                    var q = new Quaternionf().rotateXYZ((float) x, (float) y, (float) z);
                    var mat = new Matrix4f().rotate(q);
                    b.putMat4f("p"+i, () -> mat);
                }
            })
            .build();
    
    public static void init(){
    
    }
}
