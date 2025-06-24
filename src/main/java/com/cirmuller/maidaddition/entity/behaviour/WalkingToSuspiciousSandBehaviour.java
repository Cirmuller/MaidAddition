package com.cirmuller.maidaddition.entity.behaviour;

import com.cirmuller.maidaddition.MaidAddition;
import com.cirmuller.maidaddition.entity.memory.MemoryRegistry;
import com.cirmuller.maidaddition.entity.navigation.CallbackEdge;
import com.cirmuller.maidaddition.entity.navigation.PathFindingNavigation;
import com.cirmuller.maidaddition.exception.HaveNoToolException;
import com.cirmuller.maidaddition.exception.LackBlockException;
import com.cirmuller.maidaddition.exception.TimeoutException;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.behavior.PositionTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.Consumer;

public class WalkingToSuspiciousSandBehaviour extends Behavior<EntityMaid> {
    public static Logger logger= LogManager.getLogger(MaidAddition.MODID+".WalkingToSuspiciousSandBehaviour");
    public int time=1;


    Queue<Consumer<EntityMaid>> taskToExecute=new LinkedList<>();
    public WalkingToSuspiciousSandBehaviour() {
        super(Map.of(MemoryRegistry.PATH_FINDING_NAVIGATION.get(),MemoryStatus.VALUE_PRESENT,
                InitEntities.TARGET_POS.get(),MemoryStatus.VALUE_ABSENT));
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, EntityMaid maid) {
        return (!maid.getBrain().getMemory(MemoryRegistry.PATH_FINDING_NAVIGATION.get()).get().isOutdate())&&maid.isHomeModeEnable();
    }

    @Override
    protected void tick(ServerLevel level, EntityMaid maid, long pGameTime) {
        if(pGameTime%2!=0){
            return;
        }
        PathFindingNavigation navigation=maid.getBrain().getMemory(MemoryRegistry.PATH_FINDING_NAVIGATION.get()).get();
        if(!navigation.isTerminated()){
            if(time%200==0){
                logger.debug("Do calculating navigation");
                time=1;

            }
            return;
        }
        if(navigation.isOutdate()){
            return;
        }

        try{
            navigation.getActions().execute(maid);
            if(navigation.getActions().isEmpty()&&maid.getOnPos().closerThan(navigation.getTarget(),0.5)){
                navigation.setOutdate();
                maid.getBrain().setMemory(InitEntities.TARGET_POS.get(), new BlockPosTracker(navigation.getTarget()));
            }
        }catch(LackBlockException | TimeoutException exception){
            logger.debug(exception.getMessage());
            maid.getBrain().eraseMemory(MemoryRegistry.PATH_FINDING_NAVIGATION.get());
        }

    }

    @Override
    protected void stop(ServerLevel level, EntityMaid maid, long pGameTime) {
        maid.getBrain().eraseMemory(MemoryRegistry.PATH_FINDING_NAVIGATION.get());
    }

    @Override
    protected boolean canStillUse(ServerLevel level, EntityMaid maid, long pGameTime) {
        if(!hasRequiredMemories(maid)){
            return false;
        }
        PathFindingNavigation navigation=maid.getBrain().getMemory(MemoryRegistry.PATH_FINDING_NAVIGATION.get()).get();
        if(!navigation.isTerminated()){
            return true;
        }

        return !navigation.isOutdate();
    }

    @Override
    protected boolean timedOut(long pGameTime) {
        return false;
    }

}
