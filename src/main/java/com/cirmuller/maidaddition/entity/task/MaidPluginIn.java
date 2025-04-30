package com.cirmuller.maidaddition.entity.task;

import com.cirmuller.maidaddition.entity.brain.MaidBrain;
import com.github.tartaricacid.touhoulittlemaid.api.ILittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.LittleMaidExtension;
import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.ExtraMaidBrainManager;
import com.github.tartaricacid.touhoulittlemaid.entity.task.TaskManager;

@LittleMaidExtension
public class MaidPluginIn implements ILittleMaid {

    @Override
    public void addMaidTask(TaskManager manager) {
        TaskInit.addTask(manager);
    }

    @Override
    public void addExtraMaidBrain(ExtraMaidBrainManager manager) {
        manager.addExtraMaidBrain(new MaidBrain());
    }
}
