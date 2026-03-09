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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PostService {

    private final PostSpecification postSpecification;
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;

    private static final String ACTION_USER = "User with id ";
    private static final String ACTION_POST = "Post with id ";
    private static final String ACTION_NOT_FOUND = " not found";

    public Page<PostDTO> index(PostParamsDTO params, int page, int size) {
        Specification<Post> spec = postSpecification.build(params);

        Sort sort = Sort.by(Sort.Order.desc("createdAt"));
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Post> posts = postRepository.findAll(spec, pageable);

        return posts.map(postMapper::toDTO);
    }

    public PostDTO create(PostCreateDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(ACTION_USER + dto.getUserId() + ACTION_NOT_FOUND));

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
                .orElseThrow(() -> new ResourceNotFoundException(ACTION_POST + id + ACTION_NOT_FOUND));
        return postMapper.toDTO(findedPost);
    }

    public PostDTO update(Long id, PostUpdateDTO dto) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ACTION_POST + id + ACTION_NOT_FOUND));

        postMapper.updateEntity(dto, post);

        Post savedPost = postRepository.save(post);
        return postMapper.toDTO(savedPost);
    }

    public PostDTO patch(Long id, PostPatchDTO dto) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ACTION_POST + id + ACTION_NOT_FOUND));

        postMapper.updateEntityFromPatch(dto, post);
        Post savedPost = postRepository.save(post);
        return postMapper.toDTO(savedPost);
    }

    public void destroy(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ACTION_POST + id + ACTION_NOT_FOUND));
        postRepository.delete(post);
    }
}
