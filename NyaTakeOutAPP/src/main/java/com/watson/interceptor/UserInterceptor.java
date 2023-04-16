package com.watson.interceptor;

import com.watson.common.UserLocal;
import com.watson.dto.UserDTO;
import com.watson.exception.TokenTimeOutException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class UserInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        UserDTO user = UserLocal.getUser();
        log.info("服务器上的用户信息: {}", user);
        if (user == null) {//没用户信息，拦截
            response.setStatus(401);
            throw new TokenTimeOutException("请登陆后再尝试");
        }
        //有用户信息，放行
        return true;
    }
}
