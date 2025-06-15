package com.cirmuller.maidaddition.api;

import com.cirmuller.maidaddition.Utils.CraftingTasks.CraftingTask;
import com.cirmuller.maidaddition.Utils.CraftingTasks.ItemList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Stack;

public interface ICraftingAndCarryingCapability {
    void calculate();
    boolean isComplete();
    boolean addChest(BlockPos pos);
    boolean addMaterialsToGet(ItemStack stack);
    boolean addMaterialsToGet(ItemList list);
    Stack<CraftingTask> getCraftingTaskStack();
    void setDirty();
    boolean isDirty();

    boolean createNewCalculationThread(boolean keepChests);
    boolean createNewCalculationThread(List<BlockPos> chests, ItemList materialsToGet);
}
