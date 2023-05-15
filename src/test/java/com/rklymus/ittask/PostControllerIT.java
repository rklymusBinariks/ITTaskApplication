package com.rklymus.ittask;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({SpringExtension.class})
@SpringBootTest
@AutoConfigureMockMvc
public class PostControllerIT {

    @MockBean
    private PostRepo postRepo;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Post post = new Post();

    @BeforeEach
    void init() {
        post.setId(1);
        post.setTitle("Title");
        post.setContent("Content");
        post.setTimestamp(LocalDateTime.of(2023, 5, 14, 0, 0));
    }

    @Test
    void getPost() throws Exception {
        when(postRepo.findById(1)).thenReturn(Optional.of(post));

        mockMvc.perform(MockMvcRequestBuilders.get("/post/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Title")))
                .andExpect(jsonPath("$.content", is("Content")))
                .andExpect(jsonPath("$.timestamp", is("2023-05-14T00:00:00")));
    }

    @Test
    public void getPost_EntityNotFound() throws Exception {
        when(postRepo.findById(1)).thenReturn(Optional.empty());

        mockMvc.perform(get("/post/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Entity with id=1 not found")));
    }


    @Test
    void createPost() throws Exception {
        PostRequest request = new PostRequest();
        request.setTitle("Title");
        request.setContent("Content");

        when(postRepo.save(any(Post.class))).thenReturn(post);

        mockMvc.perform(post("/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Title")))
                .andExpect(jsonPath("$.content", is("Content")))
                .andExpect(jsonPath("$.timestamp", is("2023-05-14T00:00:00")));
    }

    @Test
    void createPost_TitleNull() throws Exception {
        PostRequest request = new PostRequest();
        request.setTitle(null);
        request.setContent("Content");

        mockMvc.perform(post("/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createPost_ContentNull() throws Exception {
        PostRequest request = new PostRequest();
        request.setTitle("Title");
        request.setContent(null);

        mockMvc.perform(post("/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void updatePost() throws Exception {
        PostRequest request = new PostRequest();
        request.setTitle("TitleNew");
        request.setContent("ContentNew");

        post.setTitle("TitleNew");
        post.setContent("ContentNew");

        when(postRepo.findById(1)).thenReturn(Optional.of(post));
        when(postRepo.save(any(Post.class))).thenReturn(post);

        mockMvc.perform(put("/post/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("TitleNew")))
                .andExpect(jsonPath("$.content", is("ContentNew")))
                .andExpect(jsonPath("$.timestamp", is("2023-05-14T00:00:00")));
    }

    @Test
    void updatePost_TitleNull() throws Exception {
        PostRequest request = new PostRequest();
        request.setTitle(null);
        request.setContent("Content");

        mockMvc.perform(put("/post/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void updatePost_ContentNull() throws Exception {
        PostRequest request = new PostRequest();
        request.setTitle("Title");
        request.setContent(null);

        mockMvc.perform(put("/post/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void updatePost_EntityNotFound() throws Exception {
        PostRequest request = new PostRequest();
        request.setTitle("Title");
        request.setContent("Content");

        when(postRepo.findById(1)).thenReturn(Optional.empty());

        mockMvc.perform(put("/post/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Entity with id=1 not found")));
    }

    @Test
    void deletePost() throws Exception {
        post.setId(1);
        post.setTitle("Title");
        post.setContent("Content");
        when(postRepo.findById(1)).thenReturn(Optional.of(post));

        mockMvc.perform(delete("/post/1"))
                .andExpect(status().isOk());
        verify(postRepo, times(1)).delete(post);
    }

    @Test
    public void deletePost_EntityNotFound() throws Exception {
        when(postRepo.findById(1)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/post/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Entity with id=1 not found")));
    }

}
