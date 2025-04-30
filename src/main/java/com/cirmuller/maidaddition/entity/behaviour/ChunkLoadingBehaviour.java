package com.cirmuller.maidaddition.entity.behaviour;

import com.cirmuller.maidaddition.MaidAddition;
import com.cirmuller.maidaddition.api.IChunkLoadingCapability;
import com.cirmuller.maidaddition.capability.ModCapability;
import com.cirmuller.maidaddition.configs.Config;
import com.cirmuller.maidaddition.entity.memory.MemoryRegistry;
import com.cirmuller.maidaddition.entity.task.ChunkLoadingTask;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import java.util.HashMap;

public class ChunkLoadingBehaviour extends Behavior<EntityMaid> {
    /*
    重新计算加载区块的间隔时间.
     */
    private static int searchInterval=Config.UPDATE_RATE.get();
    private static int radius= Config.LOADING_RADIUS.get();

    private int currentTick;
    public static Logger logger=LogManager.getLogger(MaidAddition.MODID);
    public ChunkLoadingBehaviour(){
        super(ImmutableMap.of(MemoryRegistry.CAN_CHUNK_LOADED.get(), MemoryStatus.VALUE_PRESENT));
        currentTick =searchInterval;
    }

    @Override
    protected boolean timedOut(long time) {
        return false;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, EntityMaid maid) {
        return true;
    }

    @Override
    protected boolean canStillUse(ServerLevel level, EntityMaid maid, long time) {
        return maid.getBrain().getMemory(MemoryRegistry.CAN_CHUNK_LOADED.get()).isPresent()&& maid.getBrain().getMemory(MemoryRegistry.CAN_CHUNK_LOADED.get()).get();
    }

    @Override
    protected boolean hasRequiredMemories(EntityMaid maid) {
        if(maid.getBrain().getMemory(MemoryRegistry.CAN_CHUNK_LOADED.get()).isEmpty()){
            return false;
        }
        else{
            return maid.getBrain().getMemory(MemoryRegistry.CAN_CHUNK_LOADED.get()).get();
        }
    }

    @Override
    protected void tick(ServerLevel level, EntityMaid maid, long time) {
        if(currentTick!=searchInterval){
            currentTick++;
            super.tick(level,maid,time);
            return;
        }
        currentTick=0;
        LazyOptional<IChunkLoadingCapability> capability=maid.getCapability(ModCapability.CHUNK_LOADING_CAPABILITY);
        capability.ifPresent(
                (cap)->{
                    cap.updateChunkLoading(radius);
                    logger.info(String.format("Maid %d is loading chunks",maid.getId()));
                }
        );
        super.tick(level,maid,time);
    }

    @Override
    protected void stop(ServerLevel level, EntityMaid maid, long time) {
        logger.info(String.format("Maid %d stops loading chunks",maid.getId()));
        super.stop(level, maid, time);
    }

    @Override
    protected void start(ServerLevel p_22540_, EntityMaid p_22541_, long p_22542_) {
        super.start(p_22540_, p_22541_, p_22542_);
    }

}
