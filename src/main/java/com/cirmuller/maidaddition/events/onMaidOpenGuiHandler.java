package com.cirmuller.maidaddition.events;

import com.cirmuller.maidaddition.MaidAddition;
import com.cirmuller.maidaddition.MaidPluginIn;
import com.cirmuller.maidaddition.entity.memory.CanChunkLoadedMemory;
import com.cirmuller.maidaddition.network.MaidChunkLoadingMessage;
import com.cirmuller.maidaddition.network.NetWorkHandler;
import com.github.tartaricacid.touhoulittlemaid.api.event.client.MaidContainerGuiEvent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Field;
import java.util.List;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(value=Dist.CLIENT)
public class onMaidOpenGuiHandler {
    private static final ResourceLocation chunkLoadedButtonResource=new ResourceLocation(MaidAddition.MODID,"textures/gui/maid_gui_button.png");
    private static final String buttonName=MaidAddition.MODID+".chunk_loading_button";
    @SubscribeEvent
    public static void onMaidOpenGui(MaidContainerGuiEvent.Init event){
        EntityMaid maid=event.getGui().getMaid();
        int leftPos=event.getLeftPos();
        int topPos=event.getTopPos();
        ImageButton chunkLoadedButton=new ImageButton(leftPos + 8, topPos + 26, 9, 9, 0, 0, 10, chunkLoadedButtonResource,
                (button)->{

                    CanChunkLoadedMemory memory=maid.getData(MaidPluginIn.canChunkLoadedData);
                    boolean isChunkLoaded=memory!=null?memory.canChunkLoaded():false;

                    if(!isChunkLoaded){
                        isChunkLoaded=true;
                        /*
                        Memory数据不会在客户端与服务端自动同步，所以我们要手动发包
                         */
                        NetWorkHandler.CHANNEL.sendToServer(new MaidChunkLoadingMessage(maid.getId(),isChunkLoaded));
                    }
                    else{
                        isChunkLoaded=false;
                        /*
                        Memory数据不会在客户端与服务端自动同步，所以我们要手动发包
                         */
                        NetWorkHandler.CHANNEL.sendToServer(new MaidChunkLoadingMessage(maid.getId(),isChunkLoaded));
                    }


                });
        event.addButton(buttonName,chunkLoadedButton);
    }

    @SubscribeEvent
    public static void onRenderTooltip(MaidContainerGuiEvent.Tooltip event)throws NoSuchFieldException,IllegalAccessException{
        AbstractWidget chunkLoadedButton=event.getButton(buttonName);
        EntityMaid maid=event.getGui().getMaid();
        GuiGraphics graphics=event.getGraphics();
        int mouseX=event.getMouseX();
        int mouseY=event.getMouseY();

        Class abstractMaidContainerGuiClass= Screen.class;
        Field fontField=abstractMaidContainerGuiClass.getDeclaredField("font");
        fontField.setAccessible(true);
        Font font=(Font)fontField.get(event.getGui());

        if(chunkLoadedButton.isHovered()){
            MutableComponent context;
            CanChunkLoadedMemory memory=maid.getData(MaidPluginIn.canChunkLoadedData);
            boolean isChunkLoaded=memory!=null?memory.canChunkLoaded():false;
            if(isChunkLoaded) {
                context= Component.literal("")
                        .append(Component.translatable("tooltips." + MaidAddition.MODID + ".info.chunk_loading.true"));
            }
            else {
                context=Component.literal("")
                        .append(Component.translatable("tooltips." + MaidAddition.MODID + ".info.chunk_loading.false"));
            }

            graphics.renderComponentTooltip(font, List.of(context),mouseX,mouseY);
        }
    }
}
