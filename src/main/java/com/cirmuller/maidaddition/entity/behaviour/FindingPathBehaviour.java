package com.cirmuller.maidaddition.entity.behaviour;

import com.cirmuller.maidaddition.MaidAddition;
import com.cirmuller.maidaddition.entity.memory.MemoryRegistry;
import com.cirmuller.maidaddition.entity.navigation.PathFindingNavigation;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.BrushableBlock;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraftforge.common.Tags;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.Optional;

public class FindingPathBehaviour extends Behavior<EntityMaid> {
    public static int horizon=16;
    public static int height=6;
    Logger logger= LogManager.getLogger(MaidAddition.MODID);

    public FindingPathBehaviour() {
        super(Map.of(MemoryRegistry.PATH_FINDING_NAVIGATION.get(),MemoryStatus.REGISTERED,
                InitEntities.TARGET_POS.get(),MemoryStatus.VALUE_ABSENT));
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, EntityMaid maid) {
        Optional<PathFindingNavigation> navigation=maid.getBrain().getMemory(MemoryRegistry.PATH_FINDING_NAVIGATION.get());
        return (navigation.isEmpty()||navigation.get().isOutdate())&&maid.isHomeModeEnable();
    }

    @Override
    protected void start(ServerLevel level, EntityMaid maid, long pGameTime) {
        if(pGameTime%100!=0){
            return;
        }
        BlockPos pos=maid.getOnPos();
        BoundingBox box=new BoundingBox(pos.getX()-horizon,pos.getY()-height,pos.getZ()-horizon,
                pos.getX()+horizon,pos.getY()+height,pos.getZ()+horizon);
        PathFindingNavigation navigation=new PathFindingNavigation(maid,
                (blockPos)->level.getBlockState(blockPos).getBlock() instanceof BrushableBlock,
                (item)->item.is(Items.SAND),
                (blockPos)->true,
                box);
        maid.getBrain().setMemory(MemoryRegistry.PATH_FINDING_NAVIGATION.get(),navigation);
        logger.debug("Try to navigate to target position");

    }


}
