package com.turkcell.blog.service.impl;

import com.turkcell.blog.dto.request.PostDtoRequest;
import com.turkcell.blog.dto.response.PostDtoResponse;
import com.turkcell.blog.entity.Post;
import com.turkcell.blog.entity.PostRequest;
import com.turkcell.blog.entity.User;
import com.turkcell.blog.exception.AlreadyPostRequestExists;
import com.turkcell.blog.exception.PostNotFoundException;
import com.turkcell.blog.exception.UsernameNotFoundException;
import com.turkcell.blog.exception.handler.ErrorMessage;
import com.turkcell.blog.exception.handler.MessageType;
import com.turkcell.blog.repository.PostRepository;
import com.turkcell.blog.repository.PostRequestRepository;
import com.turkcell.blog.repository.UserRepository;
import com.turkcell.blog.service.PostService;
import com.turkcell.blog.service.UserActionLogService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostRequestRepository postRequestRepository;
    private final UserActionLogService userActionLogService;

    @Override
    public PostDtoResponse createPost(PostDtoRequest request, User user) {


        PostRequest postRequest = PostRequest.builder()
                .requestType(PostRequest.RequestType.CREATE)
                .newTitle(request.getTitle())
                .newContent(request.getContent())
                .user(user)
                .status(PostRequest.RequestStatus.PENDING)
                .build();

        PostRequest savedRequest = postRequestRepository.save(postRequest);
        userActionLogService.logAction(user.getUsername(), "Post create request");

        return PostDtoResponse.builder()
                .title(savedRequest.getNewTitle())
                .content(savedRequest.getNewContent())
                .username(user.getUsername())
                .createdDate(savedRequest.getCreatedAt().toString())
                .build();
    }



    @Override
    @PreAuthorize("@postSecurityService.canEditPost(#postId, authentication)")
    public PostDtoResponse updatePost(PostDtoRequest request, UUID postId) {

        Post post = postRepository.findByUuid(postId)
                .orElseThrow(() -> new PostNotFoundException(new ErrorMessage(MessageType.POST_NOT_FOUND)));


        if (checkIfPostRequestExists(post.getId())) {
            throw new AlreadyPostRequestExists(new ErrorMessage(MessageType.POST_REQUEST_EXISTS));
        }

        PostRequest postRequest = PostRequest.builder()
                .requestType(PostRequest.RequestType.UPDATE)
                .newTitle(request.getTitle())
                .newContent(request.getContent())
                .targetPostId(post.getId())
                .user(post.getUser())
                .status(PostRequest.RequestStatus.PENDING)
                .build();

        PostRequest savedRequest = postRequestRepository.save(postRequest);
        userActionLogService.logAction(post.getUser().getUsername(), "Post update request");

        return PostDtoResponse.builder()
                .title(savedRequest.getNewTitle())
                .content(savedRequest.getNewContent())
                .username(post.getUser().getUsername())
                .updatedDate(LocalDateTime.now().toString())
                .build();
    }


    @Override
    @PreAuthorize("@postSecurityService.canEditPost(#postId, authentication)")
    public void deletePost(UUID postId) {

        Post post = postRepository.findByUuid(postId)
                .orElseThrow(() -> new PostNotFoundException(new ErrorMessage(MessageType.POST_NOT_FOUND)));

        if (checkIfPostRequestExists(post.getId())) {
            throw new AlreadyPostRequestExists(new ErrorMessage(MessageType.POST_REQUEST_EXISTS));
        }

        PostRequest postRequest = PostRequest.builder()
                .requestType(PostRequest.RequestType.DELETE)
                .targetPostId(post.getId())
                .newTitle(post.getTitle())
                .newContent(post.getContent())
                .user(post.getUser())
                .build();

        postRequestRepository.save(postRequest);
        userActionLogService.logAction(post.getUser().getUsername(), "Post delete request");
    }

    @Override
    public PostDtoResponse getPost(UUID postId) {

        Post post = postRepository.findByUuid(postId)
                .orElseThrow(() -> new PostNotFoundException(new ErrorMessage(MessageType.POST_NOT_FOUND)));

        userActionLogService.logAction(post.getUser().getUsername(), "Post get request");
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

    private boolean checkIfPostRequestExists(Long postId) {
        return postRequestRepository.existsByTargetPostIdAndStatus(postId, PostRequest.RequestStatus.PENDING);
    }

    private PostDtoResponse convertToPostDtoResponse(Post post) {
        return PostDtoResponse.builder()
                .uuid(post.getUuid())
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
