package com.cirmuller.maidaddition.entity.sensor;

import com.cirmuller.maidaddition.MaidAddition;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;


public class SensorRegistry {

    public static final DeferredRegister<SensorType<?>> SENSOR_TYPE_DEFERRED_REGISTER=DeferredRegister.create(Registries.SENSOR_TYPE, MaidAddition.MODID);
    public static Supplier<SensorType<HandCrankSensor>> HAND_CRANK_SENSOR = SENSOR_TYPE_DEFERRED_REGISTER.register("hand_crank_sensor",
            ()->new SensorType<>(HandCrankSensor::new));
}
