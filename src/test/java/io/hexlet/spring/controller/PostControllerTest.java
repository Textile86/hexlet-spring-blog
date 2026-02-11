package io.hexlet.spring.controller;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.hexlet.spring.model.Post;
import io.hexlet.spring.repository.PostRepository;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.HashMap;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private Faker faker;

    @BeforeEach
    public void setUp() {
        postRepository.deleteAll();  // Очищаем БД перед каждым тестом
    }

    @Test
    public void testIndex() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        assertThatJson(body).isObject();
        assertThatJson(body).node("content").isArray();
    }

    @Test
    public void testIndexWithPagination() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/posts?page=0&size=5"))
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        assertThatJson(body).node("size").isEqualTo(5);
        assertThatJson(body).node("number").isEqualTo(0);
    }

    @Test
    public void testShow() throws Exception {
        Post post = Instancio.of(Post.class)
                .ignore(Select.field(Post::getId))
                .ignore(Select.field(Post::getCreatedAt))
                .supply(Select.field(Post::getTitle), () -> faker.lorem().sentence(5))
                .supply(Select.field(Post::getContent), () -> faker.lorem().paragraph(3))
                .create();
        postRepository.save(post);

        MvcResult result = mockMvc.perform(get("/api/posts/" + post.getId()))
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                v -> v.node("title").isEqualTo(post.getTitle()),
                v -> v.node("content").isEqualTo(post.getContent()),
                v -> v.node("published").isEqualTo(post.isPublished())
        );
    }

    @Test
    public void testShowNotFound() throws Exception {
        mockMvc.perform(get("/api/posts/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreate() throws Exception {
        HashMap<String, Object> data = new HashMap<>();
        data.put("title", "Test Post");
        data.put("content", "This is a test post content");
        data.put("published", true);

        MockHttpServletRequestBuilder request = post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        Post post = postRepository.findAll().get(0);
        assertThat(post.getTitle()).isEqualTo("Test Post");
        assertThat(post.getContent()).isEqualTo("This is a test post content");
        assertThat(post.isPublished()).isTrue();
    }

    @Test
    public void testCreateValidationFails() throws Exception {
        HashMap<String, Object> data = new HashMap<>();
        data.put("title", "");
        data.put("content", "");

        MockHttpServletRequestBuilder request = post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void testUpdate() throws Exception {
        Post post = Instancio.of(Post.class)
                .ignore(Select.field(Post::getId))
                .ignore(Select.field(Post::getCreatedAt))
                .supply(Select.field(Post::getTitle), () -> faker.lorem().sentence(5))
                .supply(Select.field(Post::getContent), () -> faker.lorem().paragraph(3))
                .create();
        postRepository.save(post);

        HashMap<String, Object> data = new HashMap<>();
        data.put("title", "Updated Title");
        data.put("content", post.getContent());
        data.put("published", false);

        MockHttpServletRequestBuilder request = put("/api/posts/" + post.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        post = postRepository.findById(post.getId()).get();
        assertThat(post.getTitle()).isEqualTo("Updated Title");
        assertThat(post.isPublished()).isFalse();
    }

    @Test
    public void testUpdateNotFound() throws Exception {
        HashMap<String, Object> data = new HashMap<>();
        data.put("title", "Updated Title");
        data.put("content", "Content");
        data.put("published", true);

        MockHttpServletRequestBuilder request = put("/api/posts/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDestroy() throws Exception {
        var post = Instancio.of(Post.class)
                .ignore(Select.field(Post::getId))
                .ignore(Select.field(Post::getCreatedAt))
                .supply(Select.field(Post::getTitle), () -> faker.lorem().sentence(5))
                .supply(Select.field(Post::getContent), () -> faker.lorem().paragraph(3))
                .create();
        postRepository.save(post);

        mockMvc.perform(delete("/api/posts/" + post.getId()))
                .andExpect(status().isNoContent());

        assertThat(postRepository.existsById(post.getId())).isFalse();
    }

    @Test
    public void testDestroyNotFound() throws Exception {
        mockMvc.perform(delete("/api/posts/999"))
                .andExpect(status().isNotFound());
    }
}