package com.cirmuller.maidaddition.entity.memory;

import com.cirmuller.maidaddition.MaidAddition;
import com.cirmuller.maidaddition.Utils.CraftingTasks.CraftingTask;
import com.cirmuller.maidaddition.entity.navigation.PathFindingNavigation;
import com.cirmuller.maidaddition.threads.CalculateCraftingStackThread;
import com.mojang.serialization.Codec;
import com.simibubi.create.content.kinetics.crank.HandCrankBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Optional;
import java.util.function.Supplier;

public class MemoryRegistry {
    public static final DeferredRegister<MemoryModuleType<?>> MEMORY_MODULE_TYPE_DEFERRED_REGISTER=DeferredRegister.create(Registries.MEMORY_MODULE_TYPE, MaidAddition.MODID);
    public static Supplier<MemoryModuleType<HandCrankBlockEntity>> HAND_CRANK_TARGET= MEMORY_MODULE_TYPE_DEFERRED_REGISTER.register("hand_crank_target",
            ()->new MemoryModuleType<>(Optional.empty()));
    public static Supplier<MemoryModuleType<Boolean>> CAN_CHUNK_LOADED= MEMORY_MODULE_TYPE_DEFERRED_REGISTER.register("can_chunk_loaded",
            ()->new MemoryModuleType<>(Optional.of(Codec.BOOL)));
    public static Supplier<MemoryModuleType<PathFindingNavigation>> PATH_FINDING_NAVIGATION=MEMORY_MODULE_TYPE_DEFERRED_REGISTER.register("path_finding_navigation",
            ()->new MemoryModuleType<>(Optional.empty()));
}
