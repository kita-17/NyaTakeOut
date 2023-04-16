package com.watson.services;

import com.baomidou.mybatisplus.extension.service.IService;
import com.watson.entity.DishFlavorEntity;
import com.watson.entity.SetmealDishEntity;

import java.util.List;

public interface SetmealDishService extends IService<SetmealDishEntity> {
    Long onAdd(SetmealDishEntity entity);
    void onSaveAll(List<SetmealDishEntity> entities);
    void onDel(Long setmealId);
    List<SetmealDishEntity> onFetchByDishID(Long setmealId);
}
