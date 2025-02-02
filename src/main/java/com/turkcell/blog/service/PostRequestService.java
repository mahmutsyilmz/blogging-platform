package com.turkcell.blog.service;

import com.turkcell.blog.dto.request.PostDtoRequest;
import com.turkcell.blog.entity.PostRequest;
import com.turkcell.blog.entity.User;

import java.util.List;
import java.util.UUID;

public interface PostRequestService {

        PostRequest approveRequest(UUID requestUuid);
        PostRequest rejectRequest(UUID requestUuid);
        List<PostRequest> getAllPendingRequests();
}
