package com.watson.services;

import com.baomidou.mybatisplus.extension.service.IService;
import com.watson.entity.DishFlavorEntity;

import java.util.List;

public interface DishFlavorService extends IService<DishFlavorEntity> {
    Long onAdd(DishFlavorEntity entity);
    void onSaveAll(List<DishFlavorEntity> entities);
    void onDel(Long dishId);
    List<DishFlavorEntity> onFetchByDishID(Long dishID);
}
