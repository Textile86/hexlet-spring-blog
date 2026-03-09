package io.hexlet.spring.service;

import io.hexlet.spring.dto.TagCreateDTO;
import io.hexlet.spring.dto.TagDTO;
import io.hexlet.spring.dto.TagUpdateDTO;
import io.hexlet.spring.exception.ResourceNotFoundException;
import io.hexlet.spring.mapper.TagMapper;
import io.hexlet.spring.model.Tag;
import io.hexlet.spring.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TagService {

    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    private static final String ACTION_TAG = "Tag with id ";
    private static final String ACTION_NOT_FOUND = " not found";

    public List<TagDTO> index() {
        List<Tag> tags = tagRepository.findAll();
        return tags.stream()
                .map(t -> tagMapper.toDTO(t))
                .toList();
    }

    public TagDTO create(TagCreateDTO dto) {
        Tag tag = tagMapper.toEntity(dto);
        Tag savedTag = tagRepository.save(tag);
        return tagMapper.toDTO(savedTag);
    }

    public TagDTO show(Long id) {
        Tag findedTag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ACTION_TAG + id + ACTION_NOT_FOUND));
        return tagMapper.toDTO(findedTag);
    }

    public TagDTO update(Long id, TagUpdateDTO dto) {
        Tag findedTag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ACTION_TAG + id + ACTION_NOT_FOUND));
        tagMapper.updateEntity(dto, findedTag);
        Tag savedTag = tagRepository.save(findedTag);
        return tagMapper.toDTO(savedTag);
    }

    public void destroy(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ACTION_TAG + id + ACTION_NOT_FOUND));

        tag.getPosts().forEach(post -> post.getTags().remove(tag));

        tagRepository.delete(tag);
    }
}
