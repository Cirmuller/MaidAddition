package com.cirmuller.maidaddition.Utils.CraftingTasks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ItemList extends LinkedList<ItemStack> {

    public ItemList(){
        super();
    }
    @Override
    public boolean add(ItemStack itemStack) {
        for(ItemStack item:this){
            if(item.getItem().equals(itemStack.getItem())){
                item.setCount(item.getCount()+itemStack.getCount());
                return true;
            }
        }
        return super.add(itemStack.copy());
    }

    @Override
    public boolean addAll(Collection<? extends ItemStack> c) {
        if(c.isEmpty()){
            return false;
        }
        for(ItemStack item:c){
            this.add(item);
        }
        return true;
    }

    @Nullable
    public ItemStack getItemInList(ItemStack item){
        return this.getItemInList(item.getItem());
    }

    @Override
    public boolean remove(Object o) {
        if(o instanceof ItemStack targetItemStack){
            for(ItemStack itemStack:this){
                if(itemStack.getItem().equals(targetItemStack.getItem())){
                    if(itemStack.getCount()>targetItemStack.getCount()){
                        itemStack.setCount(itemStack.getCount()-targetItemStack.getCount());
                        return true;
                    }
                    else{
                        return super.remove(itemStack);
                    }
                }
            }
            return false;
        }
        else{
            return false;
        }
    }

    @Nullable
    public ItemStack getItemInList(Item item){
        for(ItemStack itemStack:this){
            if(itemStack.getItem().equals(item)){
                return itemStack;
            }
        }
        return null;
    }

    public int getTotalCount(){
        int result=0;
        for(ItemStack itemStack:this){
            result+=itemStack.getCount();
        }
        return result;
    }
    public static ItemList getAbundantItemList(List<ItemList> list){
        assert !list.isEmpty();

        return list.stream().max((a,b)->{
            if(a.getTotalCount()>b.getTotalCount()){
                return 1;
            }
            else if(a.getTotalCount()<b.getTotalCount()){
                return -1;
            }else{
                return 0;
            }
        }).get();
    }

    public String getString(){
        StringBuilder stringBuilder=new StringBuilder(256);
        int count=0;
        for(ItemStack itemStack:this){
            count++;
            stringBuilder.append(String.format("%-5s : %5d",itemStack.getDisplayName().getString(),itemStack.getCount()));
            if(count%5==0){
                stringBuilder.append("\n");
            }
            else{
                stringBuilder.append("     ");
            }
        }
        return stringBuilder.toString();
    }

    //深拷贝
    public ItemList copy(){
        ItemList result=new ItemList();
        for(ItemStack item:this){
            result.add(item.copy());
        }
        return result;
    }


    public static ItemList subtract(ItemList A, ItemList B){
        ItemList result=new ItemList();
        for(ItemStack itemStack:A){
            ItemStack itemInB=B.getItemInList(itemStack);
            if(itemInB==null){
                result.add(itemStack.copy());
            }
            else if(itemStack.getCount()>itemInB.getCount()){
                result.add(new ItemStack(itemStack.getItem(),itemStack.getCount()-itemInB.getCount()));
            }
        }
        return result;
    }

    public static ItemList getItemListFromChests(Level level, List<BlockPos> materialChests){
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
}
