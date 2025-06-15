package com.cirmuller.maidaddition.Utils.CraftingTasks;

import it.unimi.dsi.fastutil.ints.IntLists;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import studio.fantasyit.maid_storage_manager.storage.Target;

import java.util.ArrayList;
import java.util.List;

import static studio.fantasyit.maid_storage_manager.items.StorageDefineBauble.TAG_STORAGES;

public class StorageDefine2ChestsPos {
    public static List<BlockPos> fromStorageDefine(ItemStack stack){
        CompoundTag tag=stack.getTag();
        if(tag==null||!tag.contains(TAG_STORAGES)){
            return new ArrayList<>();
        }
        ListTag chestsTag=tag.getList(TAG_STORAGES,10);

        int sz=chestsTag.size();
        List<BlockPos> result=new ArrayList<>(sz);
        for(int i=0;i<sz;i++){
            Target storage=Target.fromNbt(chestsTag.getCompound(i));
            result.add(storage.pos);
        }
        return result;
    }

}
