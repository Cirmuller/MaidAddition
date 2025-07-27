package com.cirmuller.maidaddition.entity.navigation;

import com.cirmuller.maidaddition.MaidAddition;
import com.cirmuller.maidaddition.Utils.Action.ActionQueue;
import com.cirmuller.maidaddition.Utils.Action.DestroyBlockAction;
import com.cirmuller.maidaddition.exception.FindNoPathException;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AStarShortestPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.*;

import java.util.List;
import java.util.function.Predicate;

public class PathFindingNavigation {
    BlockGetter level;
    BlockPos end;
    BlockPos start;
    BoundingBox region;
    Graph<BlockPos, CallbackEdge> graph;
    List<BlockPos> path;
    List<CallbackEdge> edges;
    Thread thread;

    ActionQueue<EntityMaid> actions;
    boolean isTerminated;
    boolean isOutdate;
    public static double highWeight=20;
    public static double lowWeight=1;

    public static Logger logger= LogManager.getLogger(MaidAddition.MODID);
    /**
     * 本类可以根据传入的参数自动生成寻路路径，计算结束后生成的行为动作队列存放于@link actions中
     * @param maid 女仆
     * @param targetBlockPredicate 判断是否为目标方块，女仆需要往该目标方块移动
     * @param buildingBlockPredicate 判断ItemStack是否能够用于给女仆搭方块，定义女仆能用什么方块搭路
     * @param breakableBlockPredicate 判断目标方块是否可以破坏，定义女仆能破坏什么方块
     * @param region 寻路范围
     */
    public PathFindingNavigation(EntityMaid maid,
                                 Predicate<BlockPos> targetBlockPredicate,
                                 Predicate<ItemStack> buildingBlockPredicate,
                                 Predicate<BlockPos> breakableBlockPredicate,
                                 BoundingBox region){
        this.level=maid.level();
        this.start=maid.getOnPos();
        this.region=region;
        this.isOutdate=false;
        this.end=null;
        if(!region.isInside(start)){
            throw new IllegalArgumentException("The startpoint should be inside region");
        }
        graph=new DefaultDirectedWeightedGraph<>(CallbackEdge.class);
        isTerminated=false;
        thread=new Thread() {
            @Override
            public void run(){
                //寻找目标
                for (int i = region.minX(); i <= region.maxX(); i++) {
                    for (int j = region.minY(); j <= region.maxY(); j++) {
                        for (int k = region.minZ(); k <= region.maxZ(); k++) {
                            BlockPos pos=new BlockPos(i,j,k);
                            if(targetBlockPredicate.test(pos)){
                                end=pos;
                                break;
                            }
                        }
                    }
                }
                if(end==null){
                    isOutdate=true;
                    isTerminated=true;
                    return;
                }


                //添加顶点
                for (int i = region.minX(); i <= region.maxX(); i++) {
                    for (int j = region.minY(); j <= region.maxY(); j++) {
                        for (int k = region.minZ(); k <= region.maxZ(); k++) {
                            graph.addVertex(new BlockPos(i, j, k));
                        }
                    }
                }

                //添加边
                for (int i = region.minX()+1; i < region.maxX(); i++) {
                    for (int j = region.minY()+1; j < region.maxY(); j++) {
                        for (int k = region.minZ()+1; k < region.maxZ(); k++) {
                            BlockPos origin = new BlockPos(i, j, k);
                            BlockState state=level.getBlockState(origin);
                            generateUpwardEdge(origin);
                            if(!state.isAir()) {
                                generateDownwardEdge(origin);
                            }
                            generateHorizonEdge(origin,origin.west());
                            generateHorizonEdge(origin,origin.east());
                            generateHorizonEdge(origin,origin.north());
                            generateHorizonEdge(origin,origin.south());
                            generateUpwardHorizonEdge(origin,origin.west().above());
                            generateUpwardHorizonEdge(origin,origin.east().above());
                            generateUpwardHorizonEdge(origin,origin.north().above());
                            generateUpwardHorizonEdge(origin,origin.south().above());
                            generateDownwardDiagonalEdge(origin,origin.west().below());
                            generateDownwardDiagonalEdge(origin,origin.east().below());
                            generateDownwardDiagonalEdge(origin,origin.north().below());
                            generateDownwardDiagonalEdge(origin,origin.south().below());

                        }
                    }
                }
                //GraphPath<BlockPos,CallbackEdge> graphPath=new DijkstraShortestPath(graph).getPath(start,end);
                GraphPath<BlockPos,CallbackEdge> graphPath=new AStarShortestPath<>(graph,new ManhattanAdmissibleHeuristic<>()).getPath(start,end);
                if(graphPath!=null) {
                    path = graphPath.getVertexList();
                    edges = graphPath.getEdgeList();
                    isTerminated = true;
                    actions=ActionQueue.create(PathFindingNavigation.this);
                    logger.debug(graphPath.toString());
                }else{
                    isOutdate=true;
                    isTerminated = true;
                    actions=new ActionQueue<>();
                }
            }

            /**
            ===============================辅助函数分割线========================================================
             */
            public boolean generateUpwardEdge(BlockPos origin){
                BlockPos pos=origin.above(3);
                BlockState state=level.getBlockState(pos);
                CallbackEdge edge=new CallbackEdge();
                if(state.isAir()){
                    edge.offer(CallbackEdge.jump);
                    edge.offer(CallbackEdge.doNothing);
                    edge.offer(CallbackEdge.doNothing);
                    edge.offer(CallbackEdge.putGivenBlockUnderFeet(buildingBlockPredicate));
                    graph.addEdge(origin,origin.above(),edge);
                    graph.setEdgeWeight(edge,highWeight+lowWeight);
                    return true;
                }else if(canSafelyDestroy(pos)){
                    edge.offer(CallbackEdge.destroyTargetBlock(pos));
                    edge.offer(CallbackEdge.jump);
                    edge.offer(CallbackEdge.doNothing);
                    edge.offer(CallbackEdge.doNothing);
                    edge.offer(CallbackEdge.putGivenBlockUnderFeet(buildingBlockPredicate));
                    graph.setEdgeWeight(edge,2*highWeight+lowWeight);
                    return true;
                }
                return false;
            }
            public boolean generateHorizonEdge(BlockPos origin,BlockPos target){
                BlockState state=level.getBlockState(target);
                CallbackEdge edge=new CallbackEdge();
                double times=0.0;


                BlockPos pos1=target.above();
                BlockPos pos2=target.above().above();
                BlockState state1=level.getBlockState(pos1);
                BlockState state2=level.getBlockState(pos2);
                if(state1.isAir()&&state2.isAir()){
                    if(state.canBeReplaced()){
                        if(canBeSafelyPut(target)){
                            edge.offer(CallbackEdge.putBlockOn(target,buildingBlockPredicate));
                            times+=1;
                        }else{
                            return false;
                        }
                    }
                    graph.addEdge(origin,target,edge);
                    graph.setEdgeWeight(edge,times*highWeight+lowWeight);
                    return true;
                }
                else if (!state1.isAir()&&!state2.isAir()){
                    if(canSafelyDestroy(pos1)&&canSafelyDestroy(pos2)) {
                        if(state.canBeReplaced()){
                            if(canBeSafelyPut(target)){
                                edge.offer(CallbackEdge.putBlockOn(target,buildingBlockPredicate));
                                times+=1;
                            }else{
                                return false;
                            }
                        }
                        edge.offer(CallbackEdge.destroyTargetBlock(pos1));
                        edge.offer(CallbackEdge.destroyTargetBlock(pos2));
                        graph.addEdge(origin, target, edge);
                        graph.setEdgeWeight(edge, (times+2) * highWeight+lowWeight);
                        return true;
                    }
                    else{
                        return false;
                    }
                }else{
                    BlockState airState=state1.isAir()?state1:state2;
                    BlockPos airPos=state1.isAir()?pos1:pos2;
                    BlockState notAirState=(!state1.isAir())?state1:state2;
                    BlockPos notAirPos=(!state1.isAir())?pos1:pos2;
                    if(canSafelyDestroy(notAirPos)){
                        if(state.canBeReplaced()){
                            if(canBeSafelyPut(target)){
                                edge.offer(CallbackEdge.putBlockOn(target,buildingBlockPredicate));
                                times+=1;
                            }else{
                                return false;
                            }
                        }
                        edge.offer(CallbackEdge.destroyTargetBlock(notAirPos));
                        graph.addEdge(origin, target, edge);
                        graph.setEdgeWeight(edge, (times+1) *highWeight+lowWeight);
                        return true;
                    }else{
                        return false;
                    }
                }




            }
            public boolean generateUpwardHorizonEdge(BlockPos origin,BlockPos target){
                BlockState state=level.getBlockState(target);
                CallbackEdge edge=new CallbackEdge();
                double times=0.0;


                BlockPos pos1=target.above();
                BlockPos pos2=target.above().above();
                BlockPos onHeadPos=origin.above(3);
                BlockState state1=level.getBlockState(pos1);
                BlockState state2=level.getBlockState(pos2);
                BlockState onHeadBlock=level.getBlockState(onHeadPos);
                if(state1.isAir()&&state2.isAir()){
                    if(state.canBeReplaced()){
                        if(canBeSafelyPut(target)){
                            edge.offer(CallbackEdge.putBlockOn(target,buildingBlockPredicate));
                            times+=1;
                        }else{
                            return false;
                        }
                    }
                    if(onHeadBlock.isAir()){

                    }
                    else if(canSafelyDestroy(onHeadPos)){
                        edge.offer(CallbackEdge.destroyTargetBlock(onHeadPos));
                        times+=1;

                    }else{
                        return false;
                    }
                    graph.addEdge(origin,target,edge);
                    graph.setEdgeWeight(edge,times*highWeight+lowWeight);
                    return true;
                }
                else if (!state1.isAir()&&!state2.isAir()){
                    if(canSafelyDestroy(pos1)&&canSafelyDestroy(pos2)) {
                        if(state.canBeReplaced()){
                            if(canBeSafelyPut(target)){
                                edge.offer(CallbackEdge.putBlockOn(target,buildingBlockPredicate));
                                times+=1;
                            }else{
                                return false;
                            }
                        }
                        if(onHeadBlock.isAir()){

                        }
                        else if(canSafelyDestroy(onHeadPos)){
                            edge.offer(CallbackEdge.destroyTargetBlock(onHeadPos));
                            times+=1;
                        }else{
                            return false;
                        }
                        edge.offer(CallbackEdge.destroyTargetBlock(pos1));
                        edge.offer(CallbackEdge.destroyTargetBlock(pos2));
                        graph.addEdge(origin, target, edge);
                        graph.setEdgeWeight(edge, (times+2) * highWeight+lowWeight);
                        return true;
                    }
                    else{
                        return false;
                    }
                }else{
                    BlockState airState=state1.isAir()?state1:state2;
                    BlockPos airPos=state1.isAir()?pos1:pos2;
                    BlockState notAirState=(!state1.isAir())?state1:state2;
                    BlockPos notAirPos=(!state1.isAir())?pos1:pos2;
                    if(canSafelyDestroy(notAirPos)){
                        if(state.canBeReplaced()){
                            if(canBeSafelyPut(target)){
                                edge.offer(CallbackEdge.putBlockOn(target,buildingBlockPredicate));
                                times+=1;
                            }else{
                                return false;
                            }
                        }
                        if(onHeadBlock.isAir()){

                        }else if(canSafelyDestroy(onHeadPos)){
                            edge.offer(CallbackEdge.destroyTargetBlock(onHeadPos));
                            times+=1;
                        }
                        else{
                            return false;
                        }
                        edge.offer(CallbackEdge.destroyTargetBlock(notAirPos));
                        graph.addEdge(origin, target, edge);
                        graph.setEdgeWeight(edge, (times+1) *highWeight+lowWeight);
                        return true;
                    }else{
                        return false;
                    }
                }


            }

            public boolean generateDownwardEdge(BlockPos origin){
                if(!level.getBlockState(origin.below()).canBeReplaced()){
                    CallbackEdge edge=new CallbackEdge();
                    edge.offer(CallbackEdge.destroyTargetBlock(origin));
                    graph.addEdge(origin,origin.below(),edge);
                    graph.setEdgeWeight(edge,highWeight);
                    return true;
                }else{
                    return false;
                }
            }

            public boolean generateDownwardDiagonalEdge(BlockPos origin,BlockPos target){
                BlockState state=level.getBlockState(target);
                CallbackEdge edge=new CallbackEdge();
                double times=0.0;
                boolean shouldPlaceBlock;
                if(state.canBeReplaced()){
                    if(canBeSafelyPut(target)){
                        shouldPlaceBlock=true;
                        times+=1;
                    }else{
                        return false;
                    }
                }else{
                    shouldPlaceBlock=false;
                }

                BlockPos[] poses=new BlockPos[3];
                boolean canPass=true;
                for(int i=0;i<3;i++){
                    poses[i]=target.above(i+1);
                    if(!level.getBlockState(poses[i]).isAir()&&!canSafelyDestroy(poses[i])){
                        canPass=false;
                    }
                }

                if(!canPass){
                    return false;
                }

                if(shouldPlaceBlock){
                    edge.offer(CallbackEdge.putBlockOn(target,buildingBlockPredicate));
                }
                for(int i=0;i<3;i++){
                    if(!level.getBlockState(poses[i]).isAir()){
                        edge.offer(CallbackEdge.destroyTargetBlock(poses[i]));
                        times+=1;
                    }
                }
                graph.addEdge(origin,target,edge);
                graph.setEdgeWeight(edge,times*highWeight+lowWeight);
                return true;


            }

            public boolean hasFluidNearby(BlockPos pos){
                return isFluidAtPosition(pos)||
                        isFluidAtPosition(pos.above())||
                        isFluidAtPosition(pos.east())||
                        isFluidAtPosition(pos.west())||
                        isFluidAtPosition(pos.north())||
                        isFluidAtPosition(pos.south());
            }
            public boolean isFluidAtPosition(BlockPos pos){
                return !level.getFluidState(pos).isEmpty();
            }
            public boolean canSafelyDestroy(BlockPos pos){
                return !hasFluidNearby(pos)&&!(level.getBlockState(pos.above()).getBlock() instanceof FallingBlock)&&breakableBlockPredicate.test(pos);
            }

            public boolean canBeSafelyPut(BlockPos pos){
                return !level.getBlockState(pos.below()).canBeReplaced();
            }

        };
        thread.setDaemon(true);
        thread.setName("MaidAddition navigation calculating thread-"+thread.getId());
        if(maid.getNavigation()instanceof GroundPathNavigation){
            thread.start();
        }else{
            isTerminated=true;
            isOutdate=true;
        }


    }
    public boolean isTerminated(){
        return isTerminated;
    }
    public List<BlockPos> getPath(){
        return path;
    }
    public List<CallbackEdge>getEdges(){
        return edges;
    }
    public void setOutdate(){
        this.isOutdate=true;
    }
    public boolean isOutdate(){
        return isOutdate;
    }
    public ActionQueue<EntityMaid> getActions(){
        return actions;
    }
    public BlockPos getTarget(){
        return this.end;
    }
    public BlockPos getStart(){
        return this.start;
    }

}
