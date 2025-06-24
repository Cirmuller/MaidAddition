package com.cirmuller.maidaddition.Utils.Action;

import com.cirmuller.maidaddition.entity.navigation.CallbackEdge;
import com.cirmuller.maidaddition.entity.navigation.PathFindingNavigation;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Consumer;

public class ActionQueue<T extends Entity> extends LinkedList<Action<T>> implements Queue<Action<T>> {

    private static int interval=6;
    public boolean execute(T entity){
        if(this.isEmpty()){
            return true;
        }
        Action<T> action=this.getFirst();
        if(action.isShortTerm()){
            boolean flag= action.execute(entity);
            this.poll();
            return flag;
        }
        else{
            boolean flag= action.execute(entity);
            if(action.success(entity)){
                this.poll();
            }
            return flag;
        }
    }

    public static ActionQueue<EntityMaid> create(PathFindingNavigation navigation){
        ActionQueue<EntityMaid> result=new ActionQueue<>();
        if(navigation==null||navigation.isOutdate()){
            return null;
        }
        List<BlockPos> path=navigation.getPath();
        List<CallbackEdge> edges=navigation.getEdges();
        int i;
        int count=0;
        for(i=0;i<edges.size();i++){
            CallbackEdge edge=edges.get(i);
            List<Action<EntityMaid>> actions=edge.getCallbacks().stream().toList();
            if(actions.isEmpty()){
                count++;
                if(count==interval){
                    count=0;
                    result.offer(new WalkToAction<>(path.get(i)));
                }
                continue;
            }
            count=0;
            result.offer(new WalkToAction<>(path.get(i)));
            actions.forEach(result::offer);
        }
        result.offer(new WalkToAction<>(path.get(i)));
        return result;
    }
}
