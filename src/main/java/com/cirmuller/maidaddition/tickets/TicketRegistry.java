package com.cirmuller.maidaddition.tickets;

import com.cirmuller.maidaddition.configs.Config;
import com.cirmuller.maidaddition.entity.behaviour.ChunkLoadingBehaviour;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.Unit;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class TicketRegistry {
    private static final int lifespan= Config.UPDATE_RATE.get()+10;
    public static final TicketType<Unit> MAID_TICKET=TicketType.create("maid",(a,b)->0,lifespan);
}
