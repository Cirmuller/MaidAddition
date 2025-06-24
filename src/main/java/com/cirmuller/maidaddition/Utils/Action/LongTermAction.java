package com.cirmuller.maidaddition.Utils.Action;

import com.cirmuller.maidaddition.exception.TimeoutException;
import net.minecraft.world.entity.Entity;

import javax.annotation.OverridingMethodsMustInvokeSuper;

public class LongTermAction<T extends Entity> implements Action<T>{

    protected int time;
    protected int timeOut;
    public LongTermAction(int timeOut){
        this.timeOut=timeOut;
        this.time=0;
    }
    public LongTermAction(){
        this.timeOut=100;
        this.time=0;
    }
    @Override
    public boolean timeOut(T entity) {
        return time>=timeOut;
    }

    @OverridingMethodsMustInvokeSuper
    @Override
    public boolean execute(T entity) {
        time++;
        if(timeOut(entity)){
            throw new TimeoutException("Executing timeout");
        }
        return !timeOut(entity);
    }

    @Override
    public final boolean isShortTerm() {
        return false;
    }

    @Override
    public boolean success(T entity) {
        return false;
    }
}
