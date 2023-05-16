package com.rklymus.ittask;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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
@WebMvcTest(PostController.class)
public class PostControllerServiceMockIT {

    @MockBean
    private PostService service;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Post post = new Post();
    //todo: random data generators Faker or Instnasio
    @BeforeEach
    void init() {
        post.setId(1);
        post.setTitle("Title");
        post.setContent("Content");
        post.setTimestamp(LocalDateTime.of(2023, 5, 14, 0, 0));
    }

    @Test
    void getPost_Ok() throws Exception {
        when(service.get(1)).thenReturn(post);

        mockMvc.perform(MockMvcRequestBuilders.get("/post/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Title")))
                .andExpect(jsonPath("$.content", is("Content")))
                .andExpect(jsonPath("$.timestamp", is("2023-05-14T00:00:00")));
    }

    @Test
    public void getPost_EntityNotFound_404Status() throws Exception {
        EntityNotFoundException ex = new EntityNotFoundException(1);
        when(service.get(1)).thenThrow(ex);

        mockMvc.perform(get("/post/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Entity with id=1 not found")));
    }


    @Test
    void createPost_Ok() throws Exception {
        PostRequest request = new PostRequest();
        request.setTitle("Title");
        request.setContent("Content");

        when(service.create(request)).thenReturn(post);

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
    void createPost_TitleNull_403Status() throws Exception {
        PostRequest request = new PostRequest();
        request.setTitle(null);
        request.setContent("Content");

        mockMvc.perform(post("/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createPost_ContentNull_403Status() throws Exception {
        PostRequest request = new PostRequest();
        request.setTitle("Title");
        request.setContent(null);

        mockMvc.perform(post("/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void updatePost_Ok() throws Exception {
        PostRequest request = new PostRequest();
        request.setTitle("TitleNew");
        request.setContent("ContentNew");

        post.setTitle("TitleNew");
        post.setContent("ContentNew");

        when(service.update(1, request)).thenReturn(post);

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
    public void updatePost_EntityNotFound_404Status() throws Exception {
        PostRequest request = new PostRequest();
        request.setTitle("Title");
        request.setContent("Content");

        EntityNotFoundException ex = new EntityNotFoundException(1);
        when(service.update(1, request)).thenThrow(ex);

        mockMvc.perform(put("/post/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Entity with id=1 not found")));
    }

    @Test
    void updatePost_TitleNull_403Status() throws Exception {
        PostRequest request = new PostRequest();
        request.setTitle(null);
        request.setContent("Content");

        mockMvc.perform(put("/post/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void updatePost_ContentNull_403Status() throws Exception {
        PostRequest request = new PostRequest();
        request.setTitle("Title");
        request.setContent(null);

        mockMvc.perform(put("/post/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deletePost_Ok() throws Exception {
        post.setId(1);
        post.setTitle("Title");
        post.setContent("Content");

        mockMvc.perform(delete("/post/1"))
                .andExpect(status().isOk());
        verify(service, times(1)).delete(1);
    }

    @Test
    public void deletePost_EntityNotFound_404Status() throws Exception {
        EntityNotFoundException ex = new EntityNotFoundException(1);
        doThrow(ex).when(service).delete(1);
        mockMvc.perform(delete("/post/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Entity with id=1 not found")));
    }


}
