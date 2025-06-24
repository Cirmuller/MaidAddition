package com.cirmuller.maidaddition.Utils.Action;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class WalkToAction<T extends LivingEntity> extends LongTermAction<T>{
    BlockPos destination;
    boolean isSet;
    public WalkToAction(BlockPos destination){
        this.destination=destination;
        isSet=false;
    }
    @Override
    public boolean execute(T entity) {
        if(!isSet){
            BehaviorUtils.setWalkAndLookTargetMemories(entity,destination.above(),0.4f,-1);
            isSet=true;
        }
        if(success(entity)){
            entity.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        }
        return super.execute(entity);
    }

    @Override
    public boolean success(T entity) {
        return entity.getOnPos().equals(destination);
    }
}
