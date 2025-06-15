package com.cirmuller.maidaddition.Utils.CraftingTasks;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.units.qual.C;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class CraftingTask extends AbstractCraftingTask{
    /**
     * 本类的作用是标记当前女仆的任务状态
     **/
    public enum CraftingState {
        IDLE,
        TAKING_MATERIAL,
        UNABLE_TO_FIND_MATERIAL,
        Crafting;
    }


    ItemStack itemToTakeOut;
    CraftingRecipe currentRecipe;
    CraftingState state;

    public boolean isTargetItem;
    public CraftingRecipe getCurrentRecipe(){
        return currentRecipe;
    }
    public CraftingTask(@NonNull ServerLevel level,
                                AbstractCraftingTask parentAbstractCraftingTask,
                                CraftingRecipe parentCraftingRecipe,
                                @Nullable Ingredient ingredientToCraft,
                                @Nullable Integer count){
        super(level,parentAbstractCraftingTask,parentCraftingRecipe,ingredientToCraft,count);
        state=CraftingState.IDLE;
        currentRecipe=null;
        itemToTakeOut=null;
        isTargetItem=false;
    }
    public CraftingTask(AbstractCraftingTask abstractCraftingTask){
        super(abstractCraftingTask);
        itemToTakeOut=null;
        currentRecipe=null;
        state=CraftingState.IDLE;
    }
    public ItemStack getItemToTakeOut(){
        return itemToTakeOut;
    }
    public CraftingState getState(){
        return state;
    }
    @Nullable
    public Stack<CraftingTask> createCraftingTasksStack(ItemList list){
        ItemStack itemInList=null;
        for(ItemStack itemStack:list){
            if(this.resultIngredient.test(itemStack)){
                itemInList=itemStack;
                break;
            }
        }

        if(itemInList!=null){
            Stack<CraftingTask> result=new Stack<>();
            if(this.count<=itemInList.getCount()){
                itemToTakeOut=new ItemStack(itemInList.getItem(),this.count);
                state=CraftingState.TAKING_MATERIAL;
                result.push(this);
                return result;
            }
            else{
                itemToTakeOut=itemInList.copy();
                ItemList listNext=list.copy();
                ItemStack toErase=listNext.getItemInList(itemInList);
                listNext.remove(toErase);
                state=CraftingState.TAKING_MATERIAL;
                result.push(this);
                CraftingTask craftingTaskNext=new CraftingTask(this.level,this,this.preRecipe,this.resultIngredient,this.count- itemInList.getCount());
                Stack<CraftingTask> task=craftingTaskNext.createCraftingTasksStack(listNext);
                result.addAll(task);
                return result;
            }
        }
        else{
            List<Stack<CraftingTask>> results=new ArrayList<>(allPossibleRoutes.size());
            for(CraftingRecipe recipe:allPossibleRoutes){
                List<CraftingTask> tasks=createCraftingTasksList(recipe);
                if(tasks==null){
                    return null;
                }

                Stack<CraftingTask> currentCraftingStackStack=new Stack<>();
                boolean isResultNull=false;
                for(CraftingTask task:tasks){
                    Stack<CraftingTask> stackOfCraftingTasks=task.createCraftingTasksStack(list.copy());
                    if(stackOfCraftingTasks==null){
                        isResultNull=true;
                        break;
                    }
                    currentCraftingStackStack.addAll(stackOfCraftingTasks);
                }

                if(isResultNull){
                    results.add(null);
                }
                else{
                    results.add(currentCraftingStackStack);
                }

            }

            Stack<CraftingTask> minResult=null;
            for(Stack<CraftingTask> stack:results){
                if(stack==null){
                    continue;
                }

                if(minResult==null||minResult.size()>stack.size()){
                    minResult=stack;
                }
            }

            if(minResult!=null){
                this.state=CraftingState.Crafting;
                int index=results.indexOf(minResult);
                this.currentRecipe=allPossibleRoutes.get(index);
                minResult.add(0,this);
                return minResult;
            }
            else{
                return null;
            }

        }
    }

    @Nullable
    public List<CraftingTask> createCraftingTasksList(CraftingRecipe recipe){
        List<AbstractCraftingTask> tmp=this.createAbstractCraftingTasksList(recipe);
        if(tmp==null){
            return null;
        }
        else{
            List<CraftingTask> result=new ArrayList<>(tmp.size());
            for(AbstractCraftingTask task:tmp){
                result.add(new CraftingTask(task));
            }
            return result;
        }
    }
}
