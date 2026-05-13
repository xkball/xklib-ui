package com.xkball.xklibmc_example.client.compatibility;

import net.neoforged.neoforge.common.ModConfigSpec;

public class CompatibilityConfig {

    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue FORCE_COMPATIBILITY_MODE;

    static {
        var builder = new ModConfigSpec.Builder();
        FORCE_COMPATIBILITY_MODE = builder
                .comment("Force compatibility mode. When enabled, disables MDI, sparse texture and SSBO rendering features even if the GPU supports them.")
                .define("forceCompatibilityMode", false);
        SPEC = builder.build();
    }
}
