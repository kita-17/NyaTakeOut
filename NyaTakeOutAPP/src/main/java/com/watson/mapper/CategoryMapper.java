package com.watson.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.watson.entity.CategoryEntity;

import java.util.List;

public interface CategoryMapper extends BaseMapper<CategoryEntity> {
    List<CategoryEntity> getCategoryByStoreId(Long id);

    int insertCategory(CategoryEntity entity);

    int deleteCategoryById(Long id);

    int updateCategory(CategoryEntity entity);
}
