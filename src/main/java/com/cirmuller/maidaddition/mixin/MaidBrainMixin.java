package com.cirmuller.maidaddition.mixin;

import com.cirmuller.maidaddition.entity.behaviour.ChunkLoadingBehaviour;
import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.MaidBrain;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitEntities;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.schedule.Activity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MaidBrain.class)
public class MaidBrainMixin {

    @Inject(method = "registerRideIdleGoals",at = @At("TAIL"),remap = false)
    private static void registerRideIdleGoalMixin(Brain<EntityMaid> brain, CallbackInfo ci){
        Pair<Integer, BehaviorControl<? super EntityMaid>> chunkLoading=Pair.of(5,new ChunkLoadingBehaviour());
        brain.addActivity(InitEntities.RIDE_IDLE.get(), ImmutableList.of(chunkLoading));
    }

    @Inject(method = "registerRideWorkGoals",at=@At("TAIL"),remap = false)
    private static void registerRideWorkGoalsMixin(Brain<EntityMaid> brain,EntityMaid maid,CallbackInfo ci){
        Pair<Integer, BehaviorControl<? super EntityMaid>> chunkLoading=Pair.of(5,new ChunkLoadingBehaviour());
        brain.addActivity(InitEntities.RIDE_WORK.get(), ImmutableList.of(chunkLoading));
    }

    @Inject(method = "registerRideRestGoals",at=@At("TAIL"),remap = false)
    private static void registerRideRestGoalsMixin(Brain<EntityMaid> brain,CallbackInfo ci){
        Pair<Integer, BehaviorControl<? super EntityMaid>> chunkLoading=Pair.of(5,new ChunkLoadingBehaviour());
        brain.addActivity(InitEntities.RIDE_REST.get(), ImmutableList.of(chunkLoading));
    }

    @Inject(method = "registerIdleGoals",at=@At("TAIL"),remap = false)
    private static void registerIdleGoalsMixin(Brain<EntityMaid> brain,CallbackInfo ci){
        Pair<Integer, BehaviorControl<? super EntityMaid>> chunkLoading=Pair.of(5,new ChunkLoadingBehaviour());
        brain.addActivity(Activity.IDLE, ImmutableList.of(chunkLoading));
    }

    @Inject(method = "registerWorkGoals",at=@At("TAIL"),remap = false)
    private static void registerWorkGoalsMixin(Brain<EntityMaid> brain,EntityMaid maid,CallbackInfo ci){
        Pair<Integer, BehaviorControl<? super EntityMaid>> chunkLoading=Pair.of(5,new ChunkLoadingBehaviour());
        brain.addActivity(Activity.WORK, ImmutableList.of(chunkLoading));
    }

    @Inject(method = "registerRestGoals",at=@At("TAIL"),remap = false)
    private static void registerRestGoalsMixin(Brain<EntityMaid> brain,CallbackInfo ci){
        Pair<Integer, BehaviorControl<? super EntityMaid>> chunkLoading=Pair.of(5,new ChunkLoadingBehaviour());
        brain.addActivity(Activity.REST, ImmutableList.of(chunkLoading));
    }
}
