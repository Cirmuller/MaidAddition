package com.cirmuller.maidaddition.entity.behaviour;

import com.cirmuller.maidaddition.MaidAddition;
import com.cirmuller.maidaddition.entity.memory.MemoryRegistry;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.google.common.collect.ImmutableMap;
import com.simibubi.create.content.kinetics.crank.HandCrankBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;

public class UseHandCrankBehaviour extends Behavior<EntityMaid> {
    float speedModifier;
    HandCrankBlockEntity handCrankBlockEntity;
    Logger logger= LogManager.getLogger(MaidAddition.MODID);
    public UseHandCrankBehaviour(float speedModifier) {
        super(ImmutableMap.of(MemoryRegistry.HAND_CRANK_TARGET.get(),MemoryStatus.VALUE_PRESENT, MemoryModuleType.WALK_TARGET,MemoryStatus.VALUE_ABSENT));
        this.speedModifier=speedModifier;
    }

    @Override
    protected void start(ServerLevel serverLevel, EntityMaid entityMaid, long gameTimeIn) {
        entityMaid.getBrain().getMemory(MemoryRegistry.HAND_CRANK_TARGET.get()).ifPresent((target)->{
            BehaviorUtils.setWalkAndLookTargetMemories(entityMaid,target.getBlockPos(),this.speedModifier,0);
            handCrankBlockEntity=target;
            Direction direction=handCrankBlockEntity.getBlockState().getValue(DirectionalKineticBlock.FACING);
            entityMaid.getBrain().setMemory(MemoryModuleType.LOOK_TARGET,new BlockPosTracker(handCrankBlockEntity.getBlockPos()));
            BlockPosTracker blockPosTracker=switch (direction){
                case NORTH -> new BlockPosTracker(handCrankBlockEntity.getBlockPos().north());
                case SOUTH -> new BlockPosTracker(handCrankBlockEntity.getBlockPos().south());
                case EAST -> new BlockPosTracker(handCrankBlockEntity.getBlockPos().east());
                case WEST -> new BlockPosTracker(handCrankBlockEntity.getBlockPos().west());
                case UP -> new BlockPosTracker(handCrankBlockEntity.getBlockPos().above());
                case DOWN -> new BlockPosTracker(handCrankBlockEntity.getBlockPos().below());
            };
            entityMaid.getBrain().setMemory(MemoryModuleType.WALK_TARGET,new WalkTarget(blockPosTracker,
                    this.speedModifier,0));
        });
    }

    @Override
    protected void tick(ServerLevel serverLevel, EntityMaid entityMaid, long gameTimeIn) {
        if(handCrankBlockEntity!=null&&
                !handCrankBlockEntity.isRemoved()&&handCrankBlockEntity.getBlockPos().closerThan(
                 new Vec3i(entityMaid.getOnPos().getX(),entityMaid.getOnPos().getY(), entityMaid.getOnPos().getZ()),
                2)){

            handCrankBlockEntity.turn(false);
        }
        super.tick(serverLevel,entityMaid,gameTimeIn);
    }

    @Override
    protected boolean canStillUse(ServerLevel serverLevel, EntityMaid entityMaid, long gameTimeIn) {
        if(handCrankBlockEntity.isRemoved()||!handCrankBlockEntity.getBlockPos().closerThan(new Vec3i(entityMaid.getOnPos().getX(),entityMaid.getOnPos().getY(), entityMaid.getOnPos().getZ()),
                2)){
            handCrankBlockEntity=null;
            return false;
        }
        else
        {
            return true;
        }
    }

    @Override
    protected boolean timedOut(long gameTimeIn) {
        return false;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverLevel, EntityMaid entityMaid) {
        return true;
    }
}
