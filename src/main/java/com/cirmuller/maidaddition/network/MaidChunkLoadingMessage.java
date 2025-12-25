package com.cirmuller.maidaddition.network;

import com.cirmuller.maidaddition.MaidAddition;
import com.cirmuller.maidaddition.entity.memory.CanChunkLoadedMemory;
import com.cirmuller.maidaddition.entity.memory.MemoryRegistry;
import com.cirmuller.maidaddition.MaidPluginIn;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.network.message.ItemBreakPackage;
import com.github.tartaricacid.touhoulittlemaid.network.message.MaidModelPackage;
import com.sun.jna.platform.win32.WinDef;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.apache.logging.log4j.LogManager;

import java.util.function.Supplier;

import static com.github.tartaricacid.touhoulittlemaid.util.ResourceLocationUtil.getResourceLocation;


public record MaidChunkLoadingMessage(int maidId, boolean usable) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MaidChunkLoadingMessage> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.tryBuild(MaidAddition.MODID,"maid_chunk_loading_message"));
    public static final StreamCodec<ByteBuf, MaidChunkLoadingMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            MaidChunkLoadingMessage::maidId,
            ByteBufCodecs.BOOL,
            MaidChunkLoadingMessage::usable,
            MaidChunkLoadingMessage::new
    );


    public static void handler(MaidChunkLoadingMessage message, IPayloadContext context){
            context.enqueueWork(
                    ()->{
                        ServerPlayer player=(ServerPlayer) context.player();
                        if(player==null){
                            return;
                        }
                        Entity entity=player.level().getEntity(message.maidId);
                        if(entity instanceof EntityMaid entityMaid&&entityMaid.isOwnedBy(player)){
                            //LogManager.getLogger(MaidAddition.MODID).debug("Now the canChunkLoadMemory is "+message.usable);
                            entityMaid.getBrain().setMemory(MemoryRegistry.CAN_CHUNK_LOADED.get(),message.usable);
                            entityMaid.setAndSyncData(MaidPluginIn.canChunkLoadedData, new CanChunkLoadedMemory(message.usable));
                        }
                    }
            );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return null;
    }

}
