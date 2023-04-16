package com.watson.services.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.watson.entity.CommentImageEntity;
import com.watson.services.ICommentImageService;
import com.watson.mapper.CommentImageMapper;
import org.springframework.stereotype.Service;

/**
* @author verrat
* @description 针对表【comment_image(评论关联的图片)】的数据库操作Service实现
* @createDate 2023-03-30 14:38:46
*/
@Service
public class CommentImageServiceImpl extends ServiceImpl<CommentImageMapper, CommentImageEntity>
    implements ICommentImageService {

}




