package io.hexlet.spring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hexlet.spring.model.Post;
import io.hexlet.spring.model.User;
import io.hexlet.spring.repository.PostRepository;
import io.hexlet.spring.repository.UserRepository;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private Faker faker;

    private User testUser;
    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    @BeforeEach
    public void setUp() {
        postRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User();
        testUser.setFirstName(faker.name().firstName());
        testUser.setLastName(faker.name().lastName());
        testUser.setEmail(faker.internet().emailAddress());
        testUser.setPasswordDigest("$2a$10$test");
        userRepository.save(testUser);

        token = jwt().jwt(jwt -> jwt.subject(testUser.getEmail()));
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
    public void testShow() throws Exception {
        Post post = createTestPost();

        MvcResult result = mockMvc.perform(get("/api/posts/" + post.getId()))
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                v -> v.node("title").isEqualTo(post.getTitle()),
                v -> v.node("content").isEqualTo(post.getContent()),
                v -> v.node("userId").isEqualTo(testUser.getId())
        );
    }

    @Test
    public void testCreate() throws Exception {
        var data = new HashMap<>();
        data.put("title", "Test Post");
        data.put("content", "This is a test post content");
        data.put("published", true);
        data.put("userId", testUser.getId());

        MockHttpServletRequestBuilder request = post("/api/posts")
                .with(token)
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
    public void testCreateUnauthorized() throws Exception {
        var data = new HashMap<>();
        data.put("title", "Test Post");
        data.put("content", "Content");
        data.put("userId", testUser.getId());

        MockHttpServletRequestBuilder request = post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testUpdate() throws Exception {
        Post post = createTestPost();

        var data = new HashMap<>();
        data.put("title", "Updated Title");
        data.put("content", post.getContent());
        data.put("published", false);

        MockHttpServletRequestBuilder request = put("/api/posts/" + post.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        post = postRepository.findById(post.getId()).get();
        assertThat(post.getTitle()).isEqualTo("Updated Title");
        assertThat(post.isPublished()).isFalse();
    }

    @Test
    public void testDestroy() throws Exception {
        Post post = createTestPost();

        mockMvc.perform(delete("/api/posts/" + post.getId())
                        .with(token))
                .andExpect(status().isNoContent());

        assertThat(postRepository.existsById(post.getId())).isFalse();
    }

    // Вспомогательный метод
    private Post createTestPost() {
        Post post = Instancio.of(Post.class)
                .ignore(Select.field(Post::getId))
                .ignore(Select.field(Post::getCreatedAt))
                .ignore(Select.field(Post::getUpdatedAt))
                .ignore(Select.field(Post::getUser))
                .ignore(Select.field(Post::getTags))
                .supply(Select.field(Post::getTitle), () -> faker.lorem().sentence(3))
                .supply(Select.field(Post::getContent), () -> faker.lorem().paragraph(2))
                .create();

        post.setUser(testUser);
        return postRepository.save(post);
    }
}
