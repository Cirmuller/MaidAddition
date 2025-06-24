package com.cirmuller.maidaddition.Utils.Action;

import com.cirmuller.maidaddition.exception.LackBlockException;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.items.wrapper.RangedWrapper;

public class MaidAction {
    public static MaidPutBlockUnderFeet putBlockUnderFeet =(maid, predicate) -> {
        BlockPos pos=maid.getOnPos();
        ServerLevel level=(ServerLevel) maid.level();
        while(level.getBlockState(pos).isAir()){
            pos=pos.below();
        }
        pos=pos.above();
        RangedWrapper wrapper= maid.getAvailableBackpackInv();
        int index;
        for(index=0;index<wrapper.getSlots();index++){
            if(predicate.test(wrapper.getStackInSlot(index))){
                break;
            }
        }
        if(index== wrapper.getSlots()){
            throw new LackBlockException(String.format("Maid %d with owner %s: Unable to find the building block in backpack",maid.getId(),maid.getOwner().getScoreboardName()));
        }
        ItemStack extractedItem=wrapper.extractItem(index,1,false);
        if(extractedItem.getItem() instanceof BlockItem blockItem){
            level.setBlock(pos,blockItem.getBlock().defaultBlockState(), Block.UPDATE_ALL_IMMEDIATE);
            maid.swing(InteractionHand.MAIN_HAND);
        }else{
            throw new RuntimeException(String.format("Maid %d with owner %s: Extracted item is not a BlockItem",maid.getId(),maid.getOwner().getScoreboardName()));
        }
    };

    public static MaidPutBlock putBlockOn=(maid, pos, predicate) -> {
        RangedWrapper wrapper= maid.getAvailableBackpackInv();
        int index;
        for(index=0;index<wrapper.getSlots();index++){
            if(predicate.test(wrapper.getStackInSlot(index))){
                break;
            }
        }
        if(index == wrapper.getSlots()){
            throw new LackBlockException(String.format("Maid %d with owner %s: Unable to find the building block in backpack",maid.getId(),maid.getOwner().getScoreboardName()));
        }
        ItemStack extractedItem=wrapper.extractItem(index,1,false);
        if(extractedItem.getItem() instanceof BlockItem blockItem){
            ServerLevel level=(ServerLevel) maid.level();
            level.setBlock(pos,blockItem.getBlock().defaultBlockState(), Block.UPDATE_ALL_IMMEDIATE);
            maid.swing(InteractionHand.MAIN_HAND);
        }else{
            throw new RuntimeException(String.format("Maid %d with owner %s: Extracted item is not a BlockItem",maid.getId(),maid.getOwner().getScoreboardName()));
        }
    };
}
