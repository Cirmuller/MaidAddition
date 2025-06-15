package com.cirmuller.maidaddition.entity.behaviour;

import com.cirmuller.maidaddition.MaidAddition;
import com.cirmuller.maidaddition.Utils.CraftingTasks.CraftingTask;
import com.cirmuller.maidaddition.entity.memory.CraftingAndCarryingMemory;
import com.cirmuller.maidaddition.entity.memory.MemoryRegistry;
import com.github.tartaricacid.touhoulittlemaid.entity.chatbubble.implement.TextChatBubbleData;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import studio.fantasyit.maid_storage_manager.storage.ItemHandler.SimulateTargetInteractHelper;

import java.util.Map;
import static com.cirmuller.maidaddition.threads.CalculateCraftingStackThread.*;
public class CraftingAndCarryingBehaviour extends Behavior<EntityMaid> {
    private final float speedModifier=0.8f;
    private final Component lackOfSpace=Component.translatable(MaidAddition.MODID+".lack_of_space");
    private final Component lackOfMaterial=Component.translatable(MaidAddition.MODID+".lack_of_material");
    private final Component donotMoveChest=Component.translatable(MaidAddition.MODID+".donot_move_chest");
    public CraftingAndCarryingBehaviour() {
        super(Map.of(MemoryRegistry.CRAFTING_AND_CARRYING_MEMORY.get(),MemoryStatus.REGISTERED));
    }

    @Override
    protected void start(ServerLevel pLevel, EntityMaid maid, long pGameTime) {
        CraftingAndCarryingMemory memory=maid.getBrain().getMemory(MemoryRegistry.CRAFTING_AND_CARRYING_MEMORY.get()).orElse(null);
        assert memory!=null;
        memory.start();
    }

    @Override
    protected void tick(ServerLevel pLevel, EntityMaid maid, long pGameTime) {
        CraftingAndCarryingMemory memory=maid.getBrain().getMemory(MemoryRegistry.CRAFTING_AND_CARRYING_MEMORY.get()).orElse(null);
        assert memory!=null;
        if(!memory.isCalculated()){
            return;
        }

        if(memory.isBackPackFull()){
            if(memory.walkTarget ==null&&(!memory.getChestsFrom().isEmpty())){
                memory.walkTarget =memory.getChestsFrom().get(0);
            }
            this.unloadedBackPack(pLevel,maid,pGameTime);
            return;
        }






    }

    private void doCurrentTask(ServerLevel level,EntityMaid maid,long gameTime){
        CraftingAndCarryingMemory memory=maid.getBrain().getMemory(MemoryRegistry.CRAFTING_AND_CARRYING_MEMORY.get()).orElse(null);
        assert memory!=null;
        CraftingTask currentTask=memory.getCraftingTasks().peek();
        //TODO 需要完成女仆任务代码编写
        switch(currentTask.getState()){
            case TAKING_MATERIAL:
                /**
                 * 当目前walkTarget==null时，需要初始化
                 */
                if(memory.walkTarget==null){
                    ChestPos chestPos=ChestPos.findItemInChests(level,memory.getChestsFrom(),currentTask.getItemToTakeOut().getItem());
                    if(chestPos==null){
                        maid.getChatBubbleManager().addChatBubble(TextChatBubbleData.type2(lackOfMaterial));
                        memory.pop();
                        return;
                    }
                    else{
                        memory.setChestPos(chestPos);
                    }
                }
                /**
                 * 目前walkTarget!=null
                 */
                maid.getBrain().setMemory(MemoryModuleType.WALK_TARGET,new WalkTarget(memory.walkTarget,speedModifier,0));


                /**
                 * 当女仆走到箱子附近时
                 */
                if(maid.getOnPos().closerThan(memory.walkTarget,2)){
                    /**
                     * 当女仆未打开箱子时，需要打开箱子
                     */
                    if(!memory.isOpeningChest()){
                        int tickDelay=memory.getOpenChestTickDelay();
                        if(tickDelay==0){
                            (new SimulateTargetInteractHelper(maid, memory.walkTarget, null, level)).open();
                        }
                        else if(tickDelay==CraftingAndCarryingMemory.openChestDelay){
                            memory.setIsOpeningChest(true);
                        }

                    }
                    else{
                        /**
                         * 当女仆已经打开箱子时，从箱子中取出物品
                         */
                        BlockEntity chestEntity=level.getBlockEntity(memory.walkTarget);
                        if(chestEntity==null){
                            memory.walkTarget=null;
                            memory.chestPos=null;
                            maid.getChatBubbleManager().addChatBubble(TextChatBubbleData.type2(donotMoveChest));
                            return;
                        }
                        LazyOptional<IItemHandler> capability=chestEntity.getCapability(ForgeCapabilities.ITEM_HANDLER);
                        if(!capability.isPresent()){
                            memory.walkTarget=null;
                            memory.chestPos=null;
                            maid.getChatBubbleManager().addChatBubble(TextChatBubbleData.type2(donotMoveChest));
                            return;
                        }

                        capability.ifPresent((cap)->{
                            /**
                             * 女仆从箱子中取出物品
                             */
                            if(memory.getChestPos().index<cap.getSlots()){
                                ItemStack stackInChest=cap.extractItem(memory.chestPos.index,currentTask.getItemToTakeOut().getCount(),false);
                                ItemStack remain=insertItemToBackpack(maid.getAvailableBackpackInv(),stackInChest);
                                if(remain.isEmpty()){
                                    if(stackInChest.getCount()==currentTask.getItemToTakeOut().getCount()){
                                        memory.pop();
                                        memory.walkTarget=null;
                                        memory.chestPos=null;
                                    }
                                    else{
                                        memory.walkTarget=null;
                                        memory.chestPos=null;
                                        currentTask.getItemToTakeOut().setCount(currentTask.getItemToTakeOut().getCount()-stackInChest.getCount());
                                    }

                                }else{
                                    cap.insertItem(memory.chestPos.index,remain,false);
                                    while(memory.getCraftingTasks().peek().getState()!= CraftingTask.CraftingState.Crafting){
                                        memory.getCraftingTasks().pop();
                                    }
                                    memory.setIsBackpackFull(true);
                                    memory.walkTarget=null;
                                    memory.chestPos=null;

                                }
                            }
                            else{
                                memory.walkTarget=null;
                                memory.chestPos=null;
                                maid.getChatBubbleManager().addChatBubble(TextChatBubbleData.type2(donotMoveChest));
                            }


                        });


                    }
                }
                break;
            case Crafting:

        }


    }


