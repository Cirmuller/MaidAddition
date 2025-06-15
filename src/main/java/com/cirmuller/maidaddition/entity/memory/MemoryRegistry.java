package com.cirmuller.maidaddition.entity.memory;

import com.cirmuller.maidaddition.MaidAddition;
import com.cirmuller.maidaddition.Utils.CraftingTasks.CraftingTask;
import com.cirmuller.maidaddition.threads.CalculateCraftingStackThread;
import com.mojang.serialization.Codec;
import com.simibubi.create.content.kinetics.crank.HandCrankBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Optional;

public class MemoryRegistry {
    public static final DeferredRegister<MemoryModuleType<?>> MEMORY_MODULE_TYPE_DEFERRED_REGISTER=DeferredRegister.create(ForgeRegistries.MEMORY_MODULE_TYPES, MaidAddition.MODID);
    public static RegistryObject<MemoryModuleType<HandCrankBlockEntity>> HAND_CRANK_TARGET= MEMORY_MODULE_TYPE_DEFERRED_REGISTER.register("hand_crank_target",
            ()->new MemoryModuleType<>(Optional.empty()));
    public static RegistryObject<MemoryModuleType<Boolean>> CAN_CHUNK_LOADED= MEMORY_MODULE_TYPE_DEFERRED_REGISTER.register("can_chunk_loaded",
            ()->new MemoryModuleType<>(Optional.of(Codec.BOOL)));
    public static RegistryObject<MemoryModuleType<CraftingAndCarryingMemory>> CRAFTING_AND_CARRYING_MEMORY=MEMORY_MODULE_TYPE_DEFERRED_REGISTER.register("crafting_and_carrying_memory",
            ()->new MemoryModuleType<>(Optional.empty()));


}
