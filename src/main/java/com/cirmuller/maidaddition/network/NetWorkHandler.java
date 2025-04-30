package com.cirmuller.maidaddition.network;

import com.cirmuller.maidaddition.MaidAddition;
import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public class NetWorkHandler {
    public static final String VERSION="1.0.0";
    public static SimpleChannel CHANNEL= NetworkRegistry.newSimpleChannel(new ResourceLocation(MaidAddition.MODID, "message_network"),
            () -> VERSION, it -> it.equals(VERSION), it -> it.equals(VERSION));
    private static int ID=0;
    private static int nextID(){
        return ID++;
    }
    public static void init(){
            CHANNEL.registerMessage(nextID(), MaidChunkLoadingMessage.class,MaidChunkLoadingMessage::encode,MaidChunkLoadingMessage::decode,MaidChunkLoadingMessage::handler,
                    Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }
}
