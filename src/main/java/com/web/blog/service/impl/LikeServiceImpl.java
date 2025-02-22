package com.web.blog.service.impl;

import com.web.blog.dto.request.LikeDtoRequest;
import com.web.blog.dto.response.LikeDtoResponse;
import com.web.blog.entity.Like;
import com.web.blog.entity.Post;
import com.web.blog.entity.User;
import com.web.blog.exception.AlreadyLikedException;
import com.web.blog.exception.NotLikedException;
import com.web.blog.exception.PostNotFoundException;
import com.web.blog.exception.UsernameNotFoundException;
import com.web.blog.exception.handler.ErrorMessage;
import com.web.blog.exception.handler.MessageType;
import com.web.blog.repository.LikeRepository;
import com.web.blog.repository.PostRepository;
import com.web.blog.repository.UserRepository;
import com.web.blog.service.LikeService;
import com.web.blog.service.UserActionLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final UserActionLogService userActionLogService;


    @Override
    public LikeDtoResponse likePost(LikeDtoRequest request, UUID userUuid) {
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new UsernameNotFoundException(new ErrorMessage(MessageType.USER_NOT_FOUND)));

        Post post = postRepository.findByUuid(request.getPostUuid()) // findByUuid kullanılmalı
                .orElseThrow(() -> new PostNotFoundException(new ErrorMessage(MessageType.POST_NOT_FOUND)));

        if (isLiked(user, post)) {
            throw new AlreadyLikedException(new ErrorMessage(MessageType.ALREADY_LIKED));
        }

        Like newLike = Like.builder()
                .user(user)
                .post(post)
                .build();

        likeRepository.save(newLike);
        userActionLogService.logAction(user.getUsername(), "Post liked");
        return convertToLikeDtoResponse(newLike);

    }



    @Override
    public LikeDtoResponse unlikePost(LikeDtoRequest request, UUID userUuid) {
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new UsernameNotFoundException(new ErrorMessage(MessageType.USER_NOT_FOUND)));

        Post post = postRepository.findByUuid(request.getPostUuid())
                .orElseThrow(() -> new PostNotFoundException(new ErrorMessage(MessageType.POST_NOT_FOUND)));

        Like like = likeRepository.findByUserAndPost(user, post)
                .orElseThrow(() -> new NotLikedException(new ErrorMessage(MessageType.NOT_LIKED)));



        likeRepository.delete(like);
        userActionLogService.logAction(user.getUsername(), "Post unliked");
        return convertToLikeDtoResponse(like);
    }

    @Override
    public int getLikeCountByPostId(UUID postId) {
        return likeRepository.countByPost_Uuid(postId);
    }

    @Override
    public List<String> getLikedUsernamesByPostId(UUID postId) {
        return likeRepository.findByPost_Uuid(postId)
                .stream()
                .map(like -> like.getUser().getUsername())
                .toList();
    }

    private boolean isLiked(User user, Post post) {
        return likeRepository.existsByUserAndPost(user, post);
    }

    private LikeDtoResponse convertToLikeDtoResponse(Like like) {
        String createdDateStr = (like.getCreatedAt() != null) ? like.getCreatedAt().toString() : "";

        return LikeDtoResponse.builder()
                .userUuid(like.getUser().getUuid())
                .postUuid(like.getPost().getUuid())
                .email(like.getUser().getEmail())
                .username(like.getUser().getUsername())
                .createdDate(createdDateStr)
                .build();
    }


}
