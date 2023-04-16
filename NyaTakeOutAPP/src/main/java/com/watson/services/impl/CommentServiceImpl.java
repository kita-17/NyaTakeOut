package com.watson.services.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.watson.common.CacheConstant;
import com.watson.common.Result;
import com.watson.common.ResultEnum;
import com.watson.common.UserLocal;
import com.watson.dto.CommentDTO;
import com.watson.entity.CommentEntity;
import com.watson.entity.CommentImageEntity;
import com.watson.entity.CommentLikeEntity;
import com.watson.entity.UserEntity;
import com.watson.exception.SqlCustomException;
import com.watson.services.*;
import com.watson.mapper.CommentMapper;
import com.watson.utils.RedisUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author verrat
 * @description 针对表【comment(评论表)】的数据库操作Service实现
 * @createDate 2023-03-30 14:38:46
 */

@Slf4j
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, CommentEntity> implements ICommentService {

    @Resource
    private ICommentImageService commentImageService;

    @Resource
    private ICommentLikeService likeService;

    @Resource
    private UserService userService;

    @Resource
    private IStoreService storeService;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    @Transactional
    public Result addComment(CommentDTO comment) {
        if (!UserLocal.getUser().getId().equals(comment.getUserId())) {
            throw new RuntimeException("");
        }
        Long id = IdUtil.getSnowflakeNextId();
        comment.setId(id);
        comment.setCreateDate(LocalDate.now());
        boolean res = save(comment);
        if (!res) {
            return Result.error("评论发布失败，请重试");
        }
        //获取并设置id
        List<CommentImageEntity> imageList = comment.getImageList().stream().peek(i -> i.setCommentId(id)).toList();
        if (!imageList.isEmpty()) {
            boolean saveImage = commentImageService.saveBatch(imageList);
            if (!saveImage) {
                throw new RuntimeException("保存图片时失败");
            }
        }
        stringRedisTemplate.delete(CacheConstant.STORE_COMMENT + comment.getStoreId());
        return Result.success("评论发布成功", id);
    }

    @Override
    public Result getCommentWithStoreId(Long userId, Long storeId) {
        String key = CacheConstant.STORE_COMMENT + storeId;
        List<CommentDTO> result = redisUtil.getList(key, CommentDTO.class, 30L, TimeUnit.MINUTES, db -> {
            LambdaQueryWrapper<CommentEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(CommentEntity::getStoreId, storeId);
            queryWrapper.orderByDesc(CommentEntity::getCreateDate);
            List<CommentEntity> commentEntities = list(queryWrapper);
            return commentEntities.stream().map(comment -> {
                CommentDTO dto = new CommentDTO();
                BeanUtils.copyProperties(comment, dto);
                //装载图片
                LambdaQueryWrapper<CommentImageEntity> imageQueryWrapper = new LambdaQueryWrapper<>();
                imageQueryWrapper.eq(CommentImageEntity::getCommentId, comment.getId());
                List<CommentImageEntity> list = commentImageService.list(imageQueryWrapper);
                dto.setImageList(list);
                //用户是否点赞
                LambdaQueryWrapper<CommentLikeEntity> likeQuery = new LambdaQueryWrapper<>();
                likeQuery.eq(CommentLikeEntity::getUserId, userId);
                likeQuery.eq(CommentLikeEntity::getCommentId, comment.getId());
                CommentLikeEntity one = likeService.getOne(likeQuery);
                dto.setLiked(one != null);
                //设置品论的用户信息
                UserEntity userEntity = userService.getById(userId);
                if (userEntity != null) {
                    dto.setUserName(userEntity.getNickname());
                }
                return dto;
            }).toList();
        });
        return Result.success("", result);
    }

    @Override
    @Transactional
    public Result likeComment(CommentEntity comment) {
        boolean sqlRes; //sql更新的结果
        boolean result = false; //本次请求的结果
        String key = CacheConstant.COMMENT_LIKED + comment.getId();
        Boolean checkRes = stringRedisTemplate.opsForSet().isMember(key, comment.getUserId());
        LambdaUpdateWrapper<CommentEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(CommentEntity::getId, comment.getId());

        CommentLikeEntity likeEntity = new CommentLikeEntity();
        likeEntity.setUserId(comment.getUserId());
        likeEntity.setCommentId(comment.getId());

        //有点赞记录，取消点赞
        if (Boolean.TRUE.equals(checkRes)) {
            updateWrapper.setSql("likes = likes - 1");
            sqlRes = likeService.removeById(likeEntity);
            stringRedisTemplate.opsForSet().remove(key, comment.getUserId().toString());
        }
        //没点赞记录
        else {
            updateWrapper.setSql("likes = likes + 1");
            sqlRes = likeService.save(likeEntity);
            stringRedisTemplate.opsForSet().add(key, comment.getUserId().toString());
            result = true;
        }
        if (!update(updateWrapper) && !sqlRes) {
            throw new RuntimeException("点赞失败请重试");
        }
        return Result.success("", result);
    }

    @Override
    public Result getUserComment(Long userId) {
        String key = CacheConstant.USER_COMMENT + userId;
        List<CommentDTO> dtoList = redisUtil.getList(key, CommentDTO.class, 30L, TimeUnit.MINUTES, db -> {
            LambdaQueryWrapper<CommentEntity> commentQuery = new LambdaQueryWrapper<>();
            commentQuery.eq(CommentEntity::getUserId, userId);
            List<CommentEntity> commentEntities = list(commentQuery);
            return commentEntities.stream().map(comment -> {
                CommentDTO dto = new CommentDTO();
                BeanUtils.copyProperties(comment, dto);
                //设置店铺名称
                String title = storeService.getById(comment.getStoreId()).getTitle();
                dto.setStoreName(title);
                //装载图片
                LambdaQueryWrapper<CommentImageEntity> imageQueryWrapper = new LambdaQueryWrapper<>();
                imageQueryWrapper.eq(CommentImageEntity::getCommentId, comment.getId());
                List<CommentImageEntity> list = commentImageService.list(imageQueryWrapper);
                dto.setImageList(list);
                return dto;
            }).toList();
        });
        return Result.success("", dtoList);
    }
}




