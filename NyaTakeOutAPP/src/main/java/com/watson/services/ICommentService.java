package com.watson.services;

import com.watson.common.Result;
import com.watson.dto.CommentDTO;
import com.watson.entity.CommentEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author verrat
 * @description 针对表【comment(评论表)】的数据库操作Service
 * @createDate 2023-03-30 14:38:46
 */
public interface ICommentService extends IService<CommentEntity> {
    Result addComment(CommentDTO comment);

    Result getCommentWithStoreId(Long userId, Long storeId);

    Result likeComment(CommentEntity comment);

    Result getUserComment(Long userId);

}