    private ItemStack insertItemToBackpack(IItemHandler backpack,ItemStack stack){
        ItemStack remain=stack;
        for(int i=0;i< backpack.getSlots();i++){
            remain=backpack.insertItem(i,remain,false);
            if(remain.isEmpty()){
                return ItemStack.EMPTY;
            }

        }
        return remain;

    }


    private void unloadedBackPack(ServerLevel pLevel, EntityMaid maid, long pGameTime) {
        CraftingAndCarryingMemory memory=maid.getBrain().getMemory(MemoryRegistry.CRAFTING_AND_CARRYING_MEMORY.get()).orElse(null);
        assert memory!=null;

        /**
        检查walkTarget是否为容器
         */
        BlockEntity entity=pLevel.getBlockEntity(memory.walkTarget);
        if(entity==null||(!entity.getCapability(ForgeCapabilities.ITEM_HANDLER).isPresent())){
            int index=memory.getChestsFrom().indexOf(memory.walkTarget);
            if(index+1<memory.getChestsFrom().size()){
                memory.walkTarget= memory.getChestsFrom().get(index+1);
            }
            else{
                memory.walkTarget=null;
                maid.getChatBubbleManager().addChatBubble(TextChatBubbleData.type2(lackOfSpace));
            }
            return;
        }


        maid.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(memory.walkTarget,speedModifier,0));
        if(maid.getOnPos().closerThan(memory.walkTarget,2)){
            if(memory.isOpeningChest()){
                RangedWrapper maidBackPack = maid.getAvailableBackpackInv();
                BlockEntity chest=pLevel.getBlockEntity(memory.walkTarget);
                assert chest!=null;
                LazyOptional<IItemHandler> capability=chest.getCapability(ForgeCapabilities.ITEM_HANDLER);
                capability.ifPresent((cap)->{
                    boolean isBackPackEmpty= removeItem(cap,maidBackPack);
                    if(isBackPackEmpty){
                        memory.setIsBackpackFull(false);
                        memory.setIsOpeningChest(false);
                        SimulateTargetInteractHelper helper = new SimulateTargetInteractHelper(maid, memory.walkTarget, null, pLevel);
                        helper.stop();
                        memory.walkTarget =null;
                    }
                    else{
                        memory.setIsOpeningChest(false);
                        int indexOfCurrentChest=memory.getChestsFrom().indexOf(memory.walkTarget);
                        SimulateTargetInteractHelper helper = new SimulateTargetInteractHelper(maid, memory.walkTarget, null, pLevel);
                        helper.stop();
                        if(indexOfCurrentChest+1<memory.getChestsFrom().size()){
                            memory.walkTarget =memory.getChestsFrom().get(indexOfCurrentChest+1);
                        }else{
                            memory.walkTarget =null;
                            maid.getChatBubbleManager().addChatBubble(TextChatBubbleData.type2(lackOfSpace));
                        }
                    }
                });



            }
            else{
                int tickDelay=memory.getOpenChestTickDelay();
                if(tickDelay==0) {
                    SimulateTargetInteractHelper helper = new SimulateTargetInteractHelper(maid, memory.walkTarget, null, pLevel);
                    helper.open();
                }
                else if(tickDelay==CraftingAndCarryingMemory.openChestDelay){
                    memory.setIsOpeningChest(true);
                }

            }

        }

    }

    /**
     * @return 若将女仆身上的物品移动到箱子中后没有剩余的物品，则返回true，否则返回false。
     */
    protected boolean removeItem(IItemHandler chest,RangedWrapper maidBackpack){
        boolean result=true;
        for(int i=0;i<maidBackpack.getSlots();i++){
            ItemStack remainItemStack=maidBackpack.extractItem(i,64,false);

            for(int j=0;j<chest.getSlots()&&!remainItemStack.isEmpty();j++){
                remainItemStack=chest.insertItem(j,remainItemStack,false);
            }
            if(!remainItemStack.isEmpty()){
                result=false;
                maidBackpack.insertItem(i,remainItemStack,false);
            }

        }
        return result;
    }

    @Override
    protected void stop(ServerLevel pLevel, EntityMaid pEntity, long pGameTime) {

    }

    @Override
    protected boolean canStillUse(ServerLevel pLevel, EntityMaid pEntity, long pGameTime) {
        return true;
    }

    @Override
    protected boolean timedOut(long pGameTime) {
        return false;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel pLevel, EntityMaid pOwner) {
        CraftingAndCarryingMemory memory=pOwner.getBrain().getMemory(MemoryRegistry.CRAFTING_AND_CARRYING_MEMORY.get()).orElse(null);
        return memory != null && memory.isInitialized();
    }
}
