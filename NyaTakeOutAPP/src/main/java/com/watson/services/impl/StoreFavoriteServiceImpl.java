package com.watson.services.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.watson.common.CacheConstant;
import com.watson.common.Result;
import com.watson.dto.StoreDTO;
import com.watson.dto.StoreFavoriteDTO;
import com.watson.entity.StoreEntity;
import com.watson.services.IStoreFavoriteService;
import com.watson.mapper.StoreFavoriteMapper;
import com.watson.services.IStoreService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author verrat
 * @description 针对表【StoreFavoriteDTO(用户收藏的店铺)】的数据库操作Service实现
 * @createDate 2023-03-29 17:30:33
 */
@Service
public class StoreFavoriteServiceImpl extends ServiceImpl<StoreFavoriteMapper, StoreFavoriteDTO> implements IStoreFavoriteService {
}




