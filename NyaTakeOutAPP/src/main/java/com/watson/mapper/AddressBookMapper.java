package com.watson.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.watson.entity.AddressBookEntity;

import java.util.List;

/**
* @author verrat
* @description 针对表【address_book(地址管理)】的数据库操作Mapper
* @createDate 2023-02-22 16:19:11
* @Entity com.watson.entity.AddressBook
*/
public interface AddressBookMapper extends BaseMapper<AddressBookEntity> {
    List<AddressBookEntity> getAddressByUserId(Long id);
}




