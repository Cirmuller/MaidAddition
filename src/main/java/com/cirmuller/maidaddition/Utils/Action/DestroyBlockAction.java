package com.cirmuller.maidaddition.Utils.Action;

import com.cirmuller.maidaddition.exception.HaveNoToolException;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.wrapper.EntityHandsInvWrapper;
import net.minecraftforge.items.wrapper.RangedWrapper;

public class DestroyBlockAction extends LongTermAction<EntityMaid> {
    BlockPos target;
    float destroyProcess=0.0f;
    public DestroyBlockAction(BlockPos target){
        super();
        this.target=target;
    }

    @Override
    public boolean execute(EntityMaid maid) {
        ServerLevel level=(ServerLevel) maid.level();
        BlockState state=level.getBlockState(target);
        RangedWrapper backpack=maid.getAvailableBackpackInv();
        EntityHandsInvWrapper handsInvWrapper= maid.getHandsInvWrapper();
        ItemStack itemInMainHand=handsInvWrapper.getStackInSlot(0);
        if(!itemInMainHand.isCorrectToolForDrops(state)){
            ItemStack itemInOffHand=handsInvWrapper.getStackInSlot(1);
            if(itemInOffHand.isCorrectToolForDrops(state)){
                itemInMainHand=handsInvWrapper.extractItem(0,64,false);
                itemInOffHand=handsInvWrapper.extractItem(1,64,false);
                handsInvWrapper.insertItem(0,itemInOffHand,false);
                handsInvWrapper.insertItem(1,itemInMainHand,false);
            }
            else{
                int indexSlot;
                for(indexSlot=0;indexSlot<backpack.getSlots();indexSlot++){
                    if(backpack.getStackInSlot(indexSlot).isCorrectToolForDrops(state)){
                        break;
                    }
                }
                if(indexSlot== backpack.getSlots()){
                    //throw new HaveNoToolException(maid,state.getBlock().asItem());
                }else{
                    ItemStack itemInBackpack=backpack.extractItem(indexSlot,64,false);
                    itemInMainHand=handsInvWrapper.extractItem(0,64,false);
                    backpack.insertItem(indexSlot,itemInMainHand,false);
                    handsInvWrapper.insertItem(0,itemInBackpack,false);
                }

            }

        }
        itemInMainHand=handsInvWrapper.extractItem(0,64,false);
        float destroySpeed=itemInMainHand.getDestroySpeed(state);
        destroyProcess+=destroySpeed;
        maid.swing(InteractionHand.MAIN_HAND);
        if(success(maid)){
            maid.destroyBlock(target,true);
            itemInMainHand.hurtAndBreak(1,maid,(md)->{});
        }
        handsInvWrapper.insertItem(0,itemInMainHand,false);
        return super.execute(maid);
    }

    @Override
    public boolean success(EntityMaid maid) {
        ServerLevel level=(ServerLevel) maid.level();
        float hardness=level.getBlockState(target).getDestroySpeed(level,target);
        if(hardness<-0.00001){
            return false;
        }
        if(destroyProcess>=30*hardness){
            return true;
        }
        else {
            return false;
        }
    }
}
