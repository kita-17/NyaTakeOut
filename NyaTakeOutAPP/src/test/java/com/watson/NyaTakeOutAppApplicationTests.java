package com.watson;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import com.watson.common.CacheConstant;
import com.watson.dto.UserDTO;
import com.watson.entity.AddressBookEntity;
import com.watson.mapper.AddressBookMapper;
import com.watson.mapper.EmployeeMapper;
import com.watson.mapper.StoreMapper;
import com.watson.utils.NyaJWTUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@SpringBootTest
class NyaTakeOutAppApplicationTests {

    @Resource
    private StoreMapper storeMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    final String FILE_CACHE = "D:\\WorkSpace\\FileCache\\token.txt";

    @Test
    void contextLoads() {
        String token = NyaJWTUtil.create(new HashMap<>() {{
            put("13456789001", "123456");
        }});
        System.out.println(token);
    }

    @Test
    void ver() {
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJwYXNzV29yZCI6IjEyMzQ1NiIsIm5iZiI6MTY3OTczOTA3NywiZXhwIjoxNjc5NzM5Njc3LCJ1c2VyTmFtZSI6IjEzNDU2Nzg5MDAxIiwiaWF0IjoxNjc5NzM5MDc3fQ.LzaC7omDUb_6I4EyJASOiNpFxbHkZvOlocgxPOZHUf8";
        boolean verify = NyaJWTUtil.verify(token);
        System.out.println(verify);
    }

    @Test
    void payload() {
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJwYXNzV29yZCI6IjEyMzQ1NiIsIm5iZiI6MTY3OTczOTA3NywiZXhwIjoxNjc5NzM5Njc3LCJ1c2VyTmFtZSI6IjEzNDU2Nzg5MDAxIiwiaWF0IjoxNjc5NzM5MDc3fQ.LzaC7omDUb_6I4EyJASOiNpFxbHkZvOlocgxPOZHUf8";
        JWT jwt = JWTUtil.parseToken(token);
        Object userName = jwt.getPayload("userId");
        System.out.println(userName);
    }

    @Test
    void a() {
        List<?> allStore = storeMapper.getAllStore(1640954971043831809L);
        log.info("{}", allStore);
    }

    @Test
    void login2000User() {
        List<String> tokens = new ArrayList<>();
        for (int i = 0; i < 2000; i++) {
            Long id = IdUtil.getSnowflakeNextId();
            HashMap<String, Object> payload = new HashMap<>();
            payload.put("userId", id);
            payload.put("phone", "13456789001");
            payload.put("account-type", "nor-user");
            String key = CacheConstant.USER_TOKEN + id;
            String token = NyaJWTUtil.create(payload);

            UserDTO userDTO = new UserDTO();
            userDTO.setId(id);
            userDTO.setToken(token);
            userDTO.setUsername("nya_" + RandomUtil.randomString(10));
            userDTO.setPermissions(1);
            userDTO.setAccountType("nor-user");

            Map<String, Object> beanToMap = BeanUtil.beanToMap(userDTO, new HashMap<>(), CopyOptions.create()
                    .ignoreNullValue().setFieldValueEditor((k, value) -> value.toString()));

            stringRedisTemplate.opsForHash().putAll(key, beanToMap);
            stringRedisTemplate.expire(key, 30, TimeUnit.MINUTES);
            tokens.add(token);
        }
        writeToken(tokens);
        System.out.println("共写入: " + tokens.size() + "用户");
    }

    void writeToken(List<String> tokens) {
        try {
            File file = new File(FILE_CACHE);
            FileWriter fileWriter = new FileWriter(file);
            for (String token : tokens) fileWriter.write(token + "\n");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
