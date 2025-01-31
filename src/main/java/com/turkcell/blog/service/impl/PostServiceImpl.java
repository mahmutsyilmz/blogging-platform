package com.turkcell.blog.service.impl;

import com.turkcell.blog.dto.request.PostDtoRequest;
import com.turkcell.blog.dto.response.PostDtoResponse;
import com.turkcell.blog.entity.Post;
import com.turkcell.blog.entity.User;
import com.turkcell.blog.exception.PostNotFoundException;
import com.turkcell.blog.exception.UsernameNotFoundException;
import com.turkcell.blog.exception.handler.ErrorMessage;
import com.turkcell.blog.exception.handler.MessageType;
import com.turkcell.blog.repository.PostRepository;
import com.turkcell.blog.repository.UserRepository;
import com.turkcell.blog.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Override
    public PostDtoResponse createPost(PostDtoRequest request, User user) {

        Post post = convertToPostEntity(request, user);

        Post savedPost = postRepository.save(post);

        return convertToPostDtoResponse(savedPost);
    }



    @Override
    @PreAuthorize("@postSecurityService.canEditPost(#postId, authentication)")
    public PostDtoResponse updatePost(PostDtoRequest request, UUID postId) {

        Post post = postRepository.findByUuid(postId)
                .orElseThrow(() -> new PostNotFoundException(new ErrorMessage(MessageType.POST_NOT_FOUND)));

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());

        Post updatedPost = postRepository.save(post);
        return convertToPostDtoResponse(updatedPost);
    }


    @Override
    @PreAuthorize("@postSecurityService.canEditPost(#postId, authentication)")
    public void deletePost(UUID postId) {

        Post post = postRepository.findByUuid(postId)
                .orElseThrow(() -> new PostNotFoundException(new ErrorMessage(MessageType.POST_NOT_FOUND)));

        postRepository.delete(post);
    }

    @Override
    public PostDtoResponse getPost(UUID postId) {

        Post post = postRepository.findByUuid(postId)
                .orElseThrow(() -> new PostNotFoundException(new ErrorMessage(MessageType.POST_NOT_FOUND)));

        return convertToPostDtoResponse(post);
    }

    @Override
    public List<PostDtoResponse> getAllPosts() {

        List<Post> posts = postRepository.findAll();

        return posts.stream()
                .map(this::convertToPostDtoResponse)
                .toList();
    }

    @Override
    public List<PostDtoResponse> getAllPostsByUser(UUID userId) {

        User user = userRepository.findByUuid(userId)
                .orElseThrow(() -> new UsernameNotFoundException(
                        new ErrorMessage(MessageType.USER_NOT_FOUND)));

        List<Post> posts = postRepository.findByUser((user));

        return posts.stream()
                .map(this::convertToPostDtoResponse)
                .toList();
    }

    @Override
    public List<PostDtoResponse> getAllPostsByTitle(String title) {

        List<Post> posts = postRepository.findByTitleContainingIgnoreCase(title);

        if (posts.isEmpty()) {
            throw new PostNotFoundException(new ErrorMessage(MessageType.POST_NOT_FOUND));
        }

        return posts.stream()
                .map(this::convertToPostDtoResponse)
                .toList();
    }

    private PostDtoResponse convertToPostDtoResponse(Post post) {
        return PostDtoResponse.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .username(post.getUser().getUsername())
                .createdDate(post.getCreatedAt().toString())
                .updatedDate(post.getUpdatedAt().toString())
                .likeCount(post.getLikes() != null ? post.getLikes().size() : 0)
                .build();
    }

    private Post convertToPostEntity(PostDtoRequest request, User user) {
        return Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .user(user)
                .build();
    }

}
