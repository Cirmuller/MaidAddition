package com.cirmuller.maidaddition.Utils.Action;

import com.cirmuller.maidaddition.MaidAddition;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WalkToAction<T extends PathfinderMob> extends LongTermAction<T>{
    BlockPos destination;
    static Logger logger= LogManager.getLogger(MaidAddition.MODID);
    public WalkToAction(BlockPos destination){
        this.destination=destination;
    }
    @Override
    public boolean execute(T entity) {
        BehaviorUtils.setWalkAndLookTargetMemories(entity,destination.above(),0.4f,-1);
        if(success(entity)){
            logger.debug(String.format("Maid %d arrives position %s",entity.getId(),destination.toString()));
            entity.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        }
        return super.execute(entity);
    }

    @Override
    public boolean success(T entity) {
        //return entity.getOnPos().equals(destination);
        return destination.above().closerToCenterThan(new Vec3(entity.getX(),entity.getY(),entity.getZ()),0.8f);

    }
}
