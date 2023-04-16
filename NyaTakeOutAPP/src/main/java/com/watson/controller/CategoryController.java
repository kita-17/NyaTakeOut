package com.watson.controller;

import com.watson.common.Result;
import com.watson.common.ResultEnum;
import com.watson.common.UserParam;
import com.watson.entity.CategoryEntity;
import com.watson.services.impl.CategoryServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Resource
    private CategoryServiceImpl categoryService;

    @PostMapping
    public Result<String> onAddCategory(@RequestBody CategoryEntity ce) {
        Long id = categoryService.addCategory(ce);
        return Result.build(ResultEnum.CATEGORY_ADD_SUCCESS, "分类添加成功", id.toString());
    }

    @GetMapping()
    public Result onQueryCategory(Long storeId, String name) {
        return categoryService.getCategory(storeId, name);
    }

    @DeleteMapping
    public Result<String> onDeleteCategory(@RequestBody List<CategoryEntity> ids) {
        boolean res = categoryService.delCategory(ids);
        if (!res) {
            return Result.error(ResultEnum.CATEGORY_DELETE_FAIL, "分类删除失败");
        }
        return Result.success(ResultEnum.CATEGORY_DELETE_SUCCESS, "分类删除成功");
    }

    @PutMapping
    public Result<String> onUpdateCategory(@RequestBody CategoryEntity category) {
        boolean res = categoryService.updateCategory(category);
        if (!res) {
            return Result.error(ResultEnum.CATEGORY_UPDATE_FAIL, "分类信息更新失败");
        }
        return Result.success(ResultEnum.CATEGORY_UPDATE_SUCCESS, "分类信息更新成功");
    }

    @GetMapping("/list")
    public Result<List<CategoryEntity>> onListCategory(@RequestBody CategoryEntity category) {
        return Result.build(ResultEnum.SUCCESS, "", categoryService.listCategory(category));
    }
}
