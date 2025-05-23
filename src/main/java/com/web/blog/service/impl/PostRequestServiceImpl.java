package com.web.blog.service.impl;

import com.web.blog.dto.response.PostRequestDtoResponse;
import com.web.blog.entity.Post;
import com.web.blog.entity.PostRequest;
import com.web.blog.exception.PostNotFoundException;
import com.web.blog.exception.RequestNotFoundException;
import com.web.blog.exception.RequestNotPendingException;
import com.web.blog.exception.handler.ErrorMessage;
import com.web.blog.exception.handler.MessageType;
import com.web.blog.repository.PostRepository;
import com.web.blog.repository.PostRequestRepository;
import com.web.blog.service.EmailService;
import com.web.blog.service.PostRequestService;
import com.web.blog.service.UserActionLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class PostRequestServiceImpl implements PostRequestService {

    private final PostRequestRepository postRequestRepository;
    private final PostRepository postRepository;
    private final EmailService emailService;
    private final UserActionLogService userActionLogService;


    @PreAuthorize("hasRole('ADMIN')")
    public PostRequestDtoResponse approveRequest(UUID requestUuid) {
        PostRequest postRequest = postRequestRepository.findByUuid(requestUuid);
        if (postRequest == null) {
            throw new RequestNotFoundException(new ErrorMessage(MessageType.REQUEST_NOT_FOUND));
        }

        if (postRequest.getStatus() != PostRequest.RequestStatus.PENDING) {
            throw new RequestNotPendingException(new ErrorMessage(MessageType.REQUEST_NOT_PENDING));
        }

        switch (postRequest.getRequestType()) {
            case CREATE:
                Post newPost = Post.builder()
                        .title(postRequest.getNewTitle())
                        .content(postRequest.getNewContent())
                        .user(postRequest.getUser())
                        .build();
                postRepository.save(newPost);
                userActionLogService.logAction(postRequest.getUser().getUsername(), "Post created");
                break;

            case UPDATE:
                Post existingPost = postRepository.findById(postRequest.getTargetPostId())
                                .orElseThrow(()-> new PostNotFoundException(new ErrorMessage(MessageType.POST_NOT_FOUND)));

                existingPost.setTitle(postRequest.getNewTitle());
                existingPost.setContent(postRequest.getNewContent());
                postRepository.save(existingPost);
                userActionLogService.logAction(postRequest.getUser().getUsername(), "Post updated");
                break;

            case DELETE:
                Post postToDelete = postRepository.findById(postRequest.getTargetPostId())
                        .orElseThrow(() -> new PostNotFoundException(new ErrorMessage(MessageType.POST_NOT_FOUND)));
                postRequest.setNewTitle(postToDelete.getTitle());
                postRequest.setNewContent(postToDelete.getContent());
                postRepository.delete(postToDelete);
                userActionLogService.logAction(postRequest.getUser().getUsername(), "Post deleted");
                break;
        }

        postRequest.setStatus(PostRequest.RequestStatus.APPROVED);
        postRequestRepository.save(postRequest);

        return convertToPostRequestResponseDto(postRequest);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public PostRequestDtoResponse rejectRequest(UUID requestUuid) {
        PostRequest postRequest = postRequestRepository.findByUuid(requestUuid);
        if (postRequest == null) {
            throw new RequestNotFoundException(new ErrorMessage(MessageType.REQUEST_NOT_FOUND));
        }

        if (postRequest.getStatus() != PostRequest.RequestStatus.PENDING) {
            throw new RequestNotPendingException(new ErrorMessage(MessageType.REQUEST_NOT_PENDING));
        }

        postRequest.setStatus(PostRequest.RequestStatus.REJECTED);
        postRequestRepository.save(postRequest);
        userActionLogService.logAction(postRequest.getUser().getUsername(), "Post request rejected");
        return convertToPostRequestResponseDto(postRequest);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<PostRequestDtoResponse> getAllPendingRequests() {
        List<PostRequest> postRequestList = postRequestRepository.findAll().stream()
                .filter(req -> req.getStatus() == PostRequest.RequestStatus.PENDING)
                .toList();

        return postRequestList.stream().map(this::convertToPostRequestResponseDto).toList();
    }

    private PostRequestDtoResponse convertToPostRequestResponseDto(PostRequest postRequest) {

        return PostRequestDtoResponse.builder()
                .id(postRequest.getId())
                .uuid(postRequest.getUuid())
                .requestType(postRequest.getRequestType().toString())
                .title(postRequest.getNewTitle())
                .content(postRequest.getNewContent())
                .targetPostId(postRequest.getTargetPostId())
                .email(postRequest.getUser().getEmail())
                .username(postRequest.getUser().getUsername())
                .requestStatus(postRequest.getStatus().toString())
                .createdDate(postRequest.getCreatedAt().toString())
                .build();
    }
}
