package com.watson.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.watson.entity.DishEntity;
import com.watson.entity.DishFlavorEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DishDTO extends DishEntity implements Serializable {
    Long key;
    String categoryName;
    List<DishFlavorEntity> flavor;
}
