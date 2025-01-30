package com.turkcell.blogging_platform.service.impl;

import com.turkcell.blogging_platform.dto.request.LikeDtoRequest;
import com.turkcell.blogging_platform.dto.response.LikeDtoResponse;
import com.turkcell.blogging_platform.entity.Like;
import com.turkcell.blogging_platform.entity.Post;
import com.turkcell.blogging_platform.entity.User;
import com.turkcell.blogging_platform.exception.AlreadyLikedException;
import com.turkcell.blogging_platform.exception.NotLikedException;
import com.turkcell.blogging_platform.exception.PostNotFoundException;
import com.turkcell.blogging_platform.exception.UsernameNotFoundException;
import com.turkcell.blogging_platform.exception.handler.ErrorMessage;
import com.turkcell.blogging_platform.exception.handler.MessageType;
import com.turkcell.blogging_platform.repository.LikeRepository;
import com.turkcell.blogging_platform.repository.PostRepository;
import com.turkcell.blogging_platform.repository.UserRepository;
import com.turkcell.blogging_platform.service.LikeService;
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


    @Override
    public LikeDtoResponse likePost(LikeDtoRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(new ErrorMessage(MessageType.USER_NOT_FOUND)));

        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new PostNotFoundException(new ErrorMessage(MessageType.POST_NOT_FOUND)));

        if (isLiked(user, post)) {
            throw new AlreadyLikedException(new ErrorMessage(MessageType.ALREADY_LIKED));
        }

        Like newLike = Like.builder()
                .user(user)
                .post(post)
                .build();

        likeRepository.save(newLike);

        return convertToLikeDtoResponse(newLike, username);

    }



    @Override
    public LikeDtoResponse unlikePost(LikeDtoRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(new ErrorMessage(MessageType.USER_NOT_FOUND)));

        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new PostNotFoundException(new ErrorMessage(MessageType.POST_NOT_FOUND)));

        Like like = likeRepository.findByUserAndPost(user, post)
                .orElseThrow(() -> new NotLikedException(new ErrorMessage(MessageType.NOT_LIKED)));



        likeRepository.delete(like);

        return convertToLikeDtoResponse(like, username);
    }

    @Override
    public int getLikeCountByPostId(UUID postId) {

        return likeRepository.countByPostId(postId);

    }

    @Override
    public List<String> getLikedUsernamesByPostId(UUID postId) {
        return likeRepository.findByPostId(postId)
                .stream()
                .map(like -> like.getUser().getUsername())
                .toList();
    }

    private boolean isLiked(User user, Post post) {
        return likeRepository.existsByUserAndPost(user, post);
    }

    private LikeDtoResponse convertToLikeDtoResponse(Like like, String username) {
        return LikeDtoResponse.builder()
                .username(username)
                .firstName(like.getUser().getFirstName())
                .build();
    }


}
