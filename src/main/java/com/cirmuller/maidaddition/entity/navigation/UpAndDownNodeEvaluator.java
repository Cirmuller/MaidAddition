package com.cirmuller.maidaddition.entity.navigation;

import com.cirmuller.maidaddition.Utils.Action.Action;
import com.cirmuller.maidaddition.Utils.Action.ActionQueue;
import com.github.tartaricacid.touhoulittlemaid.entity.ai.navigation.MaidNodeEvaluator;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

public class UpAndDownNodeEvaluator extends MaidNodeEvaluator {
    protected Int2ObjectMap<ActionQueue> actions=new Int2ObjectArrayMap<>();

    @Override
    protected Node getNode(int pX, int pY, int pZ) {
        return (Node)this.nodes.computeIfAbsent(Node.createHash(pX, pY, pZ), (p_77332_) -> {
            actions.computeIfAbsent(p_77332_,(key)->{
                return new ActionQueue();
            });
            return new Node(pX, pY, pZ);
        });
    }

    @Override
    public void prepare(PathNavigationRegion pLevel, Mob pMob) {
        super.prepare(pLevel, pMob);
        actions.clear();
    }

    @Override
    public int getNeighbors(Node[] nodes, Node origin) {
        int nodeId = super.getNeighbors(nodes, origin);
        int y= origin.y+1;
        if(y<=this.mob.level().getMaxBuildHeight()){
            Node node=this.getNode(new BlockPos.MutableBlockPos(origin.x,origin.y+1,origin.z));
            if (!node.closed) {
                node.costMalus = 16;
                node.type = BlockPathTypes.WALKABLE;
                if (nodeId + 1 < nodes.length) {
                    nodes[nodeId++] = node;
                }
            }
        }

        y=origin.y-1;
        if(y>=this.mob.level().getMinBuildHeight()){
            Node node=this.getNode(new BlockPos.MutableBlockPos(origin.x,origin.y+1,origin.z));
            if (!node.closed) {
                node.costMalus = 16;
                node.type = BlockPathTypes.WALKABLE;
                if (nodeId + 1 < nodes.length) {
                    nodes[nodeId++] = node;
                }
            }
        }
        return nodeId;
    }
}
