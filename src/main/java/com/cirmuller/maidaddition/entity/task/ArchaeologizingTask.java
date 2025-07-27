package com.cirmuller.maidaddition.entity.task;

import com.cirmuller.maidaddition.MaidAddition;
import com.cirmuller.maidaddition.entity.behaviour.BrushSandBehaviour;
import com.cirmuller.maidaddition.entity.behaviour.CraftingAndCarryingBehaviour;
import com.cirmuller.maidaddition.entity.behaviour.FindingPathBehaviour;
import com.cirmuller.maidaddition.entity.behaviour.WalkingToSuspiciousSandBehaviour;
import com.cirmuller.maidaddition.entity.memory.MemoryRegistry;
import com.cirmuller.maidaddition.entity.navigation.PathFindingNavigation;
import com.github.tartaricacid.touhoulittlemaid.api.task.IMaidTask;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitEntities;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ArchaeologizingTask implements IMaidTask {

    @Override
    public ResourceLocation getUid() {
        return new ResourceLocation(MaidAddition.MODID,"archaeology");
    }

    @Override
    public ItemStack getIcon() {
        return Items.BRUSH.getDefaultInstance();
    }

    @Nullable
    @Override
    public SoundEvent getAmbientSound(EntityMaid entityMaid) {
        return null;
    }

    @Override
    public List<Pair<Integer, BehaviorControl<? super EntityMaid>>> createBrainTasks(EntityMaid entityMaid) {
        return Lists.newArrayList(new Pair<>(0,new FindingPathBehaviour()),
                new Pair<>(0, new WalkingToSuspiciousSandBehaviour()),
                new Pair<>(0,new BrushSandBehaviour()));

    }

    @Override
    public boolean enableLookAndRandomWalk(EntityMaid maid) {
        PathFindingNavigation navigation=maid.getBrain().getMemory(MemoryRegistry.PATH_FINDING_NAVIGATION.get()).orElse(null);
        if(navigation==null){
            return true;
        }else if(maid.getBrain().getMemory(InitEntities.TARGET_POS.get()).isPresent()){
            return false;
        }
        else{
            return !navigation.isTerminated()||navigation.isOutdate();
        }
    }

}
