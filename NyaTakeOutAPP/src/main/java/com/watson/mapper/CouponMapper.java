package com.watson.mapper;

import com.watson.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author verrat
* @description 针对表【coupon(优惠券表)】的数据库操作Mapper
* @createDate 2023-03-31 14:13:14
* @Entity com.watson.entity.CouponEntity
*/
public interface CouponMapper extends BaseMapper<CouponEntity> {
    List<CouponEntity> getCouponWithUser(Long userId);
}




