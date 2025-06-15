package com.cirmuller.maidaddition.api;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.world.level.ChunkPos;

import java.util.List;


public interface IChunkLoadingCapability {
    void updateChunkLoading(int radius);
    EntityMaid getMaid();

}
