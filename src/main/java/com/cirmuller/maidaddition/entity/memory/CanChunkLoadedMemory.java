package com.cirmuller.maidaddition.entity.memory;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record CanChunkLoadedMemory(Boolean canChunkLoaded) {
    public static final Codec<CanChunkLoadedMemory> CODEC=RecordCodecBuilder.create(instance->instance.group(
            Codec.BOOL.fieldOf("can_chunk_loaded_memory").forGetter(CanChunkLoadedMemory::canChunkLoaded)
    ).apply(instance,CanChunkLoadedMemory::new));
}
