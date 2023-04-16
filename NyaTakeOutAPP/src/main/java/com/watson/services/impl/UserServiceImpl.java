package com.watson.services.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.watson.common.CacheConstant;
import com.watson.common.Result;
import com.watson.common.UserLocal;
import com.watson.dto.LoginDTO;
import com.watson.dto.UserDTO;
import com.watson.entity.UserEntity;
import com.watson.services.UserService;
import com.watson.mapper.UserMapper;
import com.watson.utils.NyaJWTUtil;
import com.watson.utils.RegexUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author verrat
 * @description 针对表【UserEntity(用户信息)】的数据库操作Service实现
 * @createDate 2023-03-25 17:42:15
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /***
     * TODO 重复登录与挤下线
     * 使用userID当key，时间戳与token当值
     */
    @Override
    public Result login(LoginDTO dto) {
        String phone = dto.getPhone();
        if (RegexUtils.isPhoneInvalid(phone)) {
            return Result.error("手机号输入错误");
        }
        String code = stringRedisTemplate.opsForValue().get(CacheConstant.LOGIN_CODE + dto.getPhone());
        if (code == null || !code.equals(dto.getCode())) {
            return Result.error("验证码错误");
        }
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserEntity::getPhone, dto.getPhone());
        UserEntity userEntity = getOne(queryWrapper);
        if (userEntity == null) { // 新账号，注册
            userEntity = new UserEntity();
            userEntity.setPhone(dto.getPhone());
            userEntity.setNickname("nya_" + RandomUtil.randomString(10));
            boolean save = save(userEntity);
            if (!save) {
                throw new RuntimeException("注册失败");
            }
        }
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("userId", userEntity.getId());
        payload.put("phone", phone);
        payload.put("account-type", "nor-user");

        String key = CacheConstant.USER_TOKEN + userEntity.getId();
        String token = NyaJWTUtil.create(payload);

        UserDTO userDTO = new UserDTO();
        userDTO.setId(userEntity.getId());
        userDTO.setToken(token);
        userDTO.setUsername(userEntity.getNickname());
        userDTO.setPermissions(1);
        userDTO.setAccountType("nor-user");

        Map<String, Object> beanToMap = BeanUtil.beanToMap(userDTO, new HashMap<>(), CopyOptions.create()
                .ignoreNullValue().setFieldValueEditor((k, value) -> value.toString()));

        stringRedisTemplate.opsForHash().putAll(key, beanToMap);
        stringRedisTemplate.expire(key, 30, TimeUnit.MINUTES);
        stringRedisTemplate.delete(CacheConstant.LOGIN_CODE + phone);
        UserLocal.saveUser(userDTO);
        return Result.success("登陆成功", userDTO);
    }

    @Override
    public Result createCode(String phone) {
        if (RegexUtils.isPhoneInvalid(phone)) {
            return Result.error("手机号码错误");
        }
        String key = CacheConstant.LOGIN_CODE + phone;
        Boolean hasKey = stringRedisTemplate.hasKey(key);
        if (Boolean.TRUE.equals(hasKey)) {
            return Result.error("请勿重复申请验证码");
        }
        String code = RandomUtil.randomNumbers(6);
        stringRedisTemplate.opsForValue().set(key, code, 60, TimeUnit.SECONDS);
        log.info("code: {}", code);
        return Result.success("", code);
    }

    @Override
    public Result logout(UserDTO dto) {

        return null;
    }
}




