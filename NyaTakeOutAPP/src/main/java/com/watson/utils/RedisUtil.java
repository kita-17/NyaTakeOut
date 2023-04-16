package com.watson.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.watson.common.CacheConstant;
import com.watson.common.ThreadUtil;
import com.watson.entity.AddressBookEntity;
import com.watson.entity.RedisData;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Slf4j
@Component
public class RedisUtil {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private Redisson redisson;

    @Resource
    private ThreadUtil threadUtil;

    /**
     * 添加字符串类型的缓存
     *
     * @param key      缓存key
     * @param data     需要缓存的数据
     * @param expire   缓存有效时间
     * @param timeUnit 时间单位
     */
    public <E, T> void set(final E key, final T data, Long expire, TimeUnit timeUnit) {
        stringRedisTemplate.opsForValue().set(String.valueOf(key), JSONUtil.toJsonStr(data), expire, timeUnit);
    }

    /**
     * 添加字符串类型的缓存，并设置逻辑过期时间
     *
     * @param key      缓存key
     * @param data     需要缓存的数据
     * @param expire   缓存有效时间
     * @param timeUnit 时间单位
     */
    public <E, T> void setWithLogicalExpire(final E key, T data, Long expire, TimeUnit timeUnit) {
        RedisData<T> redisData = new RedisData<>();
        redisData.setData(data);
        redisData.setExpire(LocalDateTime.now().plusSeconds(timeUnit.toSeconds(expire)));
        stringRedisTemplate.opsForValue().set(String.valueOf(key), JSONUtil.toJsonStr(redisData));
    }

    /**
     * 获取字符串类型的缓存，如果缓存不存在则从数据库查询并写入缓存
     * 若数据库也不存在，则缓存一个1分钟的空对象，防止穿透
     *
     * @param key      缓存key
     * @param type     返回的缓存数据类型
     * @param expire   缓存有效时间
     * @param timeUnit 时间单位
     * @param callback 数据库查询逻辑
     */
    public <I, T> T get(final I key, Class<T> type, Long expire, TimeUnit timeUnit, Function<I, T> callback) {
        String json = stringRedisTemplate.opsForValue().get(String.valueOf(key));
        //缓存存在，直接返回
        if (StrUtil.isNotBlank(json)) {
            return JSONUtil.toBean(json, type);
        }
        //缓存数据存在，但是为空值，说明这个数据是空对象。直接返回
        if (json != null) {
            return null;
        }
        //查询数据
        T result = callback.apply(key); //缓存数据不存在，根据条件查数据库数据
        //数据库数据不存在
        if (result == null) {
            //缓存空对象
            stringRedisTemplate.opsForValue().set(String.valueOf(key), "", CacheConstant.CACHE_NULL_TTL, TimeUnit.MINUTES);
            return null;
        }
        //新数据写入缓存
        this.set(key, result, expire, timeUnit);
        return result;
    }

    /**
     * 获取列表类型的缓存
     *
     * @param key      缓存key
     * @param type     返回的缓存数据类型
     * @param expire   缓存有效时间
     * @param timeUnit 时间单位
     * @param callback 数据库查询逻辑
     */
    public <I, T> List<T> getList(final I key, Class<T> type, Long expire, TimeUnit timeUnit, Function<I, List<T>> callback) {
        String k = String.valueOf(key);
        List<String> jsonList = stringRedisTemplate.opsForList().range(k, 0, -1);
        if (jsonList != null && jsonList.size() > 0) {
            if (!jsonList.get(0).equals("1")) {
                List<T> dataList = new ArrayList<>();
                jsonList.forEach(i -> dataList.add(JSONUtil.toBean(i, type)));
                return dataList;
            } else return null; //缓存数据存在，但是为空值，说明这个数据是空对象。直接返回
        }
        //查询数据
        List<T> result = callback.apply(key); //缓存数据不存在，根据条件查数据库数据
        //数据库数据不存在
        if (result == null || result.isEmpty()) {
            //缓存空对象
            stringRedisTemplate.opsForList().leftPush(k, "1");
            stringRedisTemplate.expire(k, CacheConstant.CACHE_NULL_TTL, TimeUnit.MINUTES);
            return null;
        }
        List<String> resultList = result.stream().map(JSONUtil::toJsonStr).toList();
        stringRedisTemplate.opsForList().rightPushAll(k, resultList);
        stringRedisTemplate.expire(k, expire, timeUnit);
        return result;
    }

    /**
     * <p>逻辑过期</p>
     * <p>如果缓存不存在，则返回空。并开启独立线程重建缓存</p>
     * <p>如果缓存逻辑过期，则返回旧数据，并开启独立线程重建缓存</p>
     *
     * @param key      缓存key
     * @param type     返回的缓存数据类型
     * @param expire   缓存有效时间
     * @param timeUnit 时间单位
     * @param callback 数据库查询逻辑
     */
    public <E, T> T getWithLogicalExpire(final E key, final String lockKey, Class<T> type, Long expire, TimeUnit timeUnit, Function<E, T> callback) {
        String dataJson = stringRedisTemplate.opsForValue().get(key);
        //缓存未命中，直接返回
        if (StrUtil.isBlank(dataJson)) return null;
        //缓存命中
        RedisData redisData = JSONUtil.toBean(dataJson, RedisData.class);
        LocalDateTime cacheExpire = redisData.getExpire();
        T result = JSONUtil.toBean((JSONObject) redisData.getData(), type);
        //缓存未过期，直接返回数据
        if (cacheExpire.isAfter(LocalDateTime.now())) {
            return result;
        }
        //缓存逻辑过期，开线程重建缓存
        RLock lock = redisson.getLock(String.valueOf(lockKey));
        if (lock.tryLock()) {
            ////获取锁成功，异步重建缓存
            rebuild(key, lock, expire, timeUnit, callback);
        }
        //
        return result;
    }

    /**
     * 异步重建缓存方法
     *
     * @param key
     * @param lock
     * @param expire
     * @param timeUnit
     * @param callback
     * @param <E>
     * @param <T>
     */
    @Async
    protected <E, T> void rebuild(final E key, final RLock lock, Long expire, TimeUnit timeUnit, Function<E, T> callback) {
        try {
            //实现缓存重建
            T dbResult = callback.apply(key);
            this.setWithLogicalExpire(key, dbResult, expire, timeUnit);
        } catch (Exception e) {
            throw new RuntimeException();
        } finally {
            lock.unlock();
        }
    }
}
