package com.cirmuller.maidaddition.entity.task;

import com.cirmuller.maidaddition.MaidAddition;
import com.cirmuller.maidaddition.entity.behaviour.UseHandCrankBehaviour;
import com.github.tartaricacid.touhoulittlemaid.api.task.IMaidTask;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.simibubi.create.AllBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class UseHandCrankTask implements IMaidTask {
    private static final float searchRadius=32;
    @Override
    public ResourceLocation getUid() {
        return new ResourceLocation(MaidAddition.MODID,"hand_crank_task");
    }

    @Override
    public ItemStack getIcon() {
        return AllBlocks.HAND_CRANK.asItem().getDefaultInstance();
    }

    @Nullable
    @Override
    public SoundEvent getAmbientSound(EntityMaid entityMaid) {
        return null;
    }

    @Override
    public List<Pair<Integer, BehaviorControl<? super EntityMaid>>> createBrainTasks(EntityMaid entityMaid) {
        return Lists.newArrayList(new Pair<>(0, new UseHandCrankBehaviour(0.8f)));
    }

    @Override
    public float searchRadius(EntityMaid maid) {
        return searchRadius;
    }

    @Override
    public boolean enableLookAndRandomWalk(EntityMaid maid) {
        return false;
    }
}
