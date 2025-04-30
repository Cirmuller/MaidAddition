package com.cirmuller.maidaddition.capability;

import com.cirmuller.maidaddition.MaidAddition;
import com.cirmuller.maidaddition.api.IChunkLoadingCapability;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod.EventBusSubscriber()
public class CapabilityRegistry {
    @SubscribeEvent
    public static void attachCapability(AttachCapabilitiesEvent<Entity> event){
        Entity maid=event.getObject();
        if(maid instanceof EntityMaid&&!event.getObject().level().isClientSide){
            event.addCapability(new ResourceLocation(MaidAddition.MODID,"chunk_loading_capability"),new ChunkLoadingCapabilityProvider((EntityMaid) maid));
        }


    }

    @SubscribeEvent
    public static void registerCapability(RegisterCapabilitiesEvent event){
        event.register(IChunkLoadingCapability.class);
    }


}
