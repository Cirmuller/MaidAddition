package com.cirmuller.maidaddition.entity.behaviour;

import com.cirmuller.maidaddition.MaidAddition;
import com.cirmuller.maidaddition.Utils.Action.ActionQueue;
import com.cirmuller.maidaddition.Utils.Action.ArchaeologyAction;
import com.cirmuller.maidaddition.exception.HaveNoToolException;
import com.github.tartaricacid.touhoulittlemaid.entity.chatbubble.IChatBubbleData;
import com.github.tartaricacid.touhoulittlemaid.entity.chatbubble.implement.TextChatBubbleData;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.BrushableBlock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.NoSuchElementException;

public class BrushSandBehaviour extends Behavior<EntityMaid> {

    long startTime;
    ActionQueue<EntityMaid> actions;
    public static Logger logger=LogManager.getLogger(MaidAddition.MODID);
    public BrushSandBehaviour(){
        super(Map.of(InitEntities.TARGET_POS.get(), MemoryStatus.VALUE_PRESENT));
    }

    @Override
    protected void start(ServerLevel pLevel, EntityMaid pEntity, long pGameTime) {
        startTime=pGameTime;
        actions=new ActionQueue<>();
        actions.offer(new ArchaeologyAction());
    }

    @Override
    protected void tick(ServerLevel level, EntityMaid maid, long pGameTime) {
        try{
            actions.execute(maid);
            if(actions.isEmpty()){
                maid.getBrain().eraseMemory(InitEntities.TARGET_POS.get());
            }
        }catch (NullPointerException|NoSuchElementException exception){
            maid.getBrain().eraseMemory(InitEntities.TARGET_POS.get());
        }catch (HaveNoToolException exception){
            logger.debug(String.format("Maid %d with owner %s has no brush",maid.getId(),maid.getOwner().getScoreboardName()));
            Player owner=(Player) maid.getOwner();
            owner.sendSystemMessage(Component.translatable("message."+MaidAddition.MODID+".have_no_brush"));
            maid.getChatBubbleManager().addChatBubble(TextChatBubbleData.type1(Component.translatable("message."+MaidAddition.MODID+".have_no_brush")));
            maid.getBrain().eraseMemory(InitEntities.TARGET_POS.get());
        }
    }

    @Override
    protected void stop(ServerLevel level, EntityMaid maid, long pGameTime) {
        maid.getBrain().eraseMemory(InitEntities.TARGET_POS.get());
    }

    @Override
    protected boolean canStillUse(ServerLevel pLevel, EntityMaid pEntity, long pGameTime) {
        return !timedOut(pGameTime)&&hasRequiredMemories(pEntity);
    }

    @Override
    protected boolean timedOut(long pGameTime) {
        return pGameTime-startTime>400;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, EntityMaid maid) {
        BlockPos pos=maid.getBrain().getMemory(InitEntities.TARGET_POS.get()).get().currentBlockPosition();
        return (level.getBlockState(pos).getBlock() instanceof BrushableBlock)&&maid.isHomeModeEnable();
    }
}
