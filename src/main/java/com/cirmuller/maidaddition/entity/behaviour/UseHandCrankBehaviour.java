package com.cirmuller.maidaddition.entity.behaviour;

import com.cirmuller.maidaddition.MaidAddition;
import com.cirmuller.maidaddition.entity.memory.MemoryRegistry;
import com.github.tartaricacid.touhoulittlemaid.entity.chatbubble.IChatBubbleData;
import com.github.tartaricacid.touhoulittlemaid.entity.chatbubble.implement.TextChatBubbleData;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.google.common.collect.ImmutableMap;
import com.simibubi.create.content.kinetics.crank.HandCrankBlock;
import com.simibubi.create.content.kinetics.crank.HandCrankBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;

import javax.annotation.Nullable;
import java.util.Random;

public class UseHandCrankBehaviour extends Behavior<EntityMaid> {
    float speedModifier;
    HandCrankBlockEntity handCrankBlockEntity;
    Logger logger= LogManager.getLogger(MaidAddition.MODID);
    int bubbleTick;
    public static Random random=new Random();

    static String[] bubbles={"message."+MaidAddition.MODID+".996_is_felicity",
    "message."+MaidAddition.MODID+".you_will_be_grateful_to_yourself_tomorrow_for_struggling_today",
    "message."+MaidAddition.MODID+".thanks_for_providing_work_by_player",
    "message."+MaidAddition.MODID+".struggle_for_owner"};
    private static final int bubbleInterval=600;
    public UseHandCrankBehaviour(float speedModifier) {
        super(ImmutableMap.of(MemoryRegistry.HAND_CRANK_TARGET.get(),MemoryStatus.VALUE_PRESENT, MemoryModuleType.WALK_TARGET,MemoryStatus.VALUE_ABSENT));
        this.speedModifier=speedModifier;
        bubbleTick=0;
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
                !handCrankBlockEntity.isRemoved()&&isCloserThan(
                        new Vec3((double)handCrankBlockEntity.getBlockPos().getX(),(double)handCrankBlockEntity.getBlockPos().getY(),(double)handCrankBlockEntity.getBlockPos().getZ()),
                new Vec3(entityMaid.getX(),entityMaid.getY(),entityMaid.getZ()),
                2,2)
        ){
            logger.debug(String.format("The speed of handcrank which is used by maid %d is %f",entityMaid.getId(),handCrankBlockEntity.getGeneratedSpeed()));

            Direction direction=handCrankBlockEntity.getBlockState().getValue(HandCrankBlock.FACING);
            switch (direction){
                case DOWN,WEST,NORTH:handCrankBlockEntity.turn(handCrankBlockEntity.getSpeed()>0.01);
                    entityMaid.swing(InteractionHand.MAIN_HAND);
                break;
                default:handCrankBlockEntity.turn(handCrankBlockEntity.getSpeed()<-0.01);
                    entityMaid.swing(InteractionHand.MAIN_HAND);
                break;

            }
            bubbleTick++;
            if(bubbleTick==bubbleInterval){
                bubbleTick=0;
                int randomIndex=random.nextInt(bubbles.length);
                Component component=getChatBubbleComponent(entityMaid,randomIndex);
                if(component!=null) {
                    entityMaid.getChatBubbleManager().addChatBubble(TextChatBubbleData.type2(component));
                }
            }


        }
        super.tick(serverLevel,entityMaid,gameTimeIn);
    }

    @Override
    protected boolean canStillUse(ServerLevel serverLevel, EntityMaid entityMaid, long gameTimeIn) {
        if(handCrankBlockEntity.isRemoved()||!isCloserThan(
                new Vec3((double)handCrankBlockEntity.getBlockPos().getX(),(double)handCrankBlockEntity.getBlockPos().getY(),(double)handCrankBlockEntity.getBlockPos().getZ()),
                new Vec3(entityMaid.getX(),entityMaid.getY(),entityMaid.getZ()),
                2,4)){
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

    private boolean isCloserThan(Vec3 a, Vec3 b, float horizontal, float vertical){
        return ((a.x-b.x)*(a.x-b.x)+(a.z-b.z)*(a.z-b.z)<=horizontal*horizontal)&&(Math.abs(a.y-b.y)<=vertical);
    }

    @Nullable
    private Component getChatBubbleComponent(EntityMaid maid,int index){
        ServerPlayer owner=(ServerPlayer) maid.getOwner();
        if(owner==null){
            return null;
        }
        return switch(index){
            case 2: yield Component.translatable(bubbles[index],owner.getScoreboardName());
            case 3: yield Component.translatable(bubbles[index],owner.getScoreboardName(),((ServerPlayer)maid.getOwner()).getScoreboardName());
            case 0:
            case 1:
            default:
                yield Component.translatable(bubbles[index]);
        };
    }

    @Override
    protected void stop(ServerLevel pLevel, EntityMaid pEntity, long pGameTime) {
        super.stop(pLevel, pEntity, pGameTime);
        pEntity.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
    }
}
