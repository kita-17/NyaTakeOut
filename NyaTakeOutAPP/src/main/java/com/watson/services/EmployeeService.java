package com.watson.services;

import com.baomidou.mybatisplus.extension.service.IService;
import com.watson.common.Result;
import com.watson.entity.EmployeeEntity;

import java.util.List;

public interface EmployeeService extends IService<EmployeeEntity> {
    Long onAddEmployee(EmployeeEntity employee);
    Result<EmployeeEntity> login(EmployeeEntity employee);
    List<EmployeeEntity> onFetchData(Long storeId);
    boolean onUpdateData(EmployeeEntity employee);
}
