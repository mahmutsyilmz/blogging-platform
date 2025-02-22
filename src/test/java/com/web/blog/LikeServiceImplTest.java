package com.web.blog;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.web.blog.dto.request.LikeDtoRequest;
import com.web.blog.dto.response.LikeDtoResponse;
import com.web.blog.entity.Like;
import com.web.blog.entity.Post;
import com.web.blog.entity.User;
import com.web.blog.exception.AlreadyLikedException;
import com.web.blog.exception.NotLikedException;
import com.web.blog.exception.handler.MessageType;
import com.web.blog.repository.LikeRepository;
import com.web.blog.repository.PostRepository;
import com.web.blog.repository.UserRepository;
import com.web.blog.service.impl.LikeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

@ExtendWith(MockitoExtension.class)
public class LikeServiceImplTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private LikeServiceImpl likeServiceImpl;


    @Test
    public void testLikePost_Success() {

        UUID userUuid = UUID.randomUUID();
        UUID postUuid = UUID.randomUUID();
        LikeDtoRequest request = new LikeDtoRequest();
        request.setPostUuid(postUuid);

        User user = User.builder()
                .uuid(userUuid)
                .username("testuser")
                .email("test@example.com")
                .build();

        Post post = Post.builder()
                .uuid(postUuid)
                .title("Post Title")
                .content("Post Content")
                .user(user)
                .build();

        when(userRepository.findByUuid(userUuid)).thenReturn(Optional.of(user));
        when(postRepository.findByUuid(postUuid)).thenReturn(Optional.of(post));
        when(likeRepository.existsByUserAndPost(user, post)).thenReturn(false);

        Like newLike = Like.builder()
                .user(user)
                .post(post)
                .createdAt(LocalDateTime.now())
                .build();

        when(likeRepository.save(any(Like.class))).thenReturn(newLike);

        LikeDtoResponse response = likeServiceImpl.likePost(request, userUuid);

        assertNotNull(response);
        assertEquals(user.getUuid(), response.getUserUuid());
        assertEquals(post.getUuid(), response.getPostUuid());
        assertEquals(user.getEmail(), response.getEmail());
        assertEquals(user.getUsername(), response.getUsername());
        assertNotNull(response.getCreatedDate());
        verify(likeRepository, times(1)).save(any(Like.class));
    }

    @Test
    public void testLikePost_AlreadyLiked_ThrowsException() {

        UUID userUuid = UUID.randomUUID();
        UUID postUuid = UUID.randomUUID();
        LikeDtoRequest request = new LikeDtoRequest();
        request.setPostUuid(postUuid);

        User user = User.builder()
                .uuid(userUuid)
                .username("testuser")
                .email("test@example.com")
                .build();

        Post post = Post.builder()
                .uuid(postUuid)
                .title("Post Title")
                .content("Post Content")
                .user(user)
                .build();

        when(userRepository.findByUuid(userUuid)).thenReturn(Optional.of(user));
        when(postRepository.findByUuid(postUuid)).thenReturn(Optional.of(post));
        when(likeRepository.existsByUserAndPost(user, post)).thenReturn(true);


        AlreadyLikedException ex = assertThrows(AlreadyLikedException.class, () ->
                likeServiceImpl.likePost(request, userUuid));
        assertEquals(MessageType.ALREADY_LIKED.getMessage(), ex.getMessage());
    }


    @Test
    public void testUnlikePost_Success() {

        UUID userUuid = UUID.randomUUID();
        UUID postUuid = UUID.randomUUID();
        LikeDtoRequest request = new LikeDtoRequest();
        request.setPostUuid(postUuid);

        User user = User.builder()
                .uuid(userUuid)
                .username("testuser")
                .email("test@example.com")
                .build();

        Post post = Post.builder()
                .uuid(postUuid)
                .title("Post Title")
                .content("Post Content")
                .user(user)
                .build();

        Like like = Like.builder()
                .user(user)
                .post(post)
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findByUuid(userUuid)).thenReturn(Optional.of(user));
        when(postRepository.findByUuid(postUuid)).thenReturn(Optional.of(post));
        when(likeRepository.findByUserAndPost(user, post)).thenReturn(Optional.of(like));

        LikeDtoResponse response = likeServiceImpl.unlikePost(request, userUuid);

        assertNotNull(response);
        assertEquals(user.getUuid(), response.getUserUuid());
        assertEquals(post.getUuid(), response.getPostUuid());
        verify(likeRepository, times(1)).delete(like);
    }

    @Test
    public void testUnlikePost_NotLiked_ThrowsException() {

        UUID userUuid = UUID.randomUUID();
        UUID postUuid = UUID.randomUUID();
        LikeDtoRequest request = new LikeDtoRequest();
        request.setPostUuid(postUuid);

        User user = User.builder()
                .uuid(userUuid)
                .username("testuser")
                .email("test@example.com")
                .build();

        Post post = Post.builder()
                .uuid(postUuid)
                .title("Post Title")
                .content("Post Content")
                .user(user)
                .build();

        when(userRepository.findByUuid(userUuid)).thenReturn(Optional.of(user));
        when(postRepository.findByUuid(postUuid)).thenReturn(Optional.of(post));
        when(likeRepository.findByUserAndPost(user, post)).thenReturn(Optional.empty());


        assertThrows(NotLikedException.class, () ->
                likeServiceImpl.unlikePost(request, userUuid));
    }


    @Test
    public void testGetLikeCountByPostId_Success() {

        UUID postUuid = UUID.randomUUID();
        when(likeRepository.countByPost_Uuid(postUuid)).thenReturn(5);


        int count = likeServiceImpl.getLikeCountByPostId(postUuid);


        assertEquals(5, count);
    }



    @Test
    public void testGetLikedUsernamesByPostId_Success() {

        UUID postUuid = UUID.randomUUID();
        User user1 = User.builder().uuid(UUID.randomUUID()).username("user1").email("user1@example.com").build();
        User user2 = User.builder().uuid(UUID.randomUUID()).username("user2").email("user2@example.com").build();

        Like like1 = Like.builder().user(user1).createdAt(LocalDateTime.now()).build();
        Like like2 = Like.builder().user(user2).createdAt(LocalDateTime.now()).build();

        List<Like> likes = Arrays.asList(like1, like2);
        when(likeRepository.findByPost_Uuid(postUuid)).thenReturn(likes);


        List<String> usernames = likeServiceImpl.getLikedUsernamesByPostId(postUuid);


        assertNotNull(usernames);
        assertEquals(2, usernames.size());
        assertTrue(usernames.containsAll(Arrays.asList("user1", "user2")));
    }
}

