package com.watson.controller;

import com.watson.common.Result;
import com.watson.common.UserLocal;
import com.watson.entity.EmployeeEntity;
import com.watson.services.impl.EmployeeServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Resource
    EmployeeServiceImpl employeeService;

    @PostMapping("/login")
    public Result<EmployeeEntity> login(@RequestBody EmployeeEntity employee) {
        return employeeService.login(employee);
    }

    @PostMapping()
    public Result<String> onAdd(@RequestBody EmployeeEntity employee) {
        return Result.success(employeeService.onAddEmployee(employee).toString());
    }

    @GetMapping
    public Result<List<EmployeeEntity>> onFetchEmployeeData(Long storeId) {
        log.info("{}",UserLocal.getUser());
        return Result.success(employeeService.onFetchData(storeId));
    }

    @PutMapping
    public Result onUpdateEmployeeData(@RequestBody EmployeeEntity employee) {
        boolean res = employeeService.onUpdateData(employee);
        if (!res) {
            return Result.error("员工信息更新失败");
        }
        return Result.success("员工信息更新成功");
    }
}
