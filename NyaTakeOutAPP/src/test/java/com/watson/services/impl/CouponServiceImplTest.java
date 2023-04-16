package com.watson.services.impl;

import com.watson.common.Result;
import com.watson.entity.CouponEntity;
import com.watson.mapper.CouponMapper;
import com.watson.services.CouponService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.stream.PendingMessagesSummary;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class CouponServiceImplTest {

    @Resource
    private CouponService couponService;

    @Resource
    private CouponMapper couponMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Test
    void getCouponWithStore() {
        Result result = couponService.getCouponWithStore(1L);
        log.info("CouponList: {}", result);
    }

    @Test
    void addCouponToStore() {
        CouponEntity coupon = new CouponEntity();
        coupon.setCouponCondition(20);
        coupon.setRule("满114减5");
        coupon.setDiscount(5);
        coupon.setValidDays(3);
        coupon.setStoreId(1L);
        coupon.setType(1);
        coupon.setCount(10);
        coupon.setName("先辈优惠券");
        couponService.addCouponToStore(coupon);
    }

    @Test
    void addCouponToUser() {
        CouponEntity coupon = new CouponEntity();
        coupon.setId(1641695140995399680L);
        //Result result = couponService.addCouponToUser(coupon, 1641792867378700289L);
        //log.info("Add Result: {}", result);
    }

    @Test
    void map() {
        List<CouponEntity> couponWithUser = couponMapper.getCouponWithUser(1640954971043831809L);
        log.info("{}", couponWithUser);
    }

    @Test
    void a() {
        StreamOperations<String, String, String> streamOperations = stringRedisTemplate.opsForStream();
        PendingMessagesSummary pendingMessagesSummary = streamOperations.pending("stream.coupon.orders", "coupon_group");
        // 所有pending消息的数量
        long totalPendingMessages = pendingMessagesSummary.getTotalPendingMessages();

        // 消费组名称
        String groupName= pendingMessagesSummary.getGroupName();

        // pending队列中的最小ID
        String minMessageId = pendingMessagesSummary.minMessageId();

        // pending队列中的最大ID
        String maxMessageId = pendingMessagesSummary.maxMessageId();

        log.info("消费组：{}，一共有{}条pending消息，最大ID={}，最小ID={}", groupName, totalPendingMessages, minMessageId, maxMessageId);
    }
}