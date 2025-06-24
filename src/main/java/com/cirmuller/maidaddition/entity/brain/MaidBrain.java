package com.cirmuller.maidaddition.entity.brain;

import com.cirmuller.maidaddition.entity.behaviour.ChunkLoadingBehaviour;
import com.cirmuller.maidaddition.entity.memory.MemoryRegistry;
import com.cirmuller.maidaddition.entity.sensor.SensorRegistry;
import com.github.tartaricacid.touhoulittlemaid.api.entity.ai.IExtraMaidBrain;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.simibubi.create.Create;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraftforge.fml.ModList;
import studio.fantasyit.maid_storage_manager.MaidStorageManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MaidBrain implements IExtraMaidBrain {
    @Override
    public List<MemoryModuleType<?>> getExtraMemoryTypes() {
        List<MemoryModuleType<?>> list= new ArrayList<>();
        list.add(MemoryRegistry.CAN_CHUNK_LOADED.get());
        if(ModList.get().isLoaded(Create.ID)){
            list.add(MemoryRegistry.HAND_CRANK_TARGET.get());
        }
        list.add(MemoryRegistry.PATH_FINDING_NAVIGATION.get());

//        if(ModList.get().isLoaded(MaidStorageManager.MODID)){
//            list.add(MemoryRegistry.CRAFTING_AND_CARRYING_MEMORY.get());
//        }
        return list;
    }

    @Override
    public List<SensorType<? extends Sensor<? super EntityMaid>>> getExtraSensorTypes() {
        List<SensorType<? extends Sensor<? super EntityMaid>>> list= new ArrayList<>();
        if(ModList.get().isLoaded(Create.ID)){
            list.add(SensorRegistry.HAND_CRANK_SENSOR.get());
        }

//        if(ModList.get().isLoaded(MaidStorageManager.MODID)){
//            list.add(SensorRegistry.CRAFTING_AND_CARRYING_SENSOR.get());
//        }

        return list;
    }

    @Override
    public List<Pair<Integer, BehaviorControl<? super EntityMaid>>> getCoreBehaviors() {
        Pair<Integer, BehaviorControl<? super EntityMaid>> chunkLoading=Pair.of(5,new ChunkLoadingBehaviour());
        return ImmutableList.of(chunkLoading);
    }
}
