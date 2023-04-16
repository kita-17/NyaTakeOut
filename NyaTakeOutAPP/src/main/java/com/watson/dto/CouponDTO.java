package com.watson.dto;

import com.watson.entity.CouponEntity;
import lombok.Data;

@Data
public class CouponDTO extends CouponEntity {
    private Long userId;
}
