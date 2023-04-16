package com.watson.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.watson.dto.SetmealDTO;
import com.watson.entity.SetmealEntity;

import java.util.List;

public interface SetmealMapper extends BaseMapper<SetmealEntity> {
    List<SetmealDTO> getSetmealList(Long storeId, String name, boolean showHide);
}
