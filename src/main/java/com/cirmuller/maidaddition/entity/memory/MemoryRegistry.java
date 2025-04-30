package com.cirmuller.maidaddition.entity.memory;

import com.cirmuller.maidaddition.MaidAddition;
import com.simibubi.create.content.kinetics.crank.HandCrankBlockEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Optional;

public class MemoryRegistry {
    public static final DeferredRegister<MemoryModuleType<?>> MEMORY_MODULE_TYPE_DEFERRED_REGISTER=DeferredRegister.create(ForgeRegistries.MEMORY_MODULE_TYPES, MaidAddition.MODID);
    public static RegistryObject<MemoryModuleType<HandCrankBlockEntity>> HAND_CRANK_TARGET= MEMORY_MODULE_TYPE_DEFERRED_REGISTER.register("hand_crank_target",
            ()->new MemoryModuleType<>(Optional.empty()));
    public static RegistryObject<MemoryModuleType<Boolean>> CAN_CHUNK_LOADED= MEMORY_MODULE_TYPE_DEFERRED_REGISTER.register("can_chunk_loaded",
            ()->new MemoryModuleType<>(Optional.empty()));
}
