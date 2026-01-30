package io.hexlet.spring.controller;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import io.hexlet.spring.model.Post;

@RestController
@RequestMapping("/posts")

public class PostController {
    private List<Post> posts = new ArrayList<Post>();

    private AtomicLong nextId = new AtomicLong(1);

    @GetMapping
    public List<Post> getAllPosts(@RequestParam(required = false) Integer limit) {
        if (limit != null && limit > 0) {
            return posts.stream().limit(limit).toList();
        }
        return posts;
    }

    @PostMapping
    public Post create(@Valid @RequestBody Post post) {
        post.setId(nextId.getAndIncrement());
        post.setCreatedAt(LocalDateTime.now());
        posts.add(post);
        return post;
    }

    @GetMapping("/{id}")
    public Optional<Post> show(@PathVariable Long id) {
        var post = posts.stream()
                .filter(p -> p.getId() != null && p.getId().equals(id))
                .findFirst();
        return post;
    }

    @PutMapping("/{id}") // Обновление страницы
    public Post update(@PathVariable Long id,@Valid @RequestBody Post post) {
        Optional<Post> maybePost = posts.stream()
                .filter(p -> p.getId() != null && p.getId().equals(id))
                .findFirst();
        if (maybePost.isPresent()) {
            Post findedPost = maybePost.get();
            findedPost.setTitle(post.getTitle());
            findedPost.setAuthor(post.getAuthor());
            findedPost.setContent(post.getContent());
            return findedPost;
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public void destroy(@PathVariable Long id) {
        posts.removeIf(p -> p.getId().equals(id));
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