package com.cirmuller.maidaddition;

import com.cirmuller.maidaddition.MaidAddition;
import com.cirmuller.maidaddition.entity.brain.MaidBrain;
import com.cirmuller.maidaddition.entity.memory.CanChunkLoadedMemory;
import com.cirmuller.maidaddition.entity.task.TaskInit;
import com.github.tartaricacid.touhoulittlemaid.api.ILittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.LittleMaidExtension;
import com.github.tartaricacid.touhoulittlemaid.api.entity.data.TaskDataKey;
import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.ExtraMaidBrainManager;
import com.github.tartaricacid.touhoulittlemaid.entity.data.TaskDataRegister;
import com.github.tartaricacid.touhoulittlemaid.entity.task.TaskManager;
import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.treedecorators.CocoaDecorator;

@LittleMaidExtension
public class MaidPluginIn implements ILittleMaid {
    public static TaskDataKey<CanChunkLoadedMemory> canChunkLoadedData;
    @Override
    public void addMaidTask(TaskManager manager) {
        TaskInit.addTask(manager);
    }

    @Override
    public void addExtraMaidBrain(ExtraMaidBrainManager manager) {
        manager.addExtraMaidBrain(new MaidBrain());
    }

    @Override
    public void registerTaskData(TaskDataRegister register) {
        canChunkLoadedData=register.register(new ResourceLocation(MaidAddition.MODID,"can_chunk_loaded_data"), CanChunkLoadedMemory.CODEC);
    }
}
