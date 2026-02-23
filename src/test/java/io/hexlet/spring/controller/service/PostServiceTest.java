package io.hexlet.spring.controller.service;

import io.hexlet.spring.dto.PostCreateDTO;
import io.hexlet.spring.dto.PostDTO;
import io.hexlet.spring.dto.PostUpdateDTO;
import io.hexlet.spring.dto.PostParamsDTO;
import io.hexlet.spring.exception.ResourceNotFoundException;
import io.hexlet.spring.model.Tag;
import io.hexlet.spring.model.User;
import io.hexlet.spring.repository.PostRepository;
import io.hexlet.spring.repository.TagRepository;
import io.hexlet.spring.repository.UserRepository;
import io.hexlet.spring.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private Tag testTag1;
    private Tag testTag2;

    @BeforeEach
    public void setUp() {
        postRepository.deleteAll();
        tagRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User();
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john@example.com");
        testUser.setPasswordDigest(passwordEncoder.encode("password"));
        testUser = userRepository.save(testUser);

        testTag1 = new Tag();
        testTag1.setName("Java");
        testTag1 = tagRepository.save(testTag1);

        testTag2 = new Tag();
        testTag2.setName("Spring");
        testTag2 = tagRepository.save(testTag2);
    }

    @Test
    public void testCreatePost() {
        PostCreateDTO dto = new PostCreateDTO();
        dto.setTitle("Test Post");
        dto.setContent("This is a test post");
        dto.setPublished(true);
        dto.setUserId(testUser.getId());
        dto.setTagIds(Arrays.asList(testTag1.getId(), testTag2.getId()));

        PostDTO created = postService.create(dto);

        assertThat(created).isNotNull();
        assertThat(created.getTitle()).isEqualTo("Test Post");
        assertThat(created.isPublished()).isTrue();
        assertThat(created.getTags()).hasSize(2);
    }

    @Test
    public void testCreatePostWithoutTags() {
        PostCreateDTO dto = new PostCreateDTO();
        dto.setTitle("Post without tags");
        dto.setContent("Content");
        dto.setPublished(false);
        dto.setUserId(testUser.getId());

        PostDTO created = postService.create(dto);

        assertThat(created.getTags()).isEmpty();
    }

    @Test
    public void testIndexWithPagination() {
        // Создаем несколько постов
        for (int i = 0; i < 5; i++) {
            PostCreateDTO dto = new PostCreateDTO();
            dto.setTitle("Post " + i);
            dto.setContent("Content " + i);
            dto.setPublished(true);
            dto.setUserId(testUser.getId());
            postService.create(dto);
        }

        PostParamsDTO params = new PostParamsDTO();
        Page<PostDTO> page = postService.index(params, 0, 3);

        assertThat(page.getContent()).hasSize(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
    }

    @Test
    public void testIndexWithFilters() {
        PostCreateDTO dto1 = new PostCreateDTO();
        dto1.setTitle("Java Tutorial");
        dto1.setContent("Learn Java");
        dto1.setPublished(true);
        dto1.setUserId(testUser.getId());
        dto1.setTagIds(List.of(testTag1.getId()));
        postService.create(dto1);

        PostCreateDTO dto2 = new PostCreateDTO();
        dto2.setTitle("Spring Guide");
        dto2.setContent("Learn Spring");
        dto2.setPublished(true);
        dto2.setUserId(testUser.getId());
        dto2.setTagIds(List.of(testTag2.getId()));
        postService.create(dto2);

        PostParamsDTO params = new PostParamsDTO();
        params.setTitleCont("Java");
        Page<PostDTO> page = postService.index(params, 0, 10);

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getTitle()).contains("Java");
    }

    @Test
    public void testShow() {
        PostCreateDTO dto = new PostCreateDTO();
        dto.setTitle("Show Test");
        dto.setContent("Content");
        dto.setUserId(testUser.getId());
        PostDTO created = postService.create(dto);

        PostDTO found = postService.show(created.getId());

        assertThat(found.getTitle()).isEqualTo("Show Test");
    }

    @Test
    public void testShowNotFound() {
        assertThatThrownBy(() -> postService.show(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void testUpdate() {
        PostCreateDTO createDto = new PostCreateDTO();
        createDto.setTitle("Original Title");
        createDto.setContent("Original Content");
        createDto.setUserId(testUser.getId());
        PostDTO created = postService.create(createDto);

        PostUpdateDTO updateDto = new PostUpdateDTO();
        updateDto.setTitle("Updated Title");
        updateDto.setContent("Updated Content");
        updateDto.setPublished(true);

        PostDTO updated = postService.update(created.getId(), updateDto);

        assertThat(updated.getTitle()).isEqualTo("Updated Title");
        assertThat(updated.getContent()).isEqualTo("Updated Content");
        assertThat(updated.isPublished()).isTrue();
    }

    @Test
    public void testDestroy() {
        PostCreateDTO dto = new PostCreateDTO();
        dto.setTitle("To Delete");
        dto.setContent("Content");
        dto.setUserId(testUser.getId());
        PostDTO created = postService.create(dto);

        postService.destroy(created.getId());

        assertThat(postRepository.existsById(created.getId())).isFalse();
    }
}
