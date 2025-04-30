package com.cirmuller.maidaddition.datagen;

import com.cirmuller.maidaddition.MaidAddition;
import dan200.computercraft.data.LanguageProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class DataGenEvent {
    private static Logger logger= LogManager.getLogger(MaidAddition.MODID);
    @SubscribeEvent
    public static void DataGenEventHandler(GatherDataEvent event){

        DataGenerator generator=event.getGenerator();
        PackOutput output=generator.getPackOutput();
        generator.addProvider(event.includeServer(),new AIData(output));


    }




}
