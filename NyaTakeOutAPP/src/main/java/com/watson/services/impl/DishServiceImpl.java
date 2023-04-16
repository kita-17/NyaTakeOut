package com.watson.services.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.watson.common.CacheConstant;
import com.watson.common.Result;
import com.watson.common.ResultEnum;
import com.watson.common.UserLocal;
import com.watson.dto.DishDTO;
import com.watson.entity.DishEntity;
import com.watson.entity.DishFlavorEntity;
import com.watson.entity.SetmealDishEntity;
import com.watson.exception.SqlCustomException;
import com.watson.mapper.DishMapper;
import com.watson.services.DishService;
import com.watson.utils.RedisUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, DishEntity> implements DishService {

    @Resource
    private SetmealDishServiceImpl setmealDishService;

    @Resource
    private DishFlavorServiceImpl flavorService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private DishMapper dishMapper;

    @Resource
    private RedisUtil redisUtil;

    @Override
    public Result onFetchData(Long storeId, String name, boolean showHide) {
        String key = CacheConstant.STORE_DISH + storeId;
        //List<DishDTO> dishDTOS = redisUtil.getList(key, DishDTO.class, 30L, TimeUnit.MINUTES, db -> {
        //    LambdaQueryWrapper<DishEntity> lwq = new LambdaQueryWrapper<>();
        //    lwq.eq(null != showHide && showHide.equals("false"), DishEntity::isStatus, true);
        //    lwq.eq(DishEntity::getStoreId, storeId);
        //    lwq.like(null != name, DishEntity::getName, name);
        //    List<DishEntity> temp = list(lwq);
        //    return temp.stream().map(i -> {
        //        DishDTO dto = new DishDTO();
        //        BeanUtils.copyProperties(i, dto);
        //        dto.setKey(dto.getId());
        //        //分配分类名
        //        String categoryName = cs.getById(dto.getCategory()).getName();
        //        dto.setCategoryName(categoryName);
        //        //分配口味信息
        //        LambdaQueryWrapper<DishFlavorEntity> lw = new LambdaQueryWrapper<>();
        //        lw.eq(DishFlavorEntity::getDishId, dto.getId());
        //        List<DishFlavorEntity> list = flavorService.list(lw);
        //        dto.setFlavor(list);
        //        return dto;
        //    }).toList();
        //});
        List<DishDTO> list = redisUtil.getList(key, DishDTO.class, 30L, TimeUnit.MINUTES,
                db -> dishMapper.getDishByStoreId(storeId, name, showHide));
        return Result.success(list);
    }

    @Override
    @Transactional
    public Result addDish(DishDTO dishEntity) {
        /*
         * 员工权限鉴定，暂时先这样吧，需要想个更优雅的方法替换掉
         */
        if (!UserLocal.getUser().getStoreId().equals(dishEntity.getStoreId())) {
            return Result.error("权限不足");
        }
        Long id = IdUtil.getSnowflakeNextId();
        dishEntity.setId(id);
        dishEntity.setType(1);
        boolean res = save(dishEntity);
        if (!res) {
            throw new SqlCustomException(ResultEnum.FAIL, "数据已存在");
        }
        //口味数据,绑定与入库
        List<DishFlavorEntity> flavorEntities = dishEntity.getFlavor();
        for (DishFlavorEntity flavor : flavorEntities) {
            flavor.setId(IdUtil.getSnowflakeNextId());
            flavor.setDishId(id);
        }
        flavorService.onSaveAll(flavorEntities);
        String key = CacheConstant.STORE_DISH + dishEntity.getStoreId();
        stringRedisTemplate.delete(key);
        return Result.success(dishEntity);
    }

    /**
     * TODO: 或许应该在返回信息里加入 该菜品被收入的套餐名
     */
    @Override
    @Transactional
    public Result removeDishById(Long[] id) {
        final List<Long> ids = Arrays.asList(id);
        LambdaQueryWrapper<SetmealDishEntity> setmealDishLwq = new LambdaQueryWrapper<>();
        setmealDishLwq.in(SetmealDishEntity::getDishId, ids);
        List<SetmealDishEntity> list = setmealDishService.list(setmealDishLwq);
        if (!list.isEmpty()) {
            return Result.error(ResultEnum.FAIL, "有菜品被套餐收录, 禁止删除");
        }
        //菜品没有被套餐收录
        List<DishEntity> dishEntities = listByIds(ids);
        Long storeId = 0L;
        for (DishEntity dish : dishEntities) {
            if (dish.isStatus()) {
                return Result.error(ResultEnum.FAIL, "禁止删除正在售卖的菜品");
            }
            storeId = dish.getStoreId();
        }
        if (!UserLocal.getUser().getStoreId().equals(storeId)) {
            return Result.error("权限不足");
        }
        LambdaQueryWrapper<DishFlavorEntity> flavorQuery = new LambdaQueryWrapper<>();
        flavorQuery.in(DishFlavorEntity::getDishId, ids);
        if (!removeByIds(ids) && !flavorService.remove(flavorQuery)) {
            return Result.error(ResultEnum.FAIL, "删除失败");
        }
        String key = CacheConstant.STORE_DISH + storeId;
        stringRedisTemplate.delete(key);
        return Result.success("");
    }

    @Override
    @Transactional
    public boolean updateDish(DishDTO entity) {
        if (!UserLocal.getUser().getStoreId().equals(entity.getStoreId())) {
            throw new RuntimeException("权限不足");
        }
        //删除口味数据
        flavorService.onDel(entity.getId());
        //重新插入口味数据
        List<DishFlavorEntity> flavors = entity.getFlavor();
        for (DishFlavorEntity e : flavors) {
            e.setId(IdUtil.getSnowflakeNextId());
            e.setDishId(entity.getId());
        }
        flavorService.onSaveAll(flavors);
        //更新菜品数据
        if (!updateById(entity)) {
            return false;
        }
        stringRedisTemplate.delete(CacheConstant.STORE_DISH + entity.getStoreId());
        return true;
    }

    @Override
    public List<DishDTO> onListDish() {
        return null;
    }
}
