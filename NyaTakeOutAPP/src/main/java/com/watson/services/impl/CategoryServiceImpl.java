package com.watson.services.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.watson.common.*;
import com.watson.entity.CategoryEntity;
import com.watson.exception.SqlCustomException;
import com.watson.mapper.CategoryMapper;
import com.watson.services.CategoryService;
import com.watson.utils.RedisUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static cn.hutool.core.lang.Console.log;

@Slf4j
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, CategoryEntity> implements CategoryService {

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public Long addCategory(CategoryEntity category) {
        if (!UserLocal.getUser().getStoreId().equals(category.getStoreId())) {
            throw new SqlCustomException(ResultEnum.CATEGORY_ADD_FAIL, "分类添加失败");
        }
        Long id = IdUtil.getSnowflakeNextId();
        category.setId(id);
        if (!save(category)) {
            throw new SqlCustomException(ResultEnum.CATEGORY_ADD_FAIL, "分类添加失败");
        }
        stringRedisTemplate.delete(CacheConstant.STORE_CATEGORY + category.getStoreId());
        return id;
    }

    public Result getCategory(Long storeId, String name) {
        String key = CacheConstant.STORE_CATEGORY + storeId;
        List<CategoryEntity> categoryEntities = redisUtil.getList(key, CategoryEntity.class, 30L, TimeUnit.MINUTES, db -> {
            LambdaQueryWrapper<CategoryEntity> lwq = new LambdaQueryWrapper<>();
            lwq.eq(CategoryEntity::getStoreId, storeId);
            lwq.like(null != name, CategoryEntity::getName, name);
            lwq.orderByAsc(CategoryEntity::getSort);
            return list(lwq);
        });
        return Result.success("", categoryEntities);
    }

    //TODO: 对在售菜品分类判定, 若该分类下有在售菜品, 则禁止删除
    @Transactional
    public boolean delCategory(List<CategoryEntity> categoryEntities) {
        Set<Long> idList = new HashSet<>();
        for (CategoryEntity category : categoryEntities) {
            idList.add(category.getId());
        }
        if (!removeByIds(idList)) {
            return false;
        }
        Long storeId = categoryEntities.get(0).getStoreId();
        if (!UserLocal.getUser().getStoreId().equals(storeId)) {
            return false;
        }
        stringRedisTemplate.delete(CacheConstant.STORE_CATEGORY + storeId);
        return true;
    }

    @Transactional
    public boolean updateCategory(CategoryEntity category) {
        if (!UserLocal.getUser().getStoreId().equals(category.getStoreId())) {
            return false;
        }
        if (!updateById(category)) {
            return false;
        }
        stringRedisTemplate.delete(CacheConstant.STORE_CATEGORY + category.getStoreId());
        return true;
    }

    public List<CategoryEntity> listCategory(CategoryEntity categoryEntity) {
        LambdaQueryWrapper<CategoryEntity> lwq = new LambdaQueryWrapper<>();
        lwq.eq(categoryEntity.getType() != null, CategoryEntity::getType, categoryEntity.getType());
        lwq.orderByAsc(CategoryEntity::getSort);
        return list(lwq);
    }
}
