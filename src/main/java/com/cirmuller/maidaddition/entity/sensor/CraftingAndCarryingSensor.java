package com.cirmuller.maidaddition.entity.sensor;

import com.cirmuller.maidaddition.configs.Config;
import com.cirmuller.maidaddition.entity.memory.CraftingAndCarryingMemory;
import com.cirmuller.maidaddition.entity.memory.MemoryRegistry;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.inventory.handler.BaubleItemHandler;
import com.simibubi.create.AllBlocks;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.wrapper.EntityHandsInvWrapper;
import studio.fantasyit.maid_storage_manager.items.StorageDefineBauble;
import studio.fantasyit.maid_storage_manager.registry.ItemRegistry;

import java.util.Set;

public class CraftingAndCarryingSensor extends Sensor<EntityMaid> {
    private static final int scanRate= Config.UPDATE_RATE.get();
    public CraftingAndCarryingSensor(){
        super(scanRate);
    }

    @Override
    protected void doTick(ServerLevel serverLevel, EntityMaid maid) {
        BaubleItemHandler baubles=maid.getMaidBauble();
        CraftingAndCarryingMemory memory=maid.getBrain().getMemory(MemoryRegistry.CRAFTING_AND_CARRYING_MEMORY.get()).orElse(null);
        if(memory==null){
            memory=new CraftingAndCarryingMemory(maid);
            maid.getBrain().setMemory(MemoryRegistry.CRAFTING_AND_CARRYING_MEMORY.get(),memory);
        }
        for(int i=0;i<baubles.getSlots();i++){
            ItemStack stack=baubles.getStackInSlot(i);
            if(stack.getItem().equals(ItemRegistry.STORAGE_DEFINE_BAUBLE.get())){
                switch(StorageDefineBauble.getMode(stack)){
                    case REMOVE:
                        memory.setStorageDefineBaubleFrom(stack.copy());
                        break;
                    case APPEND:
                        memory.setStorageDefineBaubleTo(stack.copy());
                        break;
                    default:
                }
            }
        }

        EntityHandsInvWrapper wrapper= maid.getHandsInvWrapper();
        ItemStack clipboard=wrapper.getStackInSlot(0);
        if(clipboard.getItem().equals(AllBlocks.CLIPBOARD.asItem())){
            memory.setClipboard(clipboard.copy());
        }

    }

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return Set.of(MemoryRegistry.CRAFTING_AND_CARRYING_MEMORY.get());
    }
}
