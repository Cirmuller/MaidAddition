package com.cirmuller.maidaddition.entity.navigation;

import com.github.tartaricacid.touhoulittlemaid.entity.ai.navigation.MaidNodeEvaluator;
import com.github.tartaricacid.touhoulittlemaid.entity.ai.navigation.MaidPathNavigation;
import com.github.tartaricacid.touhoulittlemaid.entity.ai.path.MaidWrappedPathFinder;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathFinder;

public class UpAndDownPathNavigation extends MaidPathNavigation {
    public UpAndDownPathNavigation(Mob mob, Level level){
        super(mob,level);
    }
    @Override
    protected PathFinder createPathFinder(int range) {
        this.nodeEvaluator = new UpAndDownNodeEvaluator();
        this.nodeEvaluator.setCanOpenDoors(true);
        this.nodeEvaluator.setCanPassDoors(true);
        this.nodeEvaluator.setCanFloat(true);
        return new MaidWrappedPathFinder(this.nodeEvaluator, range);
    }
}
