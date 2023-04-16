package com.watson.config;

import com.watson.common.JacksonObjectMapper;
import com.watson.interceptor.AuthorizationInterceptor;
import com.watson.interceptor.UserInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

@Configuration
public class SpringWebConfig extends WebMvcConfigurationSupport {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter mc = new MappingJackson2HttpMessageConverter();
        mc.setObjectMapper(new JacksonObjectMapper());
        converters.add(0, mc);
    }

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        HandlerInterceptor auth = new AuthorizationInterceptor(stringRedisTemplate);
        HandlerInterceptor emp = new UserInterceptor();
        //
        registry.addInterceptor(auth)
                .addPathPatterns("/**");

        registry.addInterceptor(emp)
                //排除某些路径
                .excludePathPatterns("/store/list")
                .excludePathPatterns("/store/favorite/is")
                .excludePathPatterns("/coupon/store")
                .excludePathPatterns("/user/login")
                .excludePathPatterns("/user/code")
                .excludePathPatterns("/image/**")
                .excludePathPatterns("/file/**")
                .excludePathPatterns("/dish")
                .excludePathPatterns("/category")
                .excludePathPatterns("/setmeal")
                .excludePathPatterns("/setmeal/list")
                .excludePathPatterns("/comment")
                .excludePathPatterns("/employee/login");
    }
}
