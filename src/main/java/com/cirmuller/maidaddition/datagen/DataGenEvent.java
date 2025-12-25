package com.cirmuller.maidaddition.datagen;

import com.cirmuller.maidaddition.MaidAddition;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@EventBusSubscriber(bus= EventBusSubscriber.Bus.MOD)
public class DataGenEvent {
    private static Logger logger= LogManager.getLogger(MaidAddition.MODID);
    @SubscribeEvent
    public static void DataGenEventHandler(GatherDataEvent event){

        DataGenerator generator=event.getGenerator();
        PackOutput output=generator.getPackOutput();
        generator.addProvider(event.includeServer(),new AIData(output));


    }




}
