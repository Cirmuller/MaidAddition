package com.cirmuller.maidaddition.Utils;

import com.cirmuller.maidaddition.tickets.TicketRegistry;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.level.ChunkPos;

import java.util.LinkedList;
import java.util.List;

public class ChunkLoading {
    public static void chunkLoad(EntityMaid maid,int radius){
        List<ChunkPos> currentToLoad=new LinkedList<>();
        ChunkPos maidOn=new ChunkPos(maid.getOnPos());
        for(int i=-radius;i<=radius;i++){
            for(int j=-radius;j<=radius;j++){
                currentToLoad.add(new ChunkPos(maidOn.x+i,maidOn.z+j));
            }
        }
        for(ChunkPos pos:currentToLoad){
            if(maid.level() instanceof ServerLevel serverLevel){
                serverLevel.getChunkSource().addRegionTicket(TicketRegistry.MAID_TICKET,pos,2, Unit.INSTANCE,true);
            }
        }
    }
}
