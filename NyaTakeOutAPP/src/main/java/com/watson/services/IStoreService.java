package com.watson.services;

import com.watson.common.Result;
import com.watson.dto.StoreFavoriteDTO;
import com.watson.entity.StoreEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author verrat
* @description 针对表【StoreEntity(商店表)】的数据库操作Service
* @createDate 2023-03-29 14:24:11
*/
public interface IStoreService extends IService<StoreEntity> {
    Result getStoreList();

    Result addFavorite(StoreFavoriteDTO dto);

    Result removeFavorite(StoreFavoriteDTO dto);

    Result getFavoriteList(Long userId);

    Result isFavorite(Long userId, Long storeId);

    Result addStore(StoreEntity store);
    
}
