package com.cirmuller.maidaddition.threads;

import com.cirmuller.maidaddition.Utils.CraftingTasks.CraftingTask;
import com.cirmuller.maidaddition.Utils.CraftingTasks.ItemList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.checkerframework.checker.units.qual.C;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class CalculateCraftingStackThread extends Thread{
    public static class ChestPos{
        public int index;
        public BlockPos pos;

        @Nullable
        public static ChestPos findItemInChests(ServerLevel level, List<BlockPos> chests, Item item){
            ChestPos result=new ChestPos();
            result.index=-1;
            result.pos=null;
            for(BlockPos chest:chests){
                BlockEntity chestEntity=level.getBlockEntity(chest);
                if(chestEntity!=null){
                    LazyOptional<IItemHandler> capability=chestEntity.getCapability(ForgeCapabilities.ITEM_HANDLER);
                    capability.ifPresent((cap)-> {
                        int sz=cap.getSlots();
                        for(int i=0;i<sz;i++){
                            ItemStack stack=cap.getStackInSlot(i);
                            if(stack.getItem().equals(item)){
                                result.index=i;
                                result.pos=chest;
                                break;
                            }
                        }
                    }
                    );
                    if(result.index!=-1){
                        break;
                    }

                }
            }
            if(result.index==-1){
                return null;
            }
            else{
                return result;
            }

        }
    }
    List<BlockPos> chests;
    ItemList materialsHave;
    ServerLevel level;
    Stack<CraftingTask> tasks;
    ItemList materialsToGet;
    boolean completedFinished;
    public CalculateCraftingStackThread(ServerLevel level,List<BlockPos> chests,ItemList materialsToGet){
        this.chests=chests;
        this.level=level;
        this.materialsToGet=materialsToGet;
        materialsHave=null;
        tasks=null;
        completedFinished=false;
    }

    public CalculateCraftingStackThread(ServerLevel level){
        this(level,null,null);
        chests=new ArrayList<>();
        materialsToGet=new ItemList();
    }
    public CalculateCraftingStackThread(ServerLevel level,List<BlockPos> chests){
        this(level,chests,null);
        materialsToGet=new ItemList();
    }
    public boolean addChest(BlockPos pos){
        BlockEntity chest=level.getBlockEntity(pos);
        if(chest==null||!chest.getCapability(ForgeCapabilities.ITEM_HANDLER).isPresent()){
            return false;
        }
        if(chests.contains(pos)){
            return false;
        }
        chests.add(pos);
        return true;
    }

    public boolean addMaterialsToGet(ItemStack stack){
        return materialsToGet.add(stack);
    }
    public boolean addMaterialsToGet(ItemList list){
        return materialsToGet.addAll(list);
    }

    /**
    直接调用该函数是单线程计算
     **/
    public Stack<CraftingTask> getCraftingTaskStack(){
        if(tasks!=null){
            return tasks;
        }

        if(materialsHave==null){
            materialsHave=getItemList(chests);
        }
        tasks=new Stack<>();
        ItemList itemsLeft=materialsHave.copy();
        for(ItemStack itemStack:materialsToGet){
            CraftingTask task=new CraftingTask(level,null,null, Ingredient.of(itemStack), itemStack.getCount());
            Stack<CraftingTask> tasks=task.createCraftingTasksStack(itemsLeft);
            if(tasks!=null){
                tasks.get(0).isTargetItem=true;
                this.tasks.addAll(tasks);
                for(CraftingTask craftingTask:tasks){
                    if(craftingTask.getItemToTakeOut()!=null){
                        itemsLeft.remove(craftingTask.getItemToTakeOut());
                    }
                }
            }
        }
        completedFinished=true;
        return tasks;
    }


    public CraftingTask getLastTask(){
        return tasks.peek();
    }

    @Override
    public void run() {
        this.getCraftingTaskStack();
    }

    public boolean isCompleted(){
        return completedFinished;
    }

    protected ItemList getItemList(List<BlockPos> materialChests){
        ItemList result=new ItemList();
        for(BlockPos pos:materialChests){
            BlockEntity chest=level.getBlockEntity(pos);
            if(chest==null){
                continue;
            }
            LazyOptional<IItemHandler> capability=chest.getCapability(ForgeCapabilities.ITEM_HANDLER);
            capability.ifPresent((cap)->{
                int sz=cap.getSlots();
                for(int i=0;i<sz;i++){
                    result.add(cap.getStackInSlot(i));
                }
            });

        }
        return result;
    }
    public List<BlockPos> getChests(){
        return chests;
    }
}
