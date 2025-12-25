package com.cirmuller.maidaddition.network;

import com.cirmuller.maidaddition.MaidAddition;
import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.Optional;

public class NetWorkHandler {
    public static final String VERSION="1.0.0";
    private static int ID=0;
    private static int nextID(){
        return ID++;
    }
    public static void init(final RegisterPayloadHandlersEvent event){
        final PayloadRegistrar registrar = event.registrar(VERSION).optional();
        registrar.playToServer(MaidChunkLoadingMessage.TYPE,MaidChunkLoadingMessage.STREAM_CODEC,MaidChunkLoadingMessage::handler);

    }
}
