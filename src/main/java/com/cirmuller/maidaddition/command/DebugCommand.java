package com.cirmuller.maidaddition.command;

import com.cirmuller.maidaddition.MaidAddition;
import com.cirmuller.maidaddition.Utils.CraftingTasks.ItemList;
import com.cirmuller.maidaddition.threads.CalculateMaterialsLackedThread;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DebugCommand implements Command<CommandSourceStack> {
    public static DebugCommand instance=new DebugCommand();
    public static Logger logger= LogManager.getLogger(MaidAddition.MODID);
    @Override
    public int run(CommandContext<CommandSourceStack> context)throws CommandSyntaxException{
        ServerLevel level=context.getSource().getLevel();
        ItemList materials=new CalculateMaterialsLackedThread(level,getMaterialsHave(),getMaterialsToCraft()).getMaterialsLacked();
        logger.debug("The materials list is the following:\n"+materials.getString());
        return SINGLE_SUCCESS;
    }

    ItemList getMaterialsHave(){
        ItemList result=new ItemList();
        result.add(new ItemStack(Items.IRON_INGOT,30));
        result.add(new ItemStack(Items.BEACON,100));
        result.add(new ItemStack(Items.BLACK_WOOL,50));
        return result;
    }

    ItemList getMaterialsToCraft(){
        ItemList result=new ItemList();
        result.add(new ItemStack(Items.IRON_BLOCK,32));
        result.add(new ItemStack(Items.IRON_DOOR,32));
        result.add(new ItemStack(Items.BEACON,100));
        result.add(new ItemStack(Items.IRON_PICKAXE,3));
        result.add(new ItemStack(Items.BLACK_WOOL,30));
        result.add(new ItemStack(Items.BLUE_WOOL,10));
        result.add(new ItemStack(Items.WHITE_WOOL,20));
        return result;
    }

}
