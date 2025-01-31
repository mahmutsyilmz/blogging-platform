package com.turkcell.blog.security;

import com.turkcell.blog.entity.Post;
import com.turkcell.blog.exception.PostNotFoundException;
import com.turkcell.blog.exception.handler.ErrorMessage;
import com.turkcell.blog.exception.handler.MessageType;
import com.turkcell.blog.repository.PostRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service("postSecurityService")
// "postSecurityService" ismini, @PreAuthorize anotasyonunda @postSecurityService referansı kullanabilmek için veriyoruz.
public class PostSecurityServiceImpl {

    private final PostRepository postRepository;

    public PostSecurityServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public boolean canEditPost(Long postId, Authentication authentication) {

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

