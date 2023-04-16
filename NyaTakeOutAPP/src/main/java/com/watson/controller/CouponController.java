package com.watson.controller;

import com.watson.common.Result;
import com.watson.dto.CouponDTO;
import com.watson.entity.CouponEntity;
import com.watson.services.CouponService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/coupon")
public class CouponController {

    @Resource
    private CouponService couponService;

    @GetMapping("/store")
    public Result getCouponListWithStore(Long storeId) {
        return couponService.getCouponWithStore(storeId);
    }

    @GetMapping("/user")
    public Result getCouponListWithUser(Long userId) {
        return couponService.getCouponWithUser(userId);
    }

    @PostMapping("/add/store")
    public Result addCouponToStore(@RequestBody CouponEntity coupon) {
        return couponService.addCouponToStore(coupon);
    }

    /**
     * TODO 优惠券购买，抢购
     *
     * @param coupon
     *
     * @return
     */
    @PostMapping("/add/user")
    public Result addCouponToUser(@RequestBody CouponDTO coupon) {
        return couponService.addCouponToUser(coupon);
    }

    @DeleteMapping("/store")
    public Result removeCouponFromStore(Long[] ids) {
        return couponService.removeCouponFromStore(ids);
    }

    @DeleteMapping("/user")
    public Result removeCouponFromUser(Long userId) {
        return couponService.removeCouponFromUser(userId);
    }
}
