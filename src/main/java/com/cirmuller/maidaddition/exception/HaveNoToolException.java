package com.cirmuller.maidaddition.exception;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.world.item.Item;

public class HaveNoToolException extends ExecuteActionException{
    public HaveNoToolException(EntityMaid maid, Item item){
        super(String.format("Maid %d with owner %s has no tool for %s",maid.getId(),maid.getOwner().getScoreboardName(),item.toString()));
    }
}
