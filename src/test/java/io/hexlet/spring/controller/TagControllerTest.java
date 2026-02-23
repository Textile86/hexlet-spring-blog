package io.hexlet.spring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hexlet.spring.model.Tag;
import io.hexlet.spring.repository.TagRepository;
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
public class TagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Faker faker;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TagRepository tagRepository;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    @BeforeEach
    public void setUp() {
        tagRepository.deleteAll();
        token = jwt().jwt(jwt -> jwt.subject("test@example.com"));
    }

    @Test
    public void testIndex() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/tags"))
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public void testShow() throws Exception {
        Tag tag = createTestTag();

        MvcResult result = mockMvc.perform(get("/api/tags/" + tag.getId()))
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                v -> v.node("name").isEqualTo(tag.getName())
        );
    }

    @Test
    public void testCreate() throws Exception {
        var data = new HashMap<>();
        data.put("name", "Programming");

        MockHttpServletRequestBuilder request = post("/api/tags")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        Tag tag = tagRepository.findAll().get(0);
        assertThat(tag.getName()).isEqualTo("Programming");
    }

    @Test
    public void testDestroy() throws Exception {
        Tag tag = createTestTag();

        mockMvc.perform(delete("/api/tags/" + tag.getId())
                        .with(token))
                .andExpect(status().isNoContent());

        assertThat(tagRepository.existsById(tag.getId())).isFalse();
    }

    private Tag createTestTag() {
        Tag tag = Instancio.of(Tag.class)
                .ignore(Select.field(Tag::getId))
                .ignore(Select.field(Tag::getPosts))
                .supply(Select.field(Tag::getName), () -> faker.lorem().word())
                .create();

        return tagRepository.save(tag);
    }
}
