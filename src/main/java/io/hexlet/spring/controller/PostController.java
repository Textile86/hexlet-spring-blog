package io.hexlet.spring.controller;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import io.hexlet.spring.repository.PostRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import io.hexlet.spring.model.Post;

@RestController
@RequestMapping("/api/posts")

public class PostController {
    @Autowired
    private PostRepository postRepository;

    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts(@RequestParam(required = false) Integer limit) {
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
    public ResponseEntity<Post> show(@PathVariable Long id) {
        Optional<Post> post = postRepository.findById(id);
        return ResponseEntity.of(post);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> update(@PathVariable Long id, @Valid @RequestBody Post post) {
        Optional<Post> maybePost = postRepository.findById(id);
        if (maybePost.isPresent()) {
            Post findedPost = maybePost.get();
            findedPost.setTitle(post.getTitle());
            findedPost.setContent(post.getContent());
            findedPost.setPublished(post.isPublished());
            postRepository.save(findedPost);
            return ResponseEntity.ok(findedPost);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable Long id) {
        postRepository.deleteById(id);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}