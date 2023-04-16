package com.watson.services;

import com.watson.common.Result;
import com.watson.dto.CouponDTO;
import com.watson.entity.CouponEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author verrat
* @description 针对表【coupon(优惠券表)】的数据库操作Service
* @createDate 2023-03-31 14:13:14
*/
public interface CouponService extends IService<CouponEntity> {

    Result getCouponWithStore(Long storeId);

    Result addCouponToStore(CouponEntity coupon);

    Result addCouponToUser(CouponDTO coupon);

    Result removeCouponFromStore(Long[] coupon);

    Result removeCouponFromUser(Long userId);

    Result getCouponWithUser(Long userId);
}
