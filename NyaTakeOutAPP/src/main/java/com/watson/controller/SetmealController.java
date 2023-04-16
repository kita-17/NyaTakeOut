package com.watson.controller;

import com.watson.common.Result;
import com.watson.dto.SetmealDTO;
import com.watson.services.impl.SetmealServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealServiceImpl setmealService;

    @GetMapping("/list")
    public Result onListDish(Long storeId, String name, @RequestParam(required = false) boolean showHide) {
        return setmealService.onFetchData(storeId, name, showHide);
    }

    @PostMapping("/add")
    public Result onAddDish(@RequestBody SetmealDTO setmeal) {
        return Result.success(setmealService.addSetmeal(setmeal));
    }

    @DeleteMapping("/remove")
    public Result onDelDish(@RequestParam Long[] ids) {
        for (Long id : ids) {
            setmealService.removeSetmealById(id);
        }
        return Result.success("套餐删除成功");
    }

    @PutMapping("/update")
    public Result onUpdateDish(@RequestBody SetmealDTO e) {
        setmealService.updateSetmeal(e);
        return Result.success("套餐信息更新成功");
    }
}
