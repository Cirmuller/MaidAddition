package com.cirmuller.maidaddition.entity.navigation;

import com.cirmuller.maidaddition.Utils.Action.*;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.Tags;
import org.jgrapht.graph.DefaultWeightedEdge;
import static com.cirmuller.maidaddition.Utils.Action.MaidAction.*;
import java.util.EmptyStackException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Predicate;


public class CallbackEdge extends DefaultWeightedEdge {
    ActionQueue<EntityMaid> callbacks;
    public static Consumer<EntityMaid> jump=(maid)->{
        maid.getJumpControl().jump();
    };
    public static Consumer<EntityMaid> putGivenBlockUnderFeet(Predicate<ItemStack> predicate){
        return (maid)-> {
            putBlockUnderFeet.execute(maid,predicate);
        };
    }
    public static Action<EntityMaid> destroyTargetBlock(BlockPos pos){
        return new DestroyBlockAction(pos);
    }
    public static Consumer<EntityMaid> doNothing=(maid)->{

    };

    public static Consumer<EntityMaid> putBlockOn(BlockPos pos,Predicate<ItemStack> predicate){
        return (maid)->{
            putBlockOn.execute(maid,pos,predicate);
        };
    }
    public CallbackEdge(){
        callbacks=new ActionQueue<>();
    }

    public CallbackEdge offer(Consumer<EntityMaid> callback){
        callbacks.offer(new ShortTermAction<EntityMaid>() {
            @Override
            public boolean execute(EntityMaid entity) {
                callback.accept(entity);
                return true;
            }
        });
        return this;
    }

    public CallbackEdge offer(Action<EntityMaid> action){
        callbacks.offer(action);
        return this;
    }


    public boolean complete(){
        return callbacks.isEmpty();
    }
    public ActionQueue<EntityMaid> getCallbacks(){
        return callbacks;
    }


}
