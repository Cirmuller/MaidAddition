package com.cirmuller.maidaddition.Utils.CraftingTasks;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllBogeyStyles;
import com.simibubi.create.content.equipment.clipboard.ClipboardEntry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;

public class Clipboard2ItemList {
    @Nullable
    public static ItemList fromClipboard(ItemStack itemStack){
        if(itemStack==null||!AllBlocks.CLIPBOARD.is(itemStack.getItem())){
            return null;
        }
        ItemList list=new ItemList();
        List<List<ClipboardEntry>> pages=ClipboardEntry.readAll(itemStack);
        for(List<ClipboardEntry> page:pages){
            for(ClipboardEntry entry:page){
                //list.add(new ItemStack(entry.icon.getItem(), entry.itemAmount));
                list.add(entry.icon);
            }
        }
        return list;
    }
}
