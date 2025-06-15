package com.cirmuller.maidaddition.entity.memory;

import com.cirmuller.maidaddition.MaidAddition;
import com.cirmuller.maidaddition.Utils.CraftingTasks.Clipboard2ItemList;
import com.cirmuller.maidaddition.Utils.CraftingTasks.CraftingTask;
import com.cirmuller.maidaddition.Utils.CraftingTasks.StorageDefine2ChestsPos;
import com.cirmuller.maidaddition.threads.CalculateCraftingStackThread;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class CraftingAndCarryingMemory {
    private static final Logger logger= LogManager.getLogger(MaidAddition.MODID);



    CalculateCraftingStackThread thread;
    List<BlockPos> chestsFrom;
    List<BlockPos> chestsTo;

    ServerLevel level;
    EntityMaid maid;

    boolean isBackPackFull=false;
    ItemStack storageDefineBaubleFrom=null;
    ItemStack storageDefineBaubleTo=null;

    ItemStack clipboard =null;

    Stack<CraftingTask> craftingTasks=null;
    boolean isInitialized=false;

    public BlockPos walkTarget =null;
    boolean isOpeningChest=false;
    int openChestTickDelay=0;
    public CalculateCraftingStackThread.ChestPos chestPos;



    public static final int openChestDelay =100;

    public Stack<CraftingTask> getCraftingTasks(){
        if(!isInitialized){
            isInitialized=true;
            craftingTasks=thread.getCraftingTaskStack().stream().filter((task)->((task.getState()== CraftingTask.CraftingState.Crafting)||(task.isTargetItem))).collect(Collectors.toCollection(Stack::new));
        }
        return craftingTasks;
    }
    public void setChestPos(CalculateCraftingStackThread.ChestPos chestPos){
        this.chestPos=chestPos;
        this.walkTarget=chestPos.pos;
    }
    public CalculateCraftingStackThread.ChestPos getChestPos(){
        return chestPos;
    }
    public int getOpenChestTickDelay(){
        if(openChestTickDelay==openChestDelay){
            openChestTickDelay=0;
            return openChestDelay;
        }
        return openChestTickDelay++;
    }
    public void start(){
        chestsFrom= StorageDefine2ChestsPos.fromStorageDefine(storageDefineBaubleFrom);
        chestsTo=StorageDefine2ChestsPos.fromStorageDefine(storageDefineBaubleTo);

        thread=new CalculateCraftingStackThread(level,chestsFrom, Clipboard2ItemList.fromClipboard(clipboard));
        thread.start();
        logger.debug("Create a new thread to calculate");
    }
    public CraftingAndCarryingMemory(EntityMaid maid){
        this.level=(ServerLevel) maid.level();
        this.maid=maid;
    }
    public void setStorageDefineBaubleFrom(ItemStack storageDefineBaubleFrom){
        this.storageDefineBaubleFrom=storageDefineBaubleFrom;
    }
    public void setStorageDefineBaubleTo(ItemStack storageDefineBaubleTo){
        this.storageDefineBaubleTo=storageDefineBaubleTo;
    }
    public void setClipboard(ItemStack clipboard){
        this.clipboard = clipboard;
    }

    public boolean isInitialized(){
        return storageDefineBaubleFrom!=null&&storageDefineBaubleTo!=null&& clipboard !=null;
    }
    public boolean isCalculated(){
        return thread.isCompleted();
    }
    public boolean isBackPackFull(){
        return isBackPackFull;
    }
    public List<BlockPos> getChestsFrom(){
        return chestsFrom;
    }
    public boolean isOpeningChest(){
        return isOpeningChest;
    }
    public void setIsOpeningChest(boolean isOpeningChest){
        this.isOpeningChest=isOpeningChest;
    }
    public void setIsBackpackFull(boolean isBackpackFull){
        this.isBackPackFull=isBackpackFull;
    }
    public CraftingTask pop(){
        return thread.getCraftingTaskStack().pop();
    }
}
