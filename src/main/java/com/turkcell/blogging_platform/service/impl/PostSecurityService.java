package com.turkcell.blogging_platform.service.impl;

import com.turkcell.blogging_platform.entity.Post;
import com.turkcell.blogging_platform.exception.PostNotFoundException;
import com.turkcell.blogging_platform.exception.handler.ErrorMessage;
import com.turkcell.blogging_platform.exception.handler.MessageType;
import com.turkcell.blogging_platform.repository.PostRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("postSecurityService")
// "postSecurityService" ismini, @PreAuthorize anotasyonunda @postSecurityService referansı kullanabilmek için veriyoruz.
public class PostSecurityService {

    private final PostRepository postRepository;

    public PostSecurityService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public boolean canEditPost(UUID postId, Authentication authentication) {

        String currentUsername = authentication.getName();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(new ErrorMessage(MessageType.POST_NOT_FOUND)));

        String postOwner = post.getUser().getUsername();

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        System.out.println("DEBUG :: currentUsername = " + currentUsername
                + ", postOwner = " + postOwner
                + ", isAdmin = " + isAdmin);

        //eğer post sahibi veya adminse true döner
        return (currentUsername.equals(postOwner) || isAdmin);
    }
}

