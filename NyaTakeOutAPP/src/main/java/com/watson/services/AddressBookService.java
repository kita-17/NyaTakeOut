package com.watson.services;

import com.watson.entity.AddressBookEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author verrat
* @description 针对表【address_book(地址管理)】的数据库操作Service
* @createDate 2023-02-22 16:19:11
*/
public interface AddressBookService extends IService<AddressBookEntity> {
    List<AddressBookEntity> onGetAddress(Long id, Integer isDefault);
    void onRemoveAddress(AddressBookEntity addressBook);
    void onUpdateAddress(AddressBookEntity addressBook);
    void onSaveAddress(AddressBookEntity entity);
}
