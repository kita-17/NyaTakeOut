package com.watson.services.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.watson.entity.OrderDetailEntity;
import com.watson.services.OrderDetailService;
import com.watson.mapper.OrderDetailMapper;
import org.springframework.stereotype.Service;

/**
* @author verrat
* @description 针对表【order_detail(订单明细表)】的数据库操作Service实现
* @createDate 2023-02-23 13:45:28
*/
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetailEntity>
    implements OrderDetailService{

}




