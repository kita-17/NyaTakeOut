package com.watson.services.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.watson.common.CacheConstant;
import com.watson.common.Result;
import com.watson.dto.StoreDTO;
import com.watson.dto.StoreFavoriteDTO;
import com.watson.entity.StoreEntity;
import com.watson.mapper.StoreMapper;
import com.watson.services.IStoreService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author verrat
 * @description 针对表【StoreEntity(商店表)】的数据库操作Service实现
 * @createDate 2023-03-29 14:24:11
 */
@Service
public class StoreServiceImpl extends ServiceImpl<StoreMapper, StoreEntity> implements IStoreService {

    @Resource
    private StoreFavoriteServiceImpl storeFavoriteService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result getStoreList() {
        List<StoreEntity> storeList = list();
        List<StoreDTO> dtoList = storeList.stream().map(store -> {
            StoreDTO dto = new StoreDTO();
            BeanUtils.copyProperties(store, dto, "ownerId", "status");
            return dto;
        }).toList();
        return Result.success(dtoList);
    }

    @Override
    public Result addFavorite(StoreFavoriteDTO dto) {
        String key = CacheConstant.USER_FAVORITE + dto.getUserId();
        boolean save = storeFavoriteService.save(dto);
        if (!save) {
            return Result.error("收藏失败，请重试");
        }
        stringRedisTemplate.opsForSet().add(key, dto.getStoreId().toString());
        return Result.success("收藏成功");
    }

    @Override
    public Result removeFavorite(StoreFavoriteDTO dto) {
        String key = CacheConstant.USER_FAVORITE + dto.getUserId();
        LambdaQueryWrapper<StoreFavoriteDTO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StoreFavoriteDTO::getUserId, dto.getUserId());
        queryWrapper.eq(StoreFavoriteDTO::getStoreId, dto.getStoreId());
        boolean remove = storeFavoriteService.remove(queryWrapper);
        if (!remove) {
            return Result.error("取消收藏失败，请重试");
        }
        stringRedisTemplate.opsForSet().remove(key, dto.getStoreId().toString());
        return Result.success("取消收藏成功");
    }

    @Override
    public Result getFavoriteList(Long userId) {
        LambdaQueryWrapper<StoreFavoriteDTO> q1 = new LambdaQueryWrapper<>();
        q1.eq(StoreFavoriteDTO::getUserId, userId);
        List<Long> favoriteDTOS = storeFavoriteService.list(q1).stream().map(StoreFavoriteDTO::getStoreId).toList();
        if (favoriteDTOS.isEmpty()) {
            return Result.success(null);
        }
        List<StoreEntity> storeEntities = listByIds(favoriteDTOS);
        List<StoreDTO> list = storeEntities.stream().map(store -> {
            StoreDTO storeDTO = new StoreDTO();
            BeanUtils.copyProperties(store, storeDTO, "ownerId", "status");
            return storeDTO;
        }).toList();
        return Result.success(list);
    }

    @Override
    public Result isFavorite(Long userId, Long storeId) {
        String key = CacheConstant.USER_FAVORITE + userId;
        if (Boolean.TRUE.equals(stringRedisTemplate.opsForSet().isMember(key, storeId.toString()))) {
            return Result.success(true);
        }
        return Result.error(false);
    }

    @Override
    public Result addStore(StoreEntity store) {
        long id = IdUtil.getSnowflakeNextId();
        store.setId(id);
        boolean res = save(store);
        if (!res) {
            return Result.error("新增店铺失败");
        }
        return Result.success(store.getTitle() + "店铺添加成功");
    }
}