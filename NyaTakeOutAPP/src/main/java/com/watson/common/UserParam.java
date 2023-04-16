package com.watson.common;

import lombok.Data;

@Data
public class UserParam<T> {
    private T ids;
    private Long storeId;
}
