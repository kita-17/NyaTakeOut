package com.watson.controller;

import com.watson.common.Result;
import com.watson.dto.OrderDTO;
import com.watson.services.impl.OrdersServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrdersServiceImpl ordersService;

    @PostMapping
    public Result onAddOrders(@RequestBody OrderDTO dto) {
        return ordersService.onSaveOrders(dto);
    }

    @PutMapping
    public Result onUpdateOrders(@RequestBody OrderDTO dto) {
        return null;
    }


    @GetMapping
    public Result onListOrders(Long id) {
        return Result.success(ordersService.onListOrders(id));
    }

    /**
     * 给管理后台用的接口
     *
     * @param value 搜索框内容
     *
     * @return
     */
    @GetMapping("/page")
    public Result onPageOrders(Long storeId, String value) {
        return Result.success(ordersService.onPageOrders(storeId, value));
    }
}
