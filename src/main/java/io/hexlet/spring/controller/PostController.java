package io.hexlet.spring.controller;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import io.hexlet.spring.exception.ResourceNotFoundException;
import io.hexlet.spring.repository.PostRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.hexlet.spring.model.Post;

@RestController
@RequestMapping("/api/posts")

public class PostController {
    @Autowired
    private PostRepository postRepository;

    @GetMapping
    public ResponseEntity<List<Post>> index(@RequestParam(required = false) Integer limit) {
        List<Post> posts = postRepository.findAll();
        if (limit != null && limit > 0) {
            posts = posts.stream().limit(limit).toList();
        }
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(posts.size()))
                .body(posts);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Post create(@Valid @RequestBody Post post) {
        return postRepository.save(post);
    }

    @GetMapping("/{id}")
    public Post show(@PathVariable Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post with id " + id + " not found"));
    }

    @PutMapping("/{id}")
    public Post update(@PathVariable Long id, @Valid @RequestBody Post postData) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post with id " + id + " not found"));

        post.setTitle(postData.getTitle());
        post.setContent(postData.getContent());
        post.setPublished(postData.isPublished());

        return postRepository.save(post);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post with id " + id + " not found"));
        postRepository.delete(post);
    }
}
