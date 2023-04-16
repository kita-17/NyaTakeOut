package com.watson.dto;

import com.watson.entity.CommentEntity;
import com.watson.entity.CommentImageEntity;
import lombok.Data;

import java.util.List;

@Data
public class CommentDTO extends CommentEntity {
    private String userName;
    private boolean liked;
    private String storeName;
    private List<CommentImageEntity> imageList;
}
