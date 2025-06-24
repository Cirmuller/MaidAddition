package com.cirmuller.maidaddition.Utils.Action;

import net.minecraft.world.entity.Entity;

public abstract class ShortTermAction<T extends Entity> implements Action<T>{


    @Override
    public boolean timeOut(T entity) {
        return false;
    }

    @Override
    public abstract boolean execute(T entity);

    @Override
    public final boolean isShortTerm() {
        return true;
    }

    @Override
    public boolean success(T entity) {
        return true;
    }
}
