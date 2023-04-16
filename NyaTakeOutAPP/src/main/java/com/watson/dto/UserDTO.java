package com.watson.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String token;
    private String accountType;
    private Integer permissions;
    private Long storeId = 0L;
}
