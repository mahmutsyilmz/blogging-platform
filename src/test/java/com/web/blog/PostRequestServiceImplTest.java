package com.web.blog;




import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.web.blog.dto.response.PostRequestDtoResponse;
import com.web.blog.entity.Post;
import com.web.blog.entity.PostRequest;
import com.web.blog.entity.User;
import com.web.blog.exception.RequestNotFoundException;
import com.web.blog.exception.RequestNotPendingException;
import com.web.blog.exception.handler.MessageType;
import com.web.blog.repository.PostRepository;
import com.web.blog.repository.PostRequestRepository;
import com.web.blog.service.EmailService;
import com.web.blog.service.impl.PostRequestServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class PostRequestServiceImplTest {

    @Mock
    private PostRequestRepository postRequestRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private PostRequestServiceImpl postRequestServiceImpl;


    @Test
    public void testApproveRequest_NullRequest_ThrowsRequestNotFoundException() {
        UUID requestUuid = UUID.randomUUID();
        when(postRequestRepository.findByUuid(requestUuid)).thenReturn(null);

        RequestNotFoundException exception = assertThrows(RequestNotFoundException.class, () ->
                postRequestServiceImpl.approveRequest(requestUuid)
        );
        assertEquals(MessageType.REQUEST_NOT_FOUND.getMessage(), exception.getMessage());
    }

    @Test
    public void testApproveRequest_NotPending_ThrowsRequestNotPendingException() {
        UUID requestUuid = UUID.randomUUID();
        PostRequest pr = new PostRequest();
        pr.setUuid(UUID.randomUUID());
        pr.setStatus(PostRequest.RequestStatus.APPROVED); // not pending
        when(postRequestRepository.findByUuid(requestUuid)).thenReturn(pr);

        RequestNotPendingException exception = assertThrows(RequestNotPendingException.class, () ->
                postRequestServiceImpl.approveRequest(requestUuid)
        );
        assertEquals(MessageType.REQUEST_NOT_PENDING.getMessage(), exception.getMessage());
    }

    @Test
    public void testApproveRequest_CreateRequest_Success() {
        UUID requestUuid = UUID.randomUUID();
        PostRequest pr = new PostRequest();
        pr.setUuid(UUID.randomUUID());
        pr.setStatus(PostRequest.RequestStatus.PENDING);
        pr.setRequestType(PostRequest.RequestType.CREATE);
        pr.setNewTitle("New Post Title");
        pr.setNewContent("New Post Content");
        pr.setCreatedAt(LocalDateTime.now());

        User user = User.builder()
                .uuid(UUID.randomUUID())
                .username("testuser")
                .email("test@example.com")
                .build();
        pr.setUser(user);

        when(postRequestRepository.findByUuid(requestUuid)).thenReturn(pr);


        Post newPost = Post.builder()
                .title(pr.getNewTitle())
                .content(pr.getNewContent())
                .user(user)
                .build();
        when(postRepository.save(any(Post.class))).thenReturn(newPost);
        when(postRequestRepository.save(pr)).thenReturn(pr);

        PostRequestDtoResponse dto = postRequestServiceImpl.approveRequest(requestUuid);
        assertNotNull(dto);
        assertEquals(pr.getNewTitle(), dto.getTitle());
        assertEquals(pr.getNewContent(), dto.getContent());
        assertEquals("APPROVED", pr.getStatus().toString());
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    public void testApproveRequest_UpdateRequest_Success() {
        UUID requestUuid = UUID.randomUUID();
        PostRequest pr = new PostRequest();
        pr.setUuid(UUID.randomUUID());
        pr.setStatus(PostRequest.RequestStatus.PENDING);
        pr.setRequestType(PostRequest.RequestType.UPDATE);
        pr.setNewTitle("Updated Title");
        pr.setNewContent("Updated Content");
        pr.setCreatedAt(LocalDateTime.now());

        Long targetPostId = 123L;
        pr.setTargetPostId(targetPostId);
        User user = User.builder()
                .uuid(UUID.randomUUID())
                .username("testuser")
                .email("test@example.com")
                .build();
        pr.setUser(user);

        when(postRequestRepository.findByUuid(requestUuid)).thenReturn(pr);

        Post existingPost = Post.builder()
                .title("Old Title")
                .content("Old Content")
                .user(user)
                .build();
        when(postRepository.findById(targetPostId)).thenReturn(Optional.of(existingPost));
        when(postRepository.save(existingPost)).thenReturn(existingPost);
        when(postRequestRepository.save(pr)).thenReturn(pr);

        PostRequestDtoResponse dto = postRequestServiceImpl.approveRequest(requestUuid);
        assertNotNull(dto);

        assertEquals("Updated Title", existingPost.getTitle());
        assertEquals("Updated Content", existingPost.getContent());
        assertEquals("APPROVED", pr.getStatus().toString());
    }

    @Test
    public void testApproveRequest_DeleteRequest_Success() {
        UUID requestUuid = UUID.randomUUID();
        PostRequest pr = new PostRequest();
        pr.setUuid(UUID.randomUUID());
        pr.setStatus(PostRequest.RequestStatus.PENDING);
        pr.setRequestType(PostRequest.RequestType.DELETE);
        pr.setCreatedAt(LocalDateTime.now());

        Long targetPostId = 456L;
        pr.setTargetPostId(targetPostId);
        User user = User.builder()
                .uuid(UUID.randomUUID())
                .username("testuser")
                .email("test@example.com")
                .build();
        pr.setUser(user);

        when(postRequestRepository.findByUuid(requestUuid)).thenReturn(pr);

        Post postToDelete = Post.builder()
                .title("Delete Title")
                .content("Delete Content")
                .user(user)
                .build();
        when(postRepository.findById(targetPostId)).thenReturn(Optional.of(postToDelete));
        doNothing().when(postRepository).delete(postToDelete);
        when(postRequestRepository.save(pr)).thenReturn(pr);

        PostRequestDtoResponse dto = postRequestServiceImpl.approveRequest(requestUuid);
        assertNotNull(dto);

        assertEquals("Delete Title", dto.getTitle());
        assertEquals("Delete Content", dto.getContent());
        assertEquals("APPROVED", pr.getStatus().toString());
    }

    @Test
    public void testRejectRequest_NullRequest_ThrowsRequestNotFoundException() {
        UUID requestUuid = UUID.randomUUID();
        when(postRequestRepository.findByUuid(requestUuid)).thenReturn(null);
        assertThrows(RequestNotFoundException.class, () -> postRequestServiceImpl.rejectRequest(requestUuid));
    }

    @Test
    public void testRejectRequest_NotPending_ThrowsRequestNotPendingException() {
        UUID requestUuid = UUID.randomUUID();
        PostRequest pr = new PostRequest();
        pr.setUuid(UUID.randomUUID());
        pr.setStatus(PostRequest.RequestStatus.APPROVED);
        when(postRequestRepository.findByUuid(requestUuid)).thenReturn(pr);
        assertThrows(RequestNotPendingException.class, () -> postRequestServiceImpl.rejectRequest(requestUuid));
    }

    @Test
    public void testRejectRequest_Success() {
        UUID requestUuid = UUID.randomUUID();
        PostRequest pr = new PostRequest();
        pr.setUuid(UUID.randomUUID());
        pr.setStatus(PostRequest.RequestStatus.PENDING);
        pr.setRequestType(PostRequest.RequestType.CREATE);
        pr.setNewTitle("Some Title");
        pr.setNewContent("Some Content");
        pr.setCreatedAt(LocalDateTime.now());
        User user = User.builder()
                .uuid(UUID.randomUUID())
                .username("testuser")
                .email("test@example.com")
                .build();
        pr.setUser(user);

        when(postRequestRepository.findByUuid(requestUuid)).thenReturn(pr);
        when(postRequestRepository.save(pr)).thenReturn(pr);

        PostRequestDtoResponse dto = postRequestServiceImpl.rejectRequest(requestUuid);
        assertNotNull(dto);

        assertEquals("REJECTED", pr.getStatus().toString());
    }

    @Test
    public void testGetAllPendingRequests() {
        PostRequest pr1 = new PostRequest();
        pr1.setUuid(UUID.randomUUID());
        pr1.setStatus(PostRequest.RequestStatus.PENDING);
        pr1.setRequestType(PostRequest.RequestType.CREATE);
        pr1.setCreatedAt(LocalDateTime.now());
        pr1.setNewTitle("Title1");
        pr1.setNewContent("Content1");
        User user1 = User.builder().uuid(UUID.randomUUID()).username("user1").email("user1@example.com").build();
        pr1.setUser(user1);

        PostRequest pr2 = new PostRequest();
        pr2.setUuid(UUID.randomUUID());
        pr2.setStatus(PostRequest.RequestStatus.PENDING);
        pr2.setRequestType(PostRequest.RequestType.CREATE);
        pr2.setCreatedAt(LocalDateTime.now());
        pr2.setNewTitle("Title2");
        pr2.setNewContent("Content2");
        User user2 = User.builder().uuid(UUID.randomUUID()).username("user2").email("user2@example.com").build();
        pr2.setUser(user2);

        PostRequest pr3 = new PostRequest();
        pr3.setUuid(UUID.randomUUID());
        pr3.setStatus(PostRequest.RequestStatus.APPROVED);
        pr3.setRequestType(PostRequest.RequestType.CREATE);
        pr3.setCreatedAt(LocalDateTime.now());
        pr3.setNewTitle("Title3");
        pr3.setNewContent("Content3");
        User user3 = User.builder().uuid(UUID.randomUUID()).username("user3").email("user3@example.com").build();
        pr3.setUser(user3);

        when(postRequestRepository.findAll()).thenReturn(Arrays.asList(pr1, pr2, pr3));

        List<PostRequestDtoResponse> pendingList = postRequestServiceImpl.getAllPendingRequests();
        assertNotNull(pendingList);
        assertEquals(2, pendingList.size());
    }
}






