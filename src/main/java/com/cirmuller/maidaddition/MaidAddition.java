package com.cirmuller.maidaddition;

import com.cirmuller.maidaddition.configs.Config;
import com.cirmuller.maidaddition.datagen.AIData;
import com.cirmuller.maidaddition.entity.memory.MemoryRegistry;
import com.cirmuller.maidaddition.entity.sensor.SensorRegistry;
import com.cirmuller.maidaddition.network.NetWorkHandler;
import com.cirmuller.maidaddition.threads.CalculateTaskThread;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(MaidAddition.MODID)
public class MaidAddition {
    public static final String MODID="maidaddition";

    public MaidAddition(){
        IEventBus bus=FMLJavaModLoadingContext.get().getModEventBus();
        SensorRegistry.SENSOR_TYPE_DEFERRED_REGISTER.register(bus);
        MemoryRegistry.MEMORY_MODULE_TYPE_DEFERRED_REGISTER.register(bus);
        CalculateTaskThread.init();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON,Config.COMMON_CONFIG);
    }
}
