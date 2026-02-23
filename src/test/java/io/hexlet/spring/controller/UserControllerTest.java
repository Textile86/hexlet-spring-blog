package io.hexlet.spring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hexlet.spring.model.User;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Faker faker;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserRepository userRepository;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();

        token = jwt().jwt(jwt -> jwt.subject("test@example.com"));
    }

    @Test
    public void testIndex() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/users")
                        .with(token))  // 🆕 Требует токен
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public void testIndexUnauthorized() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testShow() throws Exception {
        User user = createTestUser();

        MvcResult result = mockMvc.perform(get("/api/users/" + user.getId())
                        .with(token))
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                v -> v.node("email").isEqualTo(user.getEmail()),
                v -> v.node("firstName").isEqualTo(user.getFirstName())
        );
    }

    @Test
    public void testCreate() throws Exception {
        var data = new HashMap<>();
        data.put("firstName", "John");
        data.put("lastName", "Smith");
        data.put("email", "john@example.com");
        data.put("password", "password123");

        MockHttpServletRequestBuilder request = post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        User user = userRepository.findAll().get(0);
        assertThat(user.getFirstName()).isEqualTo("John");
        assertThat(user.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    public void testDestroy() throws Exception {
        User user = createTestUser();

        mockMvc.perform(delete("/api/users/" + user.getId())
                        .with(token))
                .andExpect(status().isNoContent());

        assertThat(userRepository.existsById(user.getId())).isFalse();
    }

    private User createTestUser() {
        User user = Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .ignore(Select.field(User::getCreatedAt))
                .ignore(Select.field(User::getUpdatedAt))
                .ignore(Select.field(User::getPosts))
                .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
                .supply(Select.field(User::getFirstName), () -> faker.name().firstName())
                .supply(Select.field(User::getLastName), () -> faker.name().lastName())
                .create();

        user.setPasswordDigest("$2a$10$test");
        return userRepository.save(user);
    }
}
