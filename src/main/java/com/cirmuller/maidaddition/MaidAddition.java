package com.cirmuller.maidaddition;

import com.cirmuller.maidaddition.configs.Config;
import com.cirmuller.maidaddition.datagen.AIData;
import com.cirmuller.maidaddition.entity.memory.MemoryRegistry;
import com.cirmuller.maidaddition.entity.sensor.SensorRegistry;
import com.cirmuller.maidaddition.network.NetWorkHandler;
import com.cirmuller.maidaddition.threads.CalculateTaskThread;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(MaidAddition.MODID)
public class MaidAddition {
    public static final String MODID="maidaddition";

    public MaidAddition(IEventBus bus,ModContainer container){
        SensorRegistry.SENSOR_TYPE_DEFERRED_REGISTER.register(bus);
        MemoryRegistry.MEMORY_MODULE_TYPE_DEFERRED_REGISTER.register(bus);
        container.registerConfig(ModConfig.Type.COMMON,Config.COMMON_CONFIG);
        bus.addListener(NetWorkHandler::init);
    }
}
