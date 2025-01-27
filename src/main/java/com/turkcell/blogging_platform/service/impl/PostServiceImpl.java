package com.turkcell.blogging_platform.service.impl;

import com.turkcell.blogging_platform.dto.request.PostDtoRequest;
import com.turkcell.blogging_platform.dto.response.PostDtoResponse;
import com.turkcell.blogging_platform.entity.Post;
import com.turkcell.blogging_platform.entity.User;
import com.turkcell.blogging_platform.exception.UsernameNotFoundException;
import com.turkcell.blogging_platform.exception.handler.ErrorMessage;
import com.turkcell.blogging_platform.exception.handler.MessageType;
import com.turkcell.blogging_platform.repository.PostRepository;
import com.turkcell.blogging_platform.repository.UserRepository;
import com.turkcell.blogging_platform.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Override
    public PostDtoResponse createPost(PostDtoRequest request, String username) {
        //username'i controller tarafında securitycontext'ten alıyoruz
        //otomatik olarak giriş yapmış olan kullanıcının username'ini alıyoruz
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        new ErrorMessage(MessageType.USER_NOT_FOUND)));

        Post post = convertToPostEntity(request, user);

        Post savedPost = postRepository.save(post);

        return convertToPostDtoResponse(savedPost);
    }

    @Override
    public PostDtoResponse updatePost(PostDtoRequest request, UUID postId) {

        return null;
    }

    @Override
    public void deletePost(UUID postId) {

    }

    @Override
    public PostDtoResponse getPost(UUID postId) {
        return null;
    }

    @Override
    public List<PostDtoResponse> getAllPosts() {
        return List.of();
    }

    @Override
    public List<PostDtoResponse> getAllPostsByUser(UUID userId) {
        return List.of();
    }

    @Override
    public List<PostDtoResponse> getAllPostsByTitle(String title) {
        return List.of();
    }

    private PostDtoResponse convertToPostDtoResponse(Post post) {
        return PostDtoResponse.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .username(post.getUser().getUsername())
                .createdDate(post.getCreatedAt().toString())
                .updatedDate(post.getUpdatedAt().toString())
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
