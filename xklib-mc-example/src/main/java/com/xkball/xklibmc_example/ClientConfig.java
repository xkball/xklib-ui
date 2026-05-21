package com.xkball.xklibmc_example;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientConfig {

    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue FORCE_COMPATIBILITY_MODE;
    public static final ModConfigSpec.BooleanValue MINIMAP_ENABLED;

    static {
        var builder = new ModConfigSpec.Builder();
        FORCE_COMPATIBILITY_MODE = builder
                .comment("Force compatibility mode. When enabled, disables MDI, sparse texture and SSBO rendering features even if the GPU supports them.")
                .define("forceCompatibilityMode", false);
        MINIMAP_ENABLED = builder
                .comment("Enable the minimap HUD overlay.")
                .define("minimapEnabled", true);
        SPEC = builder.build();
    }
}
