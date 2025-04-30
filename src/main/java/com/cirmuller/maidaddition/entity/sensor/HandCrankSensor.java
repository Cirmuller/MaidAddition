package com.cirmuller.maidaddition.entity.sensor;

import com.cirmuller.maidaddition.configs.Config;
import com.cirmuller.maidaddition.entity.memory.MemoryRegistry;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.simibubi.create.content.kinetics.crank.HandCrankBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class HandCrankSensor extends Sensor<EntityMaid> {
    private static final int scanRate= Config.UPDATE_RATE.get();
    public HandCrankSensor(){
        super(scanRate);
    }
    @Override
    protected void doTick(ServerLevel serverLevel, EntityMaid entityMaid) {

        HandCrankBlockEntity handCrankBlockEntity=null;
        BlockPos pos=entityMaid.getOnPos();
        for(int i=0;i<=entityMaid.getTask().searchRadius(entityMaid);i++){
            HandCrankBlockEntity searchResult=searchHandCrankBlockEntity(serverLevel,pos,i);
            if(searchResult!=null){
                handCrankBlockEntity=searchResult;
                break;
            }
        }
        entityMaid.getBrain().setMemory(MemoryRegistry.HAND_CRANK_TARGET.get(),handCrankBlockEntity);
    }

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return Set.of(MemoryRegistry.HAND_CRANK_TARGET.get());
    }


    /*
    扫描以center为中心，以radius为半径（Minkovski距离或$L^\infty$距离）的球面上的手摇曲柄
    scan the hand crank on the sphere centering at $center$ with Minkovski(or $L^\infty$) radius $radius$
     */
    @Nullable
    private HandCrankBlockEntity searchHandCrankBlockEntity(ServerLevel serverLevel,Vec3i center, int radius){
        int xMin,yMin,zMin,xMax,yMax,zMax,x,y,z;
        x=center.getX();
        y=center.getY();
        z= center.getZ();
        xMin=x-radius;
        xMax=x+radius;
        yMin= y-radius;
        yMax= y+radius;
        zMin=z-radius;
        zMax=z+radius;

        for(int i=xMin;i<xMax;i++){
            for(int j=yMin;j<yMax;j++){
                BlockPos pos=new BlockPos(i,j,zMin);
                BlockState blockState=serverLevel.getBlockState(pos);
                if(blockState.hasBlockEntity()
                        &&(serverLevel.getBlockEntity(pos)instanceof HandCrankBlockEntity handCrankBlockEntity)
                        &&handCrankBlockEntity.inUse==0){
                    return handCrankBlockEntity;
                }
            }
        }
        for(int i=zMin;i<zMax;i++){
            for(int j=yMin;j<yMax;j++){
                BlockPos pos=new BlockPos(xMax,j,i);
                BlockState blockState=serverLevel.getBlockState(pos);
                if(blockState.hasBlockEntity()
                        &&(serverLevel.getBlockEntity(pos)instanceof HandCrankBlockEntity handCrankBlockEntity)
                        &&handCrankBlockEntity.inUse==0){
                    return handCrankBlockEntity;
                }
            }
        }
        for(int i=xMax;i>xMin;i--){
            for(int j=yMin;j<yMax;j++){
                BlockPos pos=new BlockPos(i,j,zMax);
                BlockState blockState=serverLevel.getBlockState(pos);
                if(blockState.hasBlockEntity()
                        &&(serverLevel.getBlockEntity(pos)instanceof HandCrankBlockEntity handCrankBlockEntity)
                        &&handCrankBlockEntity.inUse==0){
                    return handCrankBlockEntity;
                }
            }
        }
        for(int i=zMax;i>zMin;i--){
            for(int j=yMin;j<yMax;j++){
                BlockPos pos=new BlockPos(xMin,j,i);
                BlockState blockState=serverLevel.getBlockState(pos);
                if(blockState.hasBlockEntity()
                        &&(serverLevel.getBlockEntity(pos)instanceof HandCrankBlockEntity handCrankBlockEntity)
                        &&handCrankBlockEntity.inUse==0){
                    return handCrankBlockEntity;
                }
            }
        }
        for(int i=xMin;i<=xMax;i++){
            for(int j=zMin;j<=zMax;j++){
                BlockPos pos=new BlockPos(i,yMax,j);
                BlockState blockState=serverLevel.getBlockState(pos);
                if(blockState.hasBlockEntity()
                        &&(serverLevel.getBlockEntity(pos)instanceof HandCrankBlockEntity handCrankBlockEntity)
                        &&handCrankBlockEntity.inUse==0){
                    return handCrankBlockEntity;
                }
            }
        }
        for(int i=xMin+1;i<xMax;i++){
            for(int j=zMin+1;j<zMax;j++){
                BlockPos pos=new BlockPos(i,yMin,j);
                BlockState blockState=serverLevel.getBlockState(pos);
                if(blockState.hasBlockEntity()
                        &&(serverLevel.getBlockEntity(pos)instanceof HandCrankBlockEntity handCrankBlockEntity)
                        &&handCrankBlockEntity.inUse==0){
                    return handCrankBlockEntity;
                }
            }
        }
        return null;
    }
}
