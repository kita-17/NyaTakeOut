package com.watson.dto;

import lombok.Data;

@Data
public class EmployeeDTO {
    private String name;
    private String token;
    private Long storeId;
    private Integer permissions;
}
