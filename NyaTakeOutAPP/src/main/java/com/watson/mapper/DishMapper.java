package com.watson.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.watson.dto.DishDTO;
import com.watson.entity.DishEntity;

import java.util.List;

public interface DishMapper extends BaseMapper<DishEntity> {
    int addDish(DishEntity dishEntity);
    int deleteDishById(Long id);
    int updateDish(DishEntity dishEntity);
    List<DishDTO> getDishByStoreId(Long storeId, String name, boolean showHide);
}
