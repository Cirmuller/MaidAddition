package com.cirmuller.maidaddition.network;

import com.cirmuller.maidaddition.MaidAddition;
import com.cirmuller.maidaddition.entity.memory.MemoryRegistry;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;

import java.util.function.Supplier;

public class MaidChunkLoadingMessage {
    private int maidId;
    private boolean usable;

    public MaidChunkLoadingMessage(int maidId,boolean usable){
        this.maidId=maidId;
        this.usable=usable;
    }

    public static void encode(MaidChunkLoadingMessage message, FriendlyByteBuf buffer){
        buffer.writeInt(message.maidId);
        buffer.writeBoolean(message.usable);
    }
    public static MaidChunkLoadingMessage decode(FriendlyByteBuf buffer){
        return new MaidChunkLoadingMessage(buffer.readInt(),buffer.readBoolean());
    }

    public static void handler(MaidChunkLoadingMessage message, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context= contextSupplier.get();
        if(context.getDirection().getReceptionSide().isServer()){
            context.enqueueWork(
                    ()->{
                        ServerPlayer player=context.getSender();
                        if(player==null){
                            return;
                        }
                        Entity entity=player.level().getEntity(message.maidId);
                        if(entity instanceof EntityMaid entityMaid&&entityMaid.isOwnedBy(player)){
                            LogManager.getLogger(MaidAddition.MODID).info("Now the Memory is "+message.usable);
                            entityMaid.getBrain().setMemory(MemoryRegistry.CAN_CHUNK_LOADED.get(),message.usable);
                        }

                    }
            );


        }
        context.setPacketHandled(true);
    }
}
