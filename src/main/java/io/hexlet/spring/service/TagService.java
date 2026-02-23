package io.hexlet.spring.service;

import io.hexlet.spring.dto.TagCreateDTO;
import io.hexlet.spring.dto.TagDTO;
import io.hexlet.spring.dto.TagUpdateDTO;
import io.hexlet.spring.exception.ResourceNotFoundException;
import io.hexlet.spring.mapper.TagMapper;
import io.hexlet.spring.model.Tag;
import io.hexlet.spring.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TagService {
    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TagMapper tagMapper;

    public List<TagDTO> index() {
        List<Tag> tags = tagRepository.findAll();
        List<TagDTO> tagDTOs = tags.stream()
                .map(t -> tagMapper.toDTO(t))
                .toList();
        return tagDTOs;
    }

    public TagDTO create(TagCreateDTO dto) {
        Tag tag = tagMapper.toEntity(dto);
        Tag savedTag = tagRepository.save(tag);
        return tagMapper.toDTO(savedTag);
    }

    public TagDTO show(Long id) {
        Tag findedTag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag with id " + id + " not found"));
        return tagMapper.toDTO(findedTag);
    }

    public TagDTO update(Long id, TagUpdateDTO dto) {
        Tag findedTag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag with id " + id + " not found"));
        tagMapper.updateEntity(dto, findedTag);
        Tag savedTag = tagRepository.save(findedTag);
        return tagMapper.toDTO(savedTag);
    }

    public void destroy(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag with id " + id + " not found"));

        tag.getPosts().forEach(post -> post.getTags().remove(tag));

        tagRepository.delete(tag);
    }
}
