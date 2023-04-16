package com.watson.controller;

import com.watson.common.Result;
import com.watson.dto.CommentDTO;
import com.watson.entity.CommentEntity;
import com.watson.services.ICommentService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Resource
    private ICommentService commentService;
 
    @PostMapping()
    public Result addComment(@RequestBody CommentDTO comment) {
        return commentService.addComment(comment);
    }

    @GetMapping
    public Result getCommentWithStoreId(Long userId, Long storeId) {
        return commentService.getCommentWithStoreId(userId, storeId);
    }

    @PostMapping("/like")
    public Result commentLike(@RequestBody CommentEntity comment) {
        return commentService.likeComment(comment);
    }

    @GetMapping("/user")
    public Result getUserComment(Long userId) {
        return commentService.getUserComment(userId);
    }
}
