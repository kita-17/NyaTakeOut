package com.watson.services;

import com.baomidou.mybatisplus.extension.service.IService;
import com.watson.common.Result;
import com.watson.dto.DishDTO;
import com.watson.dto.SetmealDTO;
import com.watson.entity.SetmealEntity;

import java.util.List;

public interface SetmealService extends IService<SetmealEntity> {
    Result onFetchData(Long storeId, String name,boolean showHide);

    SetmealDTO addSetmeal(SetmealDTO entity);

    void removeSetmealById(Long id);

    void updateSetmeal(SetmealDTO entity);

    List<DishDTO> onListSetmeal();
}
