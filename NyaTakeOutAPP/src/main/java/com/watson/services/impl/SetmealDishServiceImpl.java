package com.watson.services.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.watson.common.ResultEnum;
import com.watson.entity.DishFlavorEntity;
import com.watson.entity.SetmealDishEntity;
import com.watson.exception.SqlCustomException;
import com.watson.mapper.SetmealDishMapper;
import com.watson.services.SetmealDishService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDishEntity> implements SetmealDishService {
    @Override
    public Long onAdd(SetmealDishEntity entity) {
        Long id = IdUtil.getSnowflakeNextId();
        entity.setId(id);
        boolean res = save(entity);
        if (!res) {
            throw new SqlCustomException(ResultEnum.FAIL, "添加套餐的菜品信息: " + entity.getValue() + " 时失败");
        }
        return id;
    }

    @Override
    public void onSaveAll(List<SetmealDishEntity> entities) {
        boolean res = saveBatch(entities);
        if (!res) {
            throw new SqlCustomException(ResultEnum.FAIL, "添加菜品信息时失败");
        }
    }

    @Override
    public void onDel(Long setmealId) {
        LambdaQueryWrapper<SetmealDishEntity> lwq = new LambdaQueryWrapper<>();
        lwq.eq(null != setmealId, SetmealDishEntity::getSetmealId, setmealId);
        remove(lwq);
    }

    @Override
    public List<SetmealDishEntity> onFetchByDishID(Long setmealId) {
        LambdaQueryWrapper<SetmealDishEntity> lwq = new LambdaQueryWrapper<>();
        lwq.eq(null != setmealId, SetmealDishEntity::getSetmealId, setmealId);
        return list(lwq);
    }
}
