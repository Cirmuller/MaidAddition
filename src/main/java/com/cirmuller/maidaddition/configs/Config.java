package com.cirmuller.maidaddition.configs;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.util.datafix.fixes.OptionsKeyTranslationFix;
import net.neoforged.neoforge.common.ModConfigSpec;


public class Config {
    public static ModConfigSpec COMMON_CONFIG;
    public static ModConfigSpec.IntValue LOADING_RADIUS;
    public static ModConfigSpec.IntValue UPDATE_RATE;

    static {
        ModConfigSpec.Builder COMMON_BUILDER=new ModConfigSpec.Builder();
        COMMON_BUILDER.comment("General settings")
                .push("Chunks loading settings");
        LOADING_RADIUS=COMMON_BUILDER.comment("Set the radius of loading chunks by maids").defineInRange("ChunksLoadingRadius",8,0,64);
        UPDATE_RATE=COMMON_BUILDER.comment("Set the update frequency of loading chunks by maids").defineInRange("UpdateChunksLoadingRate",100,0,Integer.MAX_VALUE);
        COMMON_BUILDER.pop();
        COMMON_CONFIG=COMMON_BUILDER.build();
    }
}
