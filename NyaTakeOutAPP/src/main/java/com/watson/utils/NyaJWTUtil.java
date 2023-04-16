package com.watson.utils;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.jwt.JWTUtil;
import com.watson.common.CacheConstant;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class NyaJWTUtil {

    private static final String key = "nyaaaaaaaaaa";

    public static String create(HashMap<String, Object> cPayload) {
        DateTime now = DateTime.now();
        // 48小时有效期
        DateTime newTime = now.offsetNew(DateField.HOUR, 48);
        Map<String, Object> payload = new HashMap<>();
        //签发时间
        payload.put(JWTPayload.ISSUED_AT, now);
        //过期时间
        payload.put(JWTPayload.EXPIRES_AT, newTime);
        //生效时间
        payload.put(JWTPayload.NOT_BEFORE, now);
        //载荷
        payload.putAll(cPayload);
        return JWTUtil.createToken(payload, key.getBytes());
    }

    public static boolean verify(String token) {
        JWT jwt = JWTUtil.parseToken(token);
        return jwt.setKey(key.getBytes()).verify() && jwt.validate(0);
    }

    public static Object getValue(String token, String key) {
        JWT jwt = JWTUtil.parseToken(token);
        return jwt.getPayload(key);
    }
}
