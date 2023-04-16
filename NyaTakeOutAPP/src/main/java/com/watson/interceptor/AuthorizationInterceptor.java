package com.watson.interceptor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import com.watson.common.CacheConstant;
import com.watson.common.UserLocal;
import com.watson.dto.UserDTO;
import com.watson.exception.TokenTimeOutException;
import com.watson.utils.NyaJWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Token 拦截器
 * 负责token校验与token有效期刷新
 */
@Slf4j
public class AuthorizationInterceptor implements HandlerInterceptor {

    private final StringRedisTemplate stringRedisTemplate;

    public AuthorizationInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        //没有token
        if (StrUtil.isBlank(token)) {
            return true;
        }
        JWT jwt = JWTUtil.parseToken(token);
        Object accountType = jwt.getPayload("account-type");
        if (accountType == null) {
            return true;
        }
        String key = accountType.equals("employee") ?
                CacheConstant.EMPLOYEE_TOKEN + jwt.getPayload("userId") : CacheConstant.USER_TOKEN + jwt.getPayload("userId");

        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(key);
        if (entries.isEmpty()) {
            UserLocal.removeUser();
            return true;
        }
        String cToken = entries.get("token").toString();
        if (StrUtil.isBlank(cToken) || !cToken.equals(token)) {
            UserLocal.removeUser();
            return true;
        }
        if (!NyaJWTUtil.verify(cToken) && !NyaJWTUtil.verify(token)) { // token失效，从缓存删除
            stringRedisTemplate.delete(key);
            UserLocal.removeUser();
            return true;
        }
        UserDTO dto = BeanUtil.fillBeanWithMap(entries, new UserDTO(), true);
        UserLocal.saveUser(dto);
        stringRedisTemplate.expire(key, 30, TimeUnit.MINUTES);
        return true;
    }
}
