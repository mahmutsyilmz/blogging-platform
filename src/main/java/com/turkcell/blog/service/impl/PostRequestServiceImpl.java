package com.turkcell.blog.service.impl;

import com.turkcell.blog.dto.request.PostDtoRequest;
import com.turkcell.blog.entity.Post;
import com.turkcell.blog.entity.PostRequest;
import com.turkcell.blog.entity.User;
import com.turkcell.blog.exception.PostNotFoundException;
import com.turkcell.blog.exception.RequestNotFoundException;
import com.turkcell.blog.exception.RequestNotPendingException;
import com.turkcell.blog.exception.UsernameNotFoundException;
import com.turkcell.blog.exception.handler.ErrorMessage;
import com.turkcell.blog.exception.handler.MessageType;
import com.turkcell.blog.repository.PostRepository;
import com.turkcell.blog.repository.PostRequestRepository;
import com.turkcell.blog.repository.UserRepository;
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


    @PreAuthorize("hasRole('ADMIN')")
    public PostRequest approveRequest(UUID requestUuid) {
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
                Post existingPost = postRepository.findByUuid(postRequest.getTargetPostUuid())
                                .orElseThrow(()-> new PostNotFoundException(new ErrorMessage(MessageType.POST_NOT_FOUND)));

                existingPost.setTitle(postRequest.getNewTitle());
                existingPost.setContent(postRequest.getNewContent());
                postRepository.save(existingPost);
                break;

            case DELETE:
                Post postToDelete = postRepository.findByUuid(postRequest.getTargetPostUuid())
                        .orElseThrow(() -> new PostNotFoundException(new ErrorMessage(MessageType.POST_NOT_FOUND)));
                postRepository.delete(postToDelete);
                break;
        }

        postRequest.setStatus(PostRequest.RequestStatus.APPROVED);
        postRequestRepository.save(postRequest);

        return postRequest;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public PostRequest rejectRequest(UUID requestUuid) {
        PostRequest postRequest = postRequestRepository.findByUuid(requestUuid);
        if (postRequest == null) {
            throw new RequestNotFoundException(new ErrorMessage(MessageType.REQUEST_NOT_FOUND));
        }

        if (postRequest.getStatus() != PostRequest.RequestStatus.PENDING) {
            throw new RequestNotPendingException(new ErrorMessage(MessageType.REQUEST_NOT_PENDING));
        }

        postRequest.setStatus(PostRequest.RequestStatus.REJECTED);
        postRequestRepository.save(postRequest);
        return postRequest;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<PostRequest> getAllPendingRequests() {
        return postRequestRepository.findAll().stream()
                .filter(req -> req.getStatus() == PostRequest.RequestStatus.PENDING)
                .toList();
    }
}
