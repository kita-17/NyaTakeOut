package com.watson.services;

import com.baomidou.mybatisplus.extension.service.IService;
import com.watson.common.Result;
import com.watson.dto.DishDTO;
import com.watson.entity.DishEntity;

import java.util.List;

public interface DishService extends IService<DishEntity> {
    Result onFetchData(Long storeId, String name, boolean showHide);

    Result addDish(DishDTO dishEntity);

    Result removeDishById(Long[] id);

    boolean updateDish(DishDTO entity);

    List<DishDTO> onListDish();
}
