package com.cirmuller.maidaddition.Utils.Action;


import com.cirmuller.maidaddition.exception.ExecuteActionException;
import net.minecraft.world.entity.Entity;

public interface Action<T extends Entity> {
    boolean timeOut(T entity);

    /**
     *
     * @param entity
     * @return 发生异常时返回false,否则返回true
     */
    boolean execute(T entity);
    boolean isShortTerm();
    boolean success(T entity);


}
