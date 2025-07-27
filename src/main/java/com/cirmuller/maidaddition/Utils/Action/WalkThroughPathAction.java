package com.cirmuller.maidaddition.Utils.Action;

import com.cirmuller.maidaddition.MaidAddition;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WalkThroughPathAction<T extends PathfinderMob> extends LongTermAction<T>{
    static Logger logger= LogManager.getLogger(MaidAddition.MODID);
    Path path;
    boolean isStarted;
    public WalkThroughPathAction(Path path){
        this.path=path;
        isStarted=false;
    }
    @Override
    public boolean execute(T entity) {
        if(!isStarted){
            entity.getNavigation().moveTo(path,0.4f);
            isStarted=true;
        }
        if(success(entity)){
            logger.debug(String.format("Maid %d arrives position %s",entity.getId(),path.getTarget()));
            //entity.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        }
        return super.execute(entity);
    }

    @Override
    public boolean success(T entity) {
        //return entity.getOnPos().equals(destination);
        return path.getTarget().closerToCenterThan(new Vec3(entity.getX(),entity.getY(),entity.getZ()),0.8f);

    }
}
