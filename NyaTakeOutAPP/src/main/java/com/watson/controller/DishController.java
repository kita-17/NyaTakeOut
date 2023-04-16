package com.watson.controller;

import com.watson.common.Result;
import com.watson.dto.DishDTO;
import com.watson.services.impl.DishServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Resource
    private DishServiceImpl dishService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping
    public Result onListDish(Long storeId,
                             @RequestParam(required = false) String name,
                             @RequestParam(required = false) boolean showHide
    ) {
        return dishService.onFetchData(storeId, name, showHide);
    }

    @PostMapping("/add")
    public Result onAddDish(@RequestBody DishDTO dish) {
        return dishService.addDish(dish);
    }

    @DeleteMapping("/remove")
    public Result onDelDish(@RequestParam Long[] ids) {
        return dishService.removeDishById(ids);
    }

    @PutMapping("/update")
    public Result onUpdateDish(@RequestBody DishDTO dish) {
        boolean res = dishService.updateDish(dish);
        if (!res) {
            return Result.error("菜品信息更新失败");
        }
        return Result.success("菜品信息更新成功");
    }
}
