package com.rklymus.ittask;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    private final Faker faker = new Faker();

    @BeforeEach
    void init() {
        post.setId(faker.number().randomDigitNotZero());
        post.setTitle(faker.pokemon().name());
        post.setContent(faker.pokemon().location());
        post.setTimestamp(LocalDateTime.of(2023, 5, 14, 0, 0));
    }

    @Test
    void getPost_Ok() throws Exception {
        when(postRepo.findById(post.getId())).thenReturn(Optional.of(post));

        mockMvc.perform(MockMvcRequestBuilders.get("/post/" + post.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(post.getId())))
                .andExpect(jsonPath("$.title", is(post.getTitle())))
                .andExpect(jsonPath("$.content", is(post.getContent())))
                .andExpect(jsonPath("$.timestamp", is("2023-05-14T00:00:00")));
    }

    @Test
    public void getPost_EntityNotFound_404Status() throws Exception {
        when(postRepo.findById(post.getId())).thenReturn(Optional.empty());

        mockMvc.perform(get("/post/" + post.getId()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Entity with id=" + post.getId() + " not found")));
    }


    @Test
    void createPost_Ok() throws Exception {
        PostRequest request = new PostRequest();
        request.setTitle(post.getTitle());
        request.setContent(post.getContent());

        when(postRepo.save(any(Post.class))).thenReturn(post);

        mockMvc.perform(post("/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(post.getId())))
                .andExpect(jsonPath("$.title", is(post.getTitle())))
                .andExpect(jsonPath("$.content", is(post.getContent())))
                .andExpect(jsonPath("$.timestamp", is("2023-05-14T00:00:00")));
    }

    @Test
    void createPost_TitleNull_403Status() throws Exception {
        PostRequest request = new PostRequest();
        request.setTitle(null);
        request.setContent(post.getContent());

        mockMvc.perform(post("/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createPost_ContentNull_403Status() throws Exception {
        PostRequest request = new PostRequest();
        request.setTitle(post.getTitle());
        request.setContent(null);

        mockMvc.perform(post("/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void updatePost_Ok() throws Exception {
        PostRequest request = new PostRequest();
        String newTitle = faker.pokemon().name();
        String newContent = faker.pokemon().location();
        request.setTitle(newTitle);
        request.setContent(newContent);

        post.setTitle(newTitle);
        post.setContent(newContent);

        when(postRepo.findById(post.getId())).thenReturn(Optional.of(post));
        when(postRepo.save(any(Post.class))).thenReturn(post);

        mockMvc.perform(put("/post/" + post.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(post.getId())))
                .andExpect(jsonPath("$.title", is(newTitle)))
                .andExpect(jsonPath("$.content", is(newContent)))
                .andExpect(jsonPath("$.timestamp", is("2023-05-14T00:00:00")));
    }

    @Test
    void updatePost_TitleNull_403Status() throws Exception {
        PostRequest request = new PostRequest();
        String newContent = faker.pokemon().location();
        request.setTitle(null);
        request.setContent(newContent);

        mockMvc.perform(put("/post/" + post.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void updatePost_ContentNull_403Status() throws Exception {
        PostRequest request = new PostRequest();
        String newTitle = faker.pokemon().name();
        request.setTitle(newTitle);
        request.setContent(null);

        mockMvc.perform(put("/post/" + post.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void updatePost_EntityNotFound_404Status() throws Exception {
        PostRequest request = new PostRequest();
        String newTitle = faker.pokemon().name();
        String newContent = faker.pokemon().location();
        request.setTitle(newTitle);
        request.setContent(newContent);

        when(postRepo.findById(post.getId())).thenReturn(Optional.empty());

        mockMvc.perform(put("/post/" + post.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Entity with id=" + post.getId() + " not found")));
    }

    @Test
    void deletePost_Ok() throws Exception {
        when(postRepo.findById(post.getId())).thenReturn(Optional.of(post));

        mockMvc.perform(delete("/post/" + post.getId()))
                .andExpect(status().isOk());
        verify(postRepo, times(1)).delete(post);
    }

    @Test
    public void deletePost_EntityNotFound_404Status() throws Exception {
        when(postRepo.findById(post.getId())).thenReturn(Optional.empty());

        mockMvc.perform(delete("/post/" + post.getId()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Entity with id=" + post.getId() + " not found")));
    }

}
