package com.watson.controller;

import cn.hutool.core.util.IdUtil;
import com.watson.common.Result;
import com.watson.entity.AddressBookEntity;
import com.watson.services.impl.AddressBookServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/address")
public class AddressController {

    @Resource
    private AddressBookServiceImpl addressBookService;

    @GetMapping()
    public Result onGetAddress(Long id, Integer isDefault) {
        return Result.success(addressBookService.onGetAddress(id, isDefault));
    }

    @PostMapping()
    public Result onAddAddress(@RequestBody AddressBookEntity entity) {
        Long id = IdUtil.getSnowflakeNextId();
        entity.setId(id);
        addressBookService.onSaveAddress(entity);
        return Result.success("地址添加成功", id);
    }

    @PutMapping()
    public Result onUpdateAddress(@RequestBody AddressBookEntity entity) {
        addressBookService.onUpdateAddress(entity);
        return Result.success("地址信息更新成功");
    }

    @DeleteMapping()
    public Result onRemoveAddress(@RequestBody AddressBookEntity entity) {
        addressBookService.onRemoveAddress(entity);
        return Result.success("地址删除成功");
    }

}
