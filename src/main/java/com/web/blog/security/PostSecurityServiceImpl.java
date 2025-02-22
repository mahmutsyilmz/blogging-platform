package com.web.blog.security;

import com.web.blog.entity.Post;
import com.web.blog.exception.PostNotFoundException;
import com.web.blog.exception.handler.ErrorMessage;
import com.web.blog.exception.handler.MessageType;
import com.web.blog.repository.PostRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("postSecurityService")
// "postSecurityService" ismini, @PreAuthorize anotasyonunda @postSecurityService referansı kullanabilmek için veriyoruz.
public class PostSecurityServiceImpl {

    private final PostRepository postRepository;

    public PostSecurityServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public boolean canEditPost(UUID postId, Authentication authentication) {

        String currentUsername = authentication.getName();

        Post post = postRepository.findByUuid(postId)
                .orElseThrow(() -> new PostNotFoundException(new ErrorMessage(MessageType.POST_NOT_FOUND)));

        String postOwner = post.getUser().getUsername();

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        //eğer post sahibi veya adminse true döner
        return (currentUsername.equals(postOwner) || isAdmin);
    }
}

