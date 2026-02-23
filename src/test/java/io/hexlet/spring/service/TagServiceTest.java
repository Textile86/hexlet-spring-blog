package io.hexlet.spring.service;

import io.hexlet.spring.dto.TagCreateDTO;
import io.hexlet.spring.dto.TagDTO;
import io.hexlet.spring.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class TagServiceTest {

    @Autowired
    private TagService tagService;

    @Autowired
    private TagRepository tagRepository;

    @BeforeEach
    public void setUp() {
        tagRepository.deleteAll();
    }

    @Test
    public void testCreateTag() {
        TagCreateDTO dto = new TagCreateDTO();
        dto.setName("Java");

        TagDTO created = tagService.create(dto);

        assertThat(created).isNotNull();
        assertThat(created.getName()).isEqualTo("Java");
        assertThat(created.getId()).isNotNull();
    }

    @Test
    public void testGetAllTags() {
        TagCreateDTO dto1 = new TagCreateDTO();
        dto1.setName("Java");
        tagService.create(dto1);

        TagCreateDTO dto2 = new TagCreateDTO();
        dto2.setName("Spring");
        tagService.create(dto2);

        List<TagDTO> tags = tagService.index();

        assertThat(tags).hasSize(2);
        assertThat(tags).extracting(TagDTO::getName).containsExactlyInAnyOrder("Java", "Spring");
    }

    @Test
    public void testShowTag() {
        TagCreateDTO dto = new TagCreateDTO();
        dto.setName("Docker");
        TagDTO created = tagService.create(dto);

        TagDTO found = tagService.show(created.getId());

        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Docker");
    }

    @Test
    public void testDeleteTag() {
        TagCreateDTO dto = new TagCreateDTO();
        dto.setName("Testing");
        TagDTO created = tagService.create(dto);

        tagService.destroy(created.getId());

        assertThat(tagRepository.existsById(created.getId())).isFalse();
    }
}
