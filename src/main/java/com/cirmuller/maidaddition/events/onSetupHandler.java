package com.cirmuller.maidaddition.events;

import com.cirmuller.maidaddition.network.NetWorkHandler;
import com.cirmuller.maidaddition.threads.CalculateTaskThread;
import com.mojang.brigadier.Command;
import dan200.computercraft.client.ClientRegistry;
import net.minecraft.server.commands.GameRuleCommand;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static com.cirmuller.maidaddition.events.DebugButtonHandler.KEY_DEBUG;


@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class onSetupHandler {

    @SubscribeEvent
    public static void setupAI(FMLCommonSetupEvent event){
        event.enqueueWork(NetWorkHandler::init);
    }


    /**
    *此方法应当只在客户端调用
    @SubscribeEvent
    public static void onClientSetupEvent(RegisterKeyMappingsEvent event){
        event.register(KEY_DEBUG);
    }
    */
}
