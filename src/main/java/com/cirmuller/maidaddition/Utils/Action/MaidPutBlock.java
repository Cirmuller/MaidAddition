package com.cirmuller.maidaddition.Utils.Action;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

@FunctionalInterface
public interface MaidPutBlock{
    void execute(EntityMaid maid, BlockPos pos, Predicate<ItemStack> blockToPut);
}
