package com.watson.services;

import com.baomidou.mybatisplus.extension.service.IService;
import com.watson.common.Result;
import com.watson.dto.OrderDTO;
import com.watson.entity.OrderEntity;

import java.util.List;

/**
 * @author verrat
 * @description 针对表【orders(订单表)】的数据库操作Service
 * @createDate 2023-02-23 13:45:23
 */
public interface OrdersService extends IService<OrderEntity> {
    Result onSaveOrders(OrderDTO ordersDTO);

    void onUpdateOrders(OrderDTO ordersDTO);

    List<OrderDTO> onListOrders(Long id);

    List<OrderDTO> onPageOrders(Long storeId, String value);
}
