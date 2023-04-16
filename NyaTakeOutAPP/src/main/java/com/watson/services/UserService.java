package com.watson.services;

import com.watson.common.Result;
import com.watson.dto.LoginDTO;
import com.watson.dto.UserDTO;
import com.watson.entity.UserEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author verrat
* @description 针对表【UserEntity(用户信息)】的数据库操作Service
* @createDate 2023-03-25 17:42:15
*/
public interface UserService extends IService<UserEntity> {
    Result login(LoginDTO dto);

    Result createCode(String phone);

    Result logout(UserDTO dto);
}
