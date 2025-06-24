package com.cirmuller.maidaddition.Utils.Action;

import com.cirmuller.maidaddition.MaidAddition;
import com.cirmuller.maidaddition.exception.HaveNoToolException;
import com.github.tartaricacid.touhoulittlemaid.entity.chatbubble.implement.TextChatBubbleData;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BrushableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import net.minecraftforge.items.wrapper.EntityHandsInvWrapper;
import net.minecraftforge.items.wrapper.RangedWrapper;

public class ArchaeologyAction extends LongTermAction<EntityMaid> {
    public ArchaeologyAction(){
        super();
        this.timeOut=400;
    }

    @Override
    public boolean execute(EntityMaid entity) {
        ServerLevel level=(ServerLevel) entity.level();
        BlockPos suspiciousPos=entity.getBrain().getMemory(InitEntities.TARGET_POS.get()).get().currentBlockPosition();
        BrushableBlockEntity suspiciousBlockEntity=(BrushableBlockEntity) level.getBlockEntity(suspiciousPos);
        EntityHandsInvWrapper handInv=entity.getHandsInvWrapper();
        int indexSlot;
        if(!handInv.getStackInSlot(0).is(Items.BRUSH)){
            if(handInv.getStackInSlot(1).is(Items.BRUSH)){
                ItemStack stack1=handInv.extractItem(1,64,false);
                ItemStack stack2=handInv.extractItem(0,64,false);
                handInv.insertItem(0,stack1,false);
                handInv.insertItem(1,stack2,false);
            }
            else{
                RangedWrapper backpack=entity.getAvailableBackpackInv();
                for(indexSlot=0;indexSlot<backpack.getSlots();indexSlot++){
                    if(backpack.getStackInSlot(indexSlot).is(Items.BRUSH)){
                        break;
                    }
                }
                if(indexSlot== backpack.getSlots()){
                    throw new HaveNoToolException(entity,Items.BRUSH);
                }
                ItemStack stack1=handInv.extractItem(0,64,false);
                ItemStack stack2=backpack.extractItem(indexSlot,64,false);
                handInv.insertItem(0,stack2,false);
                backpack.insertItem(indexSlot,stack1,false);
            }


        }

        suspiciousBlockEntity.brush(level.getGameTime(),(Player) entity.getOwner(), Direction.UP);
        entity.swing(InteractionHand.MAIN_HAND);
        if(success(entity)){
           ItemStack stack= handInv.extractItem(0,1,false);
           stack.hurtAndBreak(1,entity,(maid)->{
               maid.getOwner().sendSystemMessage(Component.translatable("message."+ MaidAddition.MODID+".brush_is_broken"));
               maid.getChatBubbleManager().addChatBubble(TextChatBubbleData.type1(Component.translatable("message."+MaidAddition.MODID+".brush_is_broken")));
               maid.broadcastBreakEvent(InteractionHand.MAIN_HAND);
           });
           handInv.insertItem(0,stack,false);
        }
        return super.execute(entity);
    }

    @Override
    public boolean success(EntityMaid entity) {
        ServerLevel level=(ServerLevel) entity.level();
        BlockPos suspiciousPos=entity.getBrain().getMemory(InitEntities.TARGET_POS.get()).get().currentBlockPosition();
        return !(level.getBlockState(suspiciousPos).getBlock() instanceof BrushableBlock);
    }
}
