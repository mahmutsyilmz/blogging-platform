package com.web.blog.service;

import com.web.blog.dto.response.PostRequestDtoResponse;

import java.util.List;
import java.util.UUID;

public interface PostRequestService {

        PostRequestDtoResponse approveRequest(UUID requestUuid);
        PostRequestDtoResponse rejectRequest(UUID requestUuid);
        List<PostRequestDtoResponse> getAllPendingRequests();
}
