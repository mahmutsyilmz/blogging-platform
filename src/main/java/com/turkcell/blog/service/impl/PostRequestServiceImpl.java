package com.turkcell.blog.service.impl;

import com.turkcell.blog.dto.response.PostRequestDtoResponse;
import com.turkcell.blog.entity.Post;
import com.turkcell.blog.entity.PostRequest;
import com.turkcell.blog.exception.PostNotFoundException;
import com.turkcell.blog.exception.RequestNotFoundException;
import com.turkcell.blog.exception.RequestNotPendingException;
import com.turkcell.blog.exception.handler.ErrorMessage;
import com.turkcell.blog.exception.handler.MessageType;
import com.turkcell.blog.repository.PostRepository;
import com.turkcell.blog.repository.PostRequestRepository;
import com.turkcell.blog.service.EmailService;
import com.turkcell.blog.service.PostRequestService;
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
                break;

            case UPDATE:
                Post existingPost = postRepository.findById(postRequest.getTargetPostId())
                                .orElseThrow(()-> new PostNotFoundException(new ErrorMessage(MessageType.POST_NOT_FOUND)));

                existingPost.setTitle(postRequest.getNewTitle());
                existingPost.setContent(postRequest.getNewContent());
                postRepository.save(existingPost);
                break;

            case DELETE:
                Post postToDelete = postRepository.findById(postRequest.getTargetPostId())
                        .orElseThrow(() -> new PostNotFoundException(new ErrorMessage(MessageType.POST_NOT_FOUND)));
                postRequest.setNewTitle(postToDelete.getTitle());
                postRequest.setNewContent(postToDelete.getContent());
                postRepository.delete(postToDelete);
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
