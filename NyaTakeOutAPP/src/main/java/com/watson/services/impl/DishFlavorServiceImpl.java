package com.watson.services.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.watson.common.ResultEnum;
import com.watson.entity.DishFlavorEntity;
import com.watson.exception.SqlCustomException;
import com.watson.mapper.DishFlavorMapper;
import com.watson.services.DishFlavorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavorEntity> implements DishFlavorService {

    @Override
    public Long onAdd(DishFlavorEntity entity) {
        Long id = IdUtil.getSnowflakeNextId();
        entity.setId(id);
        boolean res = save(entity);
        if (!res) {
            throw new SqlCustomException(ResultEnum.FAIL, "添加口味信息: " + entity.getValue() + " 时失败");
        }
        return id;
    }

    @Override
    @Transactional
    public void onSaveAll(List<DishFlavorEntity> entities) {
        saveBatch(entities);
//        if (!res) {
//            throw new SqlCustomException(ResultEnum.FAIL, "添加口味时失败");
//        }
    }

    /**
     * 删除某菜品的口味
     *
     * @param dishId   要删除口味的菜品ID
     * @return
     */
    @Override
    public void onDel(Long dishId) {
        LambdaQueryWrapper<DishFlavorEntity> lwq = new LambdaQueryWrapper<>();
        lwq.eq(null != dishId, DishFlavorEntity::getDishId, dishId);
        //感觉这行可有可无, 因为新增菜品时没有sql没有口味数据, 更新菜品时删掉所有口味数据重新插入
        //lwq.and(i-> i.eq(null != flavorId,DishFlavorEntity::getId, flavorId));
//        boolean res = remove(lwq);
//        if(!res) {
//            throw new SqlCustomException(ResultEnum.FAIL, "删除口味时失败");
//        }
        remove(lwq);
    }

    @Override
    public List<DishFlavorEntity> onFetchByDishID(Long dishId) {
        LambdaQueryWrapper<DishFlavorEntity> lwq = new LambdaQueryWrapper<>();
        lwq.eq(null != dishId, DishFlavorEntity::getDishId, dishId);
        return list(lwq);
    }
}
