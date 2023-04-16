package com.watson.controller;

import com.watson.common.Result;
import com.watson.dto.StoreFavoriteDTO;
import com.watson.entity.StoreEntity;
import com.watson.services.IStoreService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/store")
public class StoreController {

    @Resource
    private IStoreService storeService;

    /**
     * 获取所有店铺信息（名字，描述等，不包含菜品）
     */
    @GetMapping("/list")
    public Result getAllStore() {
        return storeService.getStoreList();
    }

    /**
     * 收藏店铺
     */
    @PostMapping("/favorite/add")
    public Result add(@RequestBody StoreFavoriteDTO dto) {
        return storeService.addFavorite(dto);
    }

    /**
     * 取消收藏店铺
     */
    @PostMapping("/favorite/remove")
    public Result remove(@RequestBody StoreFavoriteDTO dto) {
        return storeService.removeFavorite(dto);
    }

    /**
     * 获取用户收藏额店铺id列表
     * @param userId
     */
    @GetMapping("/favorite/list")
    public Result favList(Long userId) {
        return storeService.getFavoriteList(userId);
    }

    /**
     * 判断用户是否收藏了某店铺
     * 从redis获取并判断。
     * TODO 感觉没必要
     */
    @GetMapping("/favorite/is")
    public Result isFav(Long userId, Long storeId) {
        return storeService.isFavorite(userId, storeId);
    }

    @PostMapping("/add")
    public Result addStore(@RequestBody StoreEntity store) {
        return storeService.addStore(store);
    }
}
