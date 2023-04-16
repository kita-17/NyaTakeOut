package com.watson.dto;


import com.watson.entity.SetmealDishEntity;
import com.watson.entity.SetmealEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SetmealDTO extends SetmealEntity implements Serializable {
    Long key;
    String categoryName;
    //写作口味, 但其实是套餐的菜品信息
    List<SetmealDishEntity> flavor;
}
