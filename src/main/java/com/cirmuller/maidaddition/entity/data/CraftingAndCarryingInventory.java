package com.cirmuller.maidaddition.entity.data;

import com.cirmuller.maidaddition.Utils.CraftingTasks.CraftingTask;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.Stack;

public record CraftingAndCarryingInventory(ItemStack materialToHandler,ItemStack materialHandled) {
    public static final Codec<CraftingAndCarryingInventory> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.CODEC.fieldOf("material list").forGetter(CraftingAndCarryingInventory::materialToHandler),
            ItemStack.CODEC.fieldOf("material list handled").forGetter(CraftingAndCarryingInventory::materialHandled)
    ).apply(instance,CraftingAndCarryingInventory::new));

}
