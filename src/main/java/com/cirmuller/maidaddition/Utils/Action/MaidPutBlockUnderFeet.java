package com.cirmuller.maidaddition.Utils.Action;


import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

@FunctionalInterface
public interface MaidPutBlockUnderFeet {
    void execute(EntityMaid maid, Predicate<ItemStack> predicate);
}
