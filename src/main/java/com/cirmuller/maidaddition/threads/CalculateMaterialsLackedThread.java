package com.cirmuller.maidaddition.threads;

import com.cirmuller.maidaddition.Utils.CraftingTasks.AbstractCraftingTask;
import com.cirmuller.maidaddition.Utils.CraftingTasks.ItemList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;

public class CalculateMaterialsLackedThread extends Thread{
    ItemList materialsHave;
    ItemList materialsLacked;
    ItemList materialsToCraft;

    ServerLevel level;
    boolean isCalculated;
    public CalculateMaterialsLackedThread(ServerLevel level,ItemList materialsHave,ItemList materialsToCraft){
        this.materialsHave=materialsHave.copy();
        this.materialsToCraft=materialsToCraft.copy();
        isCalculated=false;
        this.level=level;
    }
    public CalculateMaterialsLackedThread(ServerLevel level,ItemList materialsToCraft){
        this(level,new ItemList(),materialsToCraft);
    }
    private ItemList getMaterialsUsedToCraft(){
        ItemList result=new ItemList();
        for (ItemStack itemStack : materialsToCraft) {
            result.addAll(new AbstractCraftingTask(level,itemStack.getItem(),itemStack.getCount()).getMaterialsList());
        }
        return result;
    }
    private ItemList getMaterialDecomposition(){
        ItemList result=new ItemList();
        for (ItemStack itemStack : materialsHave) {
            result.addAll(new AbstractCraftingTask(level,itemStack.getItem(),itemStack.getCount()).getMaterialsList());
        }
        return result;
    }

    @Override
    public void run() {
        getMaterialsLacked();
    }

    public ItemList getMaterialsLacked(){
        if (!isCalculated) {
            materialsLacked = ItemList.subtract(getMaterialsUsedToCraft(), getMaterialDecomposition());
            isCalculated=true;
        }
        return materialsLacked;
    }
}
