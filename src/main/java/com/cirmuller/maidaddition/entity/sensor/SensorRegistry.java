package com.cirmuller.maidaddition.entity.sensor;

import com.cirmuller.maidaddition.MaidAddition;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SensorRegistry {

    public static final DeferredRegister<SensorType<?>> SENSOR_TYPE_DEFERRED_REGISTER=DeferredRegister.create(ForgeRegistries.SENSOR_TYPES, MaidAddition.MODID);
    public static RegistryObject<SensorType<HandCrankSensor>> HAND_CRANK_SENSOR = SENSOR_TYPE_DEFERRED_REGISTER.register("hand_crank_sensor",
            ()->new SensorType<>(HandCrankSensor::new));
    public static RegistryObject<SensorType<CraftingAndCarryingSensor>> CRAFTING_AND_CARRYING_SENSOR=SENSOR_TYPE_DEFERRED_REGISTER.register("crafting_and_carrying_sensor",
            ()->new SensorType<>(CraftingAndCarryingSensor::new));
}
