package io.hexlet.spring.service;

import io.hexlet.spring.dto.PostCreateDTO;
import io.hexlet.spring.dto.PostDTO;
import io.hexlet.spring.dto.PostParamsDTO;
import io.hexlet.spring.dto.PostUpdateDTO;
import io.hexlet.spring.dto.PostPatchDTO;
import io.hexlet.spring.exception.ResourceNotFoundException;
import io.hexlet.spring.mapper.PostMapper;
import io.hexlet.spring.model.Post;
import io.hexlet.spring.model.Tag;
import io.hexlet.spring.model.User;
import io.hexlet.spring.repository.PostRepository;
import io.hexlet.spring.repository.TagRepository;
import io.hexlet.spring.repository.UserRepository;
import io.hexlet.spring.specification.PostSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@Service
public class PostService {
    @Autowired
    private PostSpecification postSpecification;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagRepository tagRepository;

    public Page<PostDTO> index(PostParamsDTO params, int page, int size) {
        Specification<Post> spec = postSpecification.build(params);

        Sort sort = Sort.by(Sort.Order.desc("createdAt"));
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Post> posts = postRepository.findAll(spec, pageable);

        return posts.map(postMapper::toDTO);
    }

    public PostDTO create(PostCreateDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + dto.getUserId() + " not found"));

        Post post = postMapper.toEntity(dto);
        post.setUser(user);

        if (dto.getTagIds() != null && !dto.getTagIds().isEmpty()) {
            List<Tag> tags = tagRepository.findAllById(dto.getTagIds());
            post.setTags(tags);
        }

        Post savedPost = postRepository.save(post);
        return postMapper.toDTO(savedPost);
    }

    public PostDTO show(Long id) {
        Post findedPost = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post with id " + id + " not found"));
        return postMapper.toDTO(findedPost);
    }

    public PostDTO update(Long id, PostUpdateDTO dto) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post with id " + id + " not found"));

        postMapper.updateEntity(dto, post);

        Post savedPost = postRepository.save(post);
        return postMapper.toDTO(savedPost);
    }

    public PostDTO patch(Long id, PostPatchDTO dto) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post with id " + id + " not found"));

        postMapper.updateEntityFromPatch(dto, post);
        Post savedPost = postRepository.save(post);
        return postMapper.toDTO(savedPost);
    }

    public void destroy(@PathVariable Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post with id " + id + " not found"));
        postRepository.delete(post);
    }
}
