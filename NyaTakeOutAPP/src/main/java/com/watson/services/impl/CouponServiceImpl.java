package com.watson.services.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.watson.common.CacheConstant;
import com.watson.common.Result;
import com.watson.common.UserLocal;
import com.watson.dto.CouponDTO;
import com.watson.entity.CouponEntity;
import com.watson.entity.UserCouponEntity;
import com.watson.services.CouponService;
import com.watson.mapper.CouponMapper;
import com.watson.services.IStoreService;
import com.watson.services.UserCouponService;
import com.watson.utils.RedisUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author verrat
 * @description 针对表【coupon(优惠券表)】的数据库操作Service实现
 * @createDate 2023-03-31 14:13:14
 */
@Slf4j
@Service
public class CouponServiceImpl extends ServiceImpl<CouponMapper, CouponEntity> implements CouponService {

    @Resource
    private UserCouponService userCouponService;

    @Resource
    private CouponMapper couponMapper;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private static final DefaultRedisScript<Long> FLASH_SALE_SCRIPT;

    static {
        FLASH_SALE_SCRIPT = new DefaultRedisScript<>();
        FLASH_SALE_SCRIPT.setLocation(new ClassPathResource("FlashSale.lua"));
        FLASH_SALE_SCRIPT.setResultType(Long.class);
    }

