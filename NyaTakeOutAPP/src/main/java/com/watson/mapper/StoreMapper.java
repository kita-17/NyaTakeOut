package com.watson.mapper;

import com.watson.entity.StoreEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author verrat
* @description 针对表【StoreEntity(商店表)】的数据库操作Mapper
* @createDate 2023-03-29 14:24:11
* @Entity com.watson.entity.StoreEntity
*/
public interface StoreMapper extends BaseMapper<StoreEntity> {
    List<StoreEntity> getAllStore(Long userId);
}




