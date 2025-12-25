package com.cirmuller.maidaddition.entity.task;

import com.github.tartaricacid.touhoulittlemaid.entity.task.TaskManager;
import com.simibubi.create.Create;
import net.neoforged.fml.ModList;

public class TaskInit {
    public static void addTask(TaskManager manager){
        //弃用以任务的形式加载区块
        //manager.add(new ChunkLoadingTask());
        if(ModList.get().isLoaded(Create.ID)){
            manager.add(new UseHandCrankTask());
        }
        manager.add(new ArchaeologizingTask());

//        if(ModList.get().isLoaded(MaidStorageManager.MODID)){
//            manager.add(new CraftingAndCarryingTask());
//        }
    }
}