    @Override
    public Result getCouponWithStore(Long storeId) {
        LambdaQueryWrapper<CouponEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CouponEntity::getStoreId, storeId);
        queryWrapper.orderByDesc(CouponEntity::getType);
        List<CouponEntity> couponEntities = list(queryWrapper);
        //缓存限购优惠券的库存
        for (CouponEntity coupon : couponEntities) {
            if (coupon.getType().equals(1)) {
                String cKey = CacheConstant.COUPON_COUNT + coupon.getId();
                stringRedisTemplate.opsForValue().set(cKey, coupon.getCount().toString());
            }
        }
        return Result.success("", couponEntities);
    }

    @Override
    public Result getCouponWithUser(Long userId) {
        return Result.success("", couponMapper.getCouponWithUser(userId));
    }

    @Override
    @Transactional
    public Result addCouponToStore(CouponEntity coupon) {
        long id;
        if (coupon.getId() == null || coupon.getId() <= 10) {
            id = IdUtil.getSnowflakeNextId();
            coupon.setId(id);
        }
        boolean res = saveOrUpdate(coupon);
        if (!res) {
            throw new RuntimeException("操作失败");
        }
        return Result.success("操作成功", coupon.getId());
    }

    @Override
    @Transactional
    public Result addCouponToUser(CouponDTO coupon) {
        long orderId = IdUtil.getSnowflakeNextId();
        Long userId = UserLocal.getUser().getId();
        if (coupon.getType().equals(1)) {
            Long res = stringRedisTemplate.execute(
                    FLASH_SALE_SCRIPT,
                    Collections.emptyList(),
                    coupon.getId().toString(),
                    userId.toString(),
                    String.valueOf(orderId)
            );
            assert res != null;
            if (res.intValue() != 0) {
                return Result.error(res.intValue() == 1 ? "库存不足" : "不能重复下单");
            }
        } else saveCouponToDataBase(coupon);
        return Result.success("购买成功", orderId);
    }

    @Override
    @Transactional
    public Result removeCouponFromStore(Long[] coupon) {
        boolean res = removeByIds(Arrays.asList(coupon));
        if (!res) {
            throw new RuntimeException("删除失败");
        }
        return Result.success("删除成功");
    }

    @Override
    public Result removeCouponFromUser(Long userId) {
        return null;
    }

    @PostConstruct
    public void init() {
        ThreadUtil.execAsync(this::handle);
    }

    public void handle() {
        while (true) {
            try {
                List<MapRecord<String, Object, Object>> list = stringRedisTemplate.opsForStream().read(
                        Consumer.from("coupon_group", "coupon_handler"),
                        StreamReadOptions.empty().count(1).block(Duration.ofSeconds(2L)),
                        StreamOffset.create("stream.coupon.orders", ReadOffset.lastConsumed())
                );
                //没有订单信息
                if (list == null || list.isEmpty()) {
                    continue;
                }
                //有订单信息 - 获取消息队列消息
                MapRecord<String, Object, Object> record = list.get(0);
                Map<Object, Object> value = record.getValue();
                CouponDTO dto = BeanUtil.fillBeanWithMap(value, new CouponDTO(), false);
                saveCouponToDataBase(dto);
                //proxy.createVoucherOrder(order);
                //ACK确认消息处理
                stringRedisTemplate.opsForStream().acknowledge("stream.coupon.orders", "coupon_group", record.getId());
            } catch (Exception e) {
                log.error("订单处理异常: {}", e.getMessage());
                //处理等待列表
                handlePending();
            }
        }
    }

    private void handlePending() {
        while (true) {
            try {
                List<MapRecord<String, Object, Object>> list = stringRedisTemplate.opsForStream().read(
                        Consumer.from("coupon_group", "coupon_handler"),
                        StreamReadOptions.empty().count(1),
                        StreamOffset.create("stream.coupon.orders", ReadOffset.from("0"))
                );
                //Pending-List 没有消息直接结束循环
                if (list == null || list.isEmpty()) {
                    break;
                }
                //获取消息队列消息
                MapRecord<String, Object, Object> record = list.get(0);
                Map<Object, Object> value = record.getValue();
                CouponDTO dto = BeanUtil.fillBeanWithMap(value, new CouponDTO(), false);
                //if(proxy != null) {
                //    proxy.createVoucherOrder(order);
                //    //ACK确认消息处理
                //
                //}
                saveCouponToDataBase(dto);
                stringRedisTemplate.opsForStream().acknowledge("stream.coupon.orders", "coupon_group", record.getId());
            } catch (Exception e) {
                log.error("PENDING-LIST - 订单处理异常: {}", e.getMessage());
            }
        }
    }

    private void saveCouponToDataBase(CouponDTO coupon) {
        LambdaQueryWrapper<CouponEntity> cQuery = new LambdaQueryWrapper<>();
        cQuery.eq(CouponEntity::getId, coupon.getId());
        CouponEntity one = this.getOne(cQuery);
        if (one == null) { // 优惠券不存在
            throw new RuntimeException("优惠券错误");
        }
        // 当优惠券是限购券，且购买过了
        if (one.getType().equals(1)) {
            LambdaQueryWrapper<UserCouponEntity> ucQuery = new LambdaQueryWrapper<>();
            ucQuery.eq(UserCouponEntity::getUserId, coupon.getUserId());
            ucQuery.eq(UserCouponEntity::getCouponId, coupon.getId());
            UserCouponEntity uc = userCouponService.getOne(ucQuery);
            if (uc != null) { // 购买过了
                throw new RuntimeException("用户已购买过此优惠券，禁止重复购买");
            }
        }
        //没购买过
        if (one.getCount() <= 0) { // 库存不足
            throw new RuntimeException("优惠券库存不足");
        }
        //库存充足下允许购买
        LambdaUpdateWrapper<CouponEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(CouponEntity::getId, coupon.getId());
        updateWrapper.gt(CouponEntity::getCount, 0);
        updateWrapper.setSql("count = count - 1");
        boolean updateRes = update(updateWrapper);
        if (!updateRes) {
            throw new RuntimeException("购买失败");
        }
        UserCouponEntity userCoupon = new UserCouponEntity();
        userCoupon.setUserId(coupon.getUserId());
        userCoupon.setCouponId(coupon.getId());
        //设置优惠券过期时间
        LocalDateTime dateTime = LocalDateTime.now().plusDays(one.getValidDays());
        userCoupon.setDate(dateTime);
        boolean save = userCouponService.save(userCoupon);
        if (!save) throw new RuntimeException("购买失败");
    }
}




