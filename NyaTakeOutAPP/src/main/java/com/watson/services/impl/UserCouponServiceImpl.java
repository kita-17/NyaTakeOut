package com.watson.services.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.watson.entity.UserCouponEntity;
import com.watson.services.UserCouponService;
import com.watson.mapper.UserCouponMapper;
import org.springframework.stereotype.Service;

/**
* @author verrat
* @description 针对表【user_coupon(用户拥有的优惠券表)】的数据库操作Service实现
* @createDate 2023-03-31 14:13:20
*/
@Service
public class UserCouponServiceImpl extends ServiceImpl<UserCouponMapper, UserCouponEntity>
    implements UserCouponService{

}




