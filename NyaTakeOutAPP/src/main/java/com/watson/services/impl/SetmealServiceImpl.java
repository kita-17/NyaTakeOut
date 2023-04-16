package com.watson.services.impl;


import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.watson.common.CacheConstant;
import com.watson.common.Result;
import com.watson.common.ResultEnum;
import com.watson.common.UserLocal;
import com.watson.dto.DishDTO;
import com.watson.dto.SetmealDTO;
import com.watson.entity.DishEntity;
import com.watson.entity.SetmealDishEntity;
import com.watson.entity.SetmealEntity;
import com.watson.exception.SetmealException;
import com.watson.exception.SqlCustomException;
import com.watson.mapper.SetmealMapper;
import com.watson.services.SetmealService;
import com.watson.utils.RedisUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

//TODO 把所有返回ID的方法改成返回实体
@Slf4j
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, SetmealEntity> implements SetmealService {

    @Resource
    private SetmealDishServiceImpl setmealDishService;

    @Resource
    private SetmealMapper setmealMapper;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    /**
     * TODO 改成 sql
     *
     * @param storeId
     * @param name
     *
     * @return
     */
    @Override
    public Result onFetchData(Long storeId, String name, boolean showHide) {
        //LambdaQueryWrapper<SetmealEntity> lwq = new LambdaQueryWrapper<>();
        //lwq.eq(null != showHide && showHide.equals("false"), SetmealEntity::isStatus, true);
        //lwq.eq(SetmealEntity::getStoreId, storeId);
        //lwq.like(null != name, SetmealEntity::getName, name);
        ////获取套餐基本信息
        //List<SetmealEntity> temp = list(lwq);
        ////装配菜品信息, 分类信息
        //List<SetmealDTO> dtoList = temp.stream().map(i -> {
        //    log.info("{}", i);
        //    SetmealDTO dto = new SetmealDTO();
        //    BeanUtils.copyProperties(i, dto);
        //    dto.setKey(i.getId());
        //    dto.setCategoryName(cs.getById(dto.getCategory()).getName());
        //    //获取菜品信息 -> SetmealDishEntity 只有id没有详细信息
        //    LambdaQueryWrapper<SetmealDishEntity> setmeal_dish_lqw = new LambdaQueryWrapper<>();
        //    setmeal_dish_lqw.eq(SetmealDishEntity::getSetmealId, i.getId());
        //    List<SetmealDishEntity> setmealDishEntities = setmealDishService.list(setmeal_dish_lqw);
        //    /*
        //     * 获取菜品详细信息
        //     * 需要注意的是 这里只获取了菜品名字, 因为前端那边我只打算显示名字
        //     * 如果要显示菜品图片介绍等, 请修改SetmealDishEntity
        //     * 当然能, 如果只是为了名字，其实可以一开始就把菜品名一起插进SetmealDishEntity里
        //     */
        //    for (SetmealDishEntity setmealDish : setmealDishEntities) {
        //        DishEntity dish = dishService.getById(setmealDish.getDishId());
        //        if (dish != null) {
        //            setmealDish.setLabel(dish.getName());
        //            setmealDish.setValue(dish.getId());
        //        }
        //    }
        //    dto.setFlavor(setmealDishEntities);
        //    return dto;
        //}).toList();
        List<SetmealDTO> dtoList = redisUtil.getList(CacheConstant.STORE_SETMEAL + storeId, SetmealDTO.class, 30L, TimeUnit.MINUTES,
                db -> setmealMapper.getSetmealList(storeId, name, showHide));
        return Result.success(dtoList);
    }

    @Override
    @Transactional
    public SetmealDTO addSetmeal(SetmealDTO entity) {
        if (!UserLocal.getUser().getStoreId().equals(entity.getStoreId())) {
            throw new RuntimeException("权限不足");
        }
        Long id = IdUtil.getSnowflakeNextId();
        entity.setId(id);
        entity.setType(2);
        boolean res = save(entity);
        if (!res) {
            throw new SqlCustomException(ResultEnum.FAIL, "数据已存在");
        }
        if (!entity.getFlavor().isEmpty()) {
            List<SetmealDishEntity> dishList = entity.getFlavor();
            for (SetmealDishEntity dish : dishList) {
                dish.setSetmealId(id);
            }
            setmealDishService.onSaveAll(dishList);
        }
        stringRedisTemplate.delete(CacheConstant.STORE_SETMEAL + entity.getStoreId());
        return entity;
    }

    /**
     * TODO 禁止删除在售套餐, 套餐删除后对应套餐菜品信息删除
     *
     * @return
     */
    @Override
    @Transactional
    public void removeSetmealById(Long id) {
        SetmealEntity entity = getById(id);
        if (!UserLocal.getUser().getStoreId().equals(entity.getStoreId())) {
            throw new RuntimeException("权限不足");
        }
        if (entity.isStatus()) {
            throw new SetmealException("禁止删除正在售卖的套餐");
        }
        boolean res = removeById(id);
        if (!res) {
            throw new SqlCustomException(ResultEnum.FAIL, "套餐删除失败");
        }
        setmealDishService.onDel(id);
        stringRedisTemplate.delete(CacheConstant.STORE_SETMEAL + entity.getStoreId());
    }

    @Override
    @Transactional
    public void updateSetmeal(SetmealDTO entity) {
        if (!UserLocal.getUser().getStoreId().equals(entity.getStoreId())) {
            throw new RuntimeException("权限不足");
        }
        setmealDishService.onDel(entity.getId());
        List<SetmealDishEntity> dishEntities = entity.getFlavor();
        for (SetmealDishEntity e : dishEntities) {
            e.setId(IdUtil.getSnowflakeNextId());
            e.setSetmealId(entity.getId());
        }
        setmealDishService.saveBatch(dishEntities);
        updateById(entity);
        stringRedisTemplate.delete(CacheConstant.STORE_SETMEAL + entity.getStoreId());
    }

    @Override
    public List<DishDTO> onListSetmeal() {
        return null;
    }
}
