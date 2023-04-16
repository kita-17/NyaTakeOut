package com.watson.services.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.watson.entity.CommentLikeEntity;
import com.watson.mapper.CommentLikeMapper;
import com.watson.services.ICommentLikeService;
import org.springframework.stereotype.Service;

/**
* @author verrat
* @description 针对表【comment_like(用户点赞其他人评论记录)】的数据库操作Service实现
* @createDate 2023-03-30 20:16:31
*/
@Service
public class CommentLikeServiceImpl extends ServiceImpl<CommentLikeMapper, CommentLikeEntity>
    implements ICommentLikeService {

}




