package com.watson.services;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.watson.common.Result;
import com.watson.common.UserParam;
import com.watson.entity.CategoryEntity;

import java.util.List;

public interface CategoryService extends IService<CategoryEntity> {
    Long addCategory(CategoryEntity category);

    Result getCategory(Long storeId, String name);

    boolean delCategory(List<CategoryEntity> categoryEntities);

    boolean updateCategory(CategoryEntity categoryEntity);

    List<CategoryEntity> listCategory(CategoryEntity categoryEntity);
}
