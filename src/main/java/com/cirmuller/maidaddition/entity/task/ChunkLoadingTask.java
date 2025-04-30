package com.cirmuller.maidaddition.entity.task;

import com.cirmuller.maidaddition.MaidAddition;
import com.cirmuller.maidaddition.entity.behaviour.ChunkLoadingBehaviour;
import com.github.tartaricacid.touhoulittlemaid.api.task.IMaidTask;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ChunkLoadingTask implements IMaidTask {

    @Override
    public ResourceLocation getUid() {
        return new ResourceLocation(MaidAddition.MODID,"chunk_loading_task");
    }

    @Override
    public ItemStack getIcon() {
        return Items.BEDROCK.getDefaultInstance();
    }

    @Nullable
    @Override
    public SoundEvent getAmbientSound(EntityMaid entityMaid) {
        return null;
    }

    @Override
    public List<Pair<Integer, BehaviorControl<? super EntityMaid>>> createBrainTasks(EntityMaid entityMaid) {
        return Lists.newArrayList(Pair.of(5, new ChunkLoadingBehaviour()));

    }
}
