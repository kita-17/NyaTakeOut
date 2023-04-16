package com.watson.dto;

import lombok.Data;

@Data
public class StoreDTO {
    private Long id;
    private String title;
    private String description;
    private String iconUrl;
    private boolean isLike;
}
