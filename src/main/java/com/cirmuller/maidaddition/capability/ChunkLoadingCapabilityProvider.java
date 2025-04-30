package com.cirmuller.maidaddition.capability;

import com.cirmuller.maidaddition.MaidAddition;
import com.cirmuller.maidaddition.api.IChunkLoadingCapability;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/*
注意：ChunkLoadingCapabilityProvider与ChunkLoadingCapability都只能在服务端调用
 */
public class ChunkLoadingCapabilityProvider implements ICapabilityProvider{
    ChunkLoadingCapability chunkLoadingCapability;
    EntityMaid maid;
    Level level;

    public static Logger logger= LogManager.getLogger(MaidAddition.MODID);
    public ChunkLoadingCapabilityProvider(EntityMaid maid){
        this.maid=maid;
        chunkLoadingCapability=null;
    }
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction) {
        if(capability==ModCapability.CHUNK_LOADING_CAPABILITY)
        {
            return LazyOptional.of(this::getOrCreateCapability).cast();
        }
        else
        {
            return LazyOptional.empty();
        }
    }

    private IChunkLoadingCapability getOrCreateCapability(){
        if(this.chunkLoadingCapability==null) {
            this.chunkLoadingCapability = new ChunkLoadingCapability(maid);
            return this.chunkLoadingCapability;
        }
        else
        {
            return this.chunkLoadingCapability;
        }
    }

}
