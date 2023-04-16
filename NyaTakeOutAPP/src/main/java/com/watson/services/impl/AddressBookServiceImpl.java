package com.watson.services.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.watson.common.CacheConstant;
import com.watson.common.ResultEnum;
import com.watson.common.UserLocal;
import com.watson.entity.AddressBookEntity;
import com.watson.exception.SqlCustomException;
import com.watson.mapper.AddressBookMapper;
import com.watson.services.AddressBookService;
import com.watson.utils.RedisUtil;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBookEntity>
        implements AddressBookService {

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public List onGetAddress(Long id, Integer isDefault) {
        return redisUtil.getList(CacheConstant.USER_ADDRESS + id, AddressBookEntity.class, 30L, TimeUnit.MINUTES,
                db -> {
                    LambdaQueryWrapper<AddressBookEntity> addressLQW = new LambdaQueryWrapper<>();
                    addressLQW.eq(id != null, AddressBookEntity::getUserId, id);
                    addressLQW.eq(isDefault != null, AddressBookEntity::getIsDefault, isDefault);
                    addressLQW.orderByAsc(AddressBookEntity::getIsDefault);
                    return list(addressLQW);
                }
        );
    }

    @Override
    @Transactional
    public void onRemoveAddress(AddressBookEntity entity) {
        if (UserLocal.getUser().getId().equals(entity.getUserId())) {
            boolean res = removeById(entity.getId());
            if (!res) {
                throw new SqlCustomException(ResultEnum.FAIL, "地址删除错误");
            }
            stringRedisTemplate.delete(CacheConstant.USER_ADDRESS + entity.getUserId());
        }
    }

    @Override
    @Transactional
    public void onUpdateAddress(AddressBookEntity addressBook) {
        if (UserLocal.getUser().getId().equals(addressBook.getUserId())) {
            boolean res = updateById(addressBook);
            if (!res) {
                throw new SqlCustomException(ResultEnum.FAIL, "地址信息更新错误");
            }
            stringRedisTemplate.delete(CacheConstant.USER_ADDRESS + addressBook.getUserId());
        }
    }

    @Override
    public void onSaveAddress(AddressBookEntity entity) {
        if (UserLocal.getUser().getId().equals(entity.getUserId())) {
            boolean res = save(entity);
            if (!res) {
                throw new SqlCustomException(ResultEnum.FAIL, "地址信息保存错误");
            }
            stringRedisTemplate.delete(CacheConstant.USER_ADDRESS + entity.getUserId());
        }
    }
}




