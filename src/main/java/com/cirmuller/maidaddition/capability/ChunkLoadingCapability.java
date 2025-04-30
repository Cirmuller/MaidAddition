package com.cirmuller.maidaddition.capability;

import com.cirmuller.maidaddition.MaidAddition;
import com.cirmuller.maidaddition.api.IChunkLoadingCapability;
import com.cirmuller.maidaddition.tickets.TicketRegistry;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ForcedChunksSavedData;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.world.ForgeChunkManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/*
注意：ChunkLoadingCapabilityProvider与ChunkLoadingCapability都只能在服务端调用
 */
public class ChunkLoadingCapability implements IChunkLoadingCapability {
    public static Logger logger= LogManager.getLogger(MaidAddition.MODID);
    EntityMaid maid;


    public ChunkLoadingCapability(EntityMaid maid){
        this.maid=maid;


    }


    @Override
    public EntityMaid getMaid() {
        return maid;
    }

    /*
        We update the chunks near the maid with $L^\infty$ distance.
     */
    @Override
    public void updateChunkLoading(int radius){
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
