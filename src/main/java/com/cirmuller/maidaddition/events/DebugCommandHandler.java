package com.cirmuller.maidaddition.events;

import com.cirmuller.maidaddition.MaidAddition;
import com.cirmuller.maidaddition.command.DebugCommand;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

//@Mod.EventBusSubscriber
public class DebugCommandHandler {
    //@SubscribeEvent
    public static void onServerStarting(RegisterCommandsEvent event){
        CommandDispatcher<CommandSourceStack> dispatcher=event.getDispatcher();
        LiteralArgumentBuilder<CommandSourceStack> root= Commands.literal(MaidAddition.MODID);
        root.then(getCalculateCraftingCommand());
        dispatcher.register(root);

    }
    public static LiteralArgumentBuilder<CommandSourceStack> getCalculateCraftingCommand(){
        return Commands.literal("calculate_crafting").requires((commandSourceStack -> commandSourceStack.hasPermission(0))).executes(DebugCommand.instance);

    }
}
