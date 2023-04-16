package com.watson.services.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.watson.common.CacheConstant;
import com.watson.common.Result;
import com.watson.common.ResultEnum;
import com.watson.common.UserLocal;
import com.watson.dto.EmployeeDTO;
import com.watson.dto.UserDTO;
import com.watson.entity.EmployeeEntity;
import com.watson.exception.SqlCustomException;
import com.watson.mapper.EmployeeMapper;
import com.watson.services.EmployeeService;
import com.watson.utils.NyaJWTUtil;
import jakarta.annotation.Resource;
import org.apache.catalina.User;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static cn.hutool.core.lang.Console.log;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, EmployeeEntity> implements EmployeeService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Long onAddEmployee(EmployeeEntity employee) {
        Long id = IdUtil.getSnowflakeNextId();
        employee.setId(id);
        boolean res = save(employee);
        if (!res) {
            throw new SqlCustomException(ResultEnum.FAIL, "该员工数据已存在");
        }
        return id;
    }

    public Result login(EmployeeEntity employee) {
        String password = employee.getPassword();
        //对密码进行加密, 这里偷懒用明文
        //password.md5();
        LambdaQueryWrapper<EmployeeEntity> lw = new LambdaQueryWrapper<>();
        lw.eq(EmployeeEntity::getUsername, employee.getUsername());
        EmployeeEntity emp = getOne(lw);
        if (emp == null) {
            return Result.error("登陆失败, 账号不存在");
        }
        if (!password.equals(emp.getPassword())) {
            return Result.error("密码错误");
        }
        if (!emp.isStatus()) {
            return Result.error("账户已禁用");
        }
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("userId", emp.getId());
        payload.put("password", emp.getPassword());
        payload.put("permissions", emp.getPermissions());
        payload.put("storeId", emp.getStoreId());
        payload.put("account-type", "employee");
        String token = NyaJWTUtil.create(payload);

        UserDTO dto = new UserDTO();
        dto.setId(emp.getId());
        dto.setUsername(emp.getName());
        dto.setToken(token);
        dto.setStoreId(emp.getStoreId());
        dto.setPermissions(emp.getPermissions());
        dto.setAccountType("employee");

        Map<String, Object> beanToMap = BeanUtil.beanToMap(dto, new HashMap<>(), CopyOptions.create().ignoreNullValue()
                .setFieldValueEditor((k, value) -> value.toString()));
        String key = CacheConstant.EMPLOYEE_TOKEN + emp.getId();
        stringRedisTemplate.delete(key);
        UserLocal.removeUser();
        stringRedisTemplate.opsForHash().putAll(key, beanToMap);
        stringRedisTemplate.expire(key, 30, TimeUnit.MINUTES);
        UserLocal.saveUser(dto);
        return Result.success("登陆成功", dto);
    }

    @Override
    public List<EmployeeEntity> onFetchData(Long storeId) {
        LambdaQueryWrapper<EmployeeEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EmployeeEntity::getStoreId, storeId);
        List<EmployeeEntity> entities = list(queryWrapper);
        return entities.stream().peek(i -> i.setPassword(null)).toList();
    }

    @Override
    public boolean onUpdateData(EmployeeEntity employee) {
        return updateById(employee);
    }
}
