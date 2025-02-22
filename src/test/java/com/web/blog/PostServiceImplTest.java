package com.web.blog;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.web.blog.dto.request.PostDtoRequest;
import com.web.blog.dto.response.PostDtoResponse;
import com.web.blog.entity.Post;
import com.web.blog.entity.PostRequest;
import com.web.blog.entity.User;
import com.web.blog.exception.AlreadyPostRequestExists;
import com.web.blog.exception.PostNotFoundException;
import com.web.blog.repository.PostRepository;
import com.web.blog.repository.PostRequestRepository;
import com.web.blog.repository.UserRepository;
import com.web.blog.service.impl.PostServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRequestRepository postRequestRepository;

    @InjectMocks
    private PostServiceImpl postServiceImpl;


    @Test
    public void testCreatePost_Success() {

        PostDtoRequest request = new PostDtoRequest();
        request.setTitle("Test Title");
        request.setContent("Test Content");

        User user = User.builder()
                .uuid(UUID.randomUUID())
                .username("testuser")
                .build();

        when(postRequestRepository.save(any(PostRequest.class))).thenAnswer(invocation -> {
            PostRequest pr = invocation.getArgument(0);
            pr.setCreatedAt(LocalDateTime.now());
            return pr;
        });

        PostDtoResponse response = postServiceImpl.createPost(request, user);

        assertNotNull(response);
        assertEquals("Test Title", response.getTitle());
        assertEquals("Test Content", response.getContent());
        assertEquals("testuser", response.getUsername());
        assertNotNull(response.getCreatedDate());
    }

    @Test
    public void testUpdatePost_Success() {

        UUID postUuid = UUID.randomUUID();
        PostDtoRequest request = new PostDtoRequest();
        request.setTitle("Updated Title");
        request.setContent("Updated Content");


        User user = User.builder().uuid(UUID.randomUUID()).username("testuser").build();
        Post existingPost = Post.builder()
                .id(100L)
                .uuid(postUuid)
                .title("Old Title")
                .content("Old Content")
                .user(user)
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();

        when(postRepository.findByUuid(postUuid)).thenReturn(Optional.of(existingPost));
        when(postRequestRepository.existsByTargetPostIdAndStatus(eq(existingPost.getId()), eq(PostRequest.RequestStatus.PENDING)))
                .thenReturn(false);

        when(postRequestRepository.save(any(PostRequest.class))).thenAnswer(invocation -> {
            PostRequest pr = invocation.getArgument(0);
            pr.setCreatedAt(LocalDateTime.now());
            return pr;
        });

        PostDtoResponse response = postServiceImpl.updatePost(request, postUuid);

        assertNotNull(response);
        assertEquals("Updated Title", response.getTitle());
        assertEquals("Updated Content", response.getContent());
        assertEquals("testuser", response.getUsername());
        assertNotNull(response.getUpdatedDate());
    }

    @Test
    public void testUpdatePost_AlreadyPostRequestExists() {

        UUID postUuid = UUID.randomUUID();
        PostDtoRequest request = new PostDtoRequest();
        request.setTitle("New Title");
        request.setContent("New Content");

        User user = User.builder().uuid(UUID.randomUUID()).username("testuser").build();
        Post existingPost = Post.builder()
                .id(101L)
                .uuid(postUuid)
                .title("Old Title")
                .content("Old Content")
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();
        when(postRepository.findByUuid(postUuid)).thenReturn(Optional.of(existingPost));
        // Simulate that a pending request already exists.
        when(postRequestRepository.existsByTargetPostIdAndStatus(eq(existingPost.getId()), eq(PostRequest.RequestStatus.PENDING)))
                .thenReturn(true);

        // Act & Assert
        assertThrows(AlreadyPostRequestExists.class, () -> postServiceImpl.updatePost(request, postUuid));
    }


    @Test
    public void testDeletePost_Success() {
        // Arrange
        UUID postUuid = UUID.randomUUID();
        User user = User.builder().uuid(UUID.randomUUID()).username("testuser").build();
        Post existingPost = Post.builder()
                .id(102L)
                .uuid(postUuid)
                .title("Post to Delete")
                .content("Content")
                .user(user)
                .build();
        when(postRepository.findByUuid(postUuid)).thenReturn(Optional.of(existingPost));
        when(postRequestRepository.existsByTargetPostIdAndStatus(eq(existingPost.getId()), eq(PostRequest.RequestStatus.PENDING)))
                .thenReturn(false);
        when(postRequestRepository.save(any(PostRequest.class))).thenReturn(existingPostToPostRequest(existingPost));

        assertDoesNotThrow(() -> postServiceImpl.deletePost(postUuid));
        verify(postRequestRepository, times(1)).save(any(PostRequest.class));
    }

    @Test
    public void testDeletePost_AlreadyPostRequestExists() {

        UUID postUuid = UUID.randomUUID();
        User user = User.builder().uuid(UUID.randomUUID()).username("testuser").build();
        Post existingPost = Post.builder()
                .id(103L)
                .uuid(postUuid)
                .title("Post to Delete")
                .content("Content")
                .user(user)
                .build();
        when(postRepository.findByUuid(postUuid)).thenReturn(Optional.of(existingPost));
        when(postRequestRepository.existsByTargetPostIdAndStatus(eq(existingPost.getId()), eq(PostRequest.RequestStatus.PENDING)))
                .thenReturn(true);

        assertThrows(AlreadyPostRequestExists.class, () -> postServiceImpl.deletePost(postUuid));
    }

    @Test
    public void testGetPost_Success() {

        UUID postUuid = UUID.randomUUID();
        User user = User.builder().uuid(UUID.randomUUID()).username("testuser").build();
        LocalDateTime createdTime = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedTime = LocalDateTime.now();
        Post post = Post.builder()
                .uuid(postUuid)
                .title("Test Post")
                .content("Test Content")
                .user(user)
                .createdAt(createdTime)
                .updatedAt(updatedTime)
                .build();
        when(postRepository.findByUuid(postUuid)).thenReturn(Optional.of(post));

        PostDtoResponse response = postServiceImpl.getPost(postUuid);

        assertNotNull(response);
        assertEquals("Test Post", response.getTitle());
        assertEquals("Test Content", response.getContent());
        assertEquals("testuser", response.getUsername());
        assertEquals(createdTime.toString(), response.getCreatedDate());
        assertEquals(updatedTime.toString(), response.getUpdatedDate());
    }

    @Test
    public void testGetPost_NotFound() {

        UUID postUuid = UUID.randomUUID();
        when(postRepository.findByUuid(postUuid)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postServiceImpl.getPost(postUuid));
    }


    @Test
    public void testGetAllPosts_Success() {

        User user = User.builder().uuid(UUID.randomUUID()).username("user1").build();
        LocalDateTime now = LocalDateTime.now();
        Post post1 = Post.builder()
                .uuid(UUID.randomUUID())
                .title("Title 1")
                .content("Content 1")
                .user(user)
                .createdAt(now)
                .updatedAt(now)
                .build();
        Post post2 = Post.builder()
                .uuid(UUID.randomUUID())
                .title("Title 2")
                .content("Content 2")
                .user(user)
                .createdAt(now)
                .updatedAt(now)
                .build();
        when(postRepository.findAll()).thenReturn(Arrays.asList(post1, post2));

        List<PostDtoResponse> responses = postServiceImpl.getAllPosts();

        assertNotNull(responses);
        assertEquals(2, responses.size());
    }


    @Test
    public void testGetAllPostsByUser_Success() {

        UUID userUuid = UUID.randomUUID();
        User user = User.builder().uuid(userUuid).username("user1").build();
        LocalDateTime now = LocalDateTime.now();
        Post post1 = Post.builder()
                .uuid(UUID.randomUUID())
                .title("User Post 1")
                .content("Content 1")
                .user(user)
                .createdAt(now)
                .updatedAt(now)
                .build();
        when(userRepository.findByUuid(userUuid)).thenReturn(Optional.of(user));
        when(postRepository.findByUser(user)).thenReturn(Collections.singletonList(post1));


        List<PostDtoResponse> responses = postServiceImpl.getAllPostsByUser(userUuid);


        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("User Post 1", responses.get(0).getTitle());
    }

    @Test
    public void testGetAllPostsByTitle_Success() {

        User user = User.builder().uuid(UUID.randomUUID()).username("user1").build();
        LocalDateTime now = LocalDateTime.now();
        Post post1 = Post.builder()
                .uuid(UUID.randomUUID())
                .title("Spring Boot Testing")
                .content("Content about testing")
                .user(user)
                .createdAt(now)
                .updatedAt(now)
                .build();
        when(postRepository.findByTitleContainingIgnoreCase("Spring")).thenReturn(Collections.singletonList(post1));


        List<PostDtoResponse> responses = postServiceImpl.getAllPostsByTitle("Spring");


        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertTrue(responses.get(0).getTitle().contains("Spring Boot Testing"));
    }

    @Test
    public void testGetAllPostsByTitle_NotFound() {

        when(postRepository.findByTitleContainingIgnoreCase("Nonexistent"))
                .thenReturn(Collections.emptyList());


        assertThrows(PostNotFoundException.class, () -> postServiceImpl.getAllPostsByTitle("Nonexistent"));
    }


    private PostRequest existingPostToPostRequest(Post post) {
        PostRequest pr = PostRequest.builder()
                .requestType(PostRequest.RequestType.DELETE)
                .targetPostId(post.getId())
                .newTitle(post.getTitle())
                .newContent(post.getContent())
                .user(post.getUser())
                .build();
        pr.setCreatedAt(LocalDateTime.now());
        return pr;
    }
}

