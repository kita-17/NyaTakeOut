package com.watson.dto;

import com.watson.entity.AddressBookEntity;
import com.watson.entity.OrderDetailEntity;
import com.watson.entity.OrderEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class OrderDTO extends OrderEntity implements Serializable {
    String key;
    List<OrderDetailEntity> detail;
    AddressBookEntity address;
    boolean commented;
}
