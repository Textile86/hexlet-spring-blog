package io.hexlet.spring.controller;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import jakarta.annotation.ManagedBean;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<Post>> getAllPosts(@RequestParam(required = false) Integer limit) {
        List<Post> result = limit != null && limit > 0 ?
                posts.stream().limit(limit).toList()
                : posts;
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(posts.size()))
                .body(result);
    }

    @PostMapping
    public ResponseEntity<Post> create(@Valid @RequestBody Post post) {
        post.setId(nextId.getAndIncrement());
        post.setCreatedAt(LocalDateTime.now());
        posts.add(post);
        return ResponseEntity.status(HttpStatus.CREATED).body(post);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> show(@PathVariable Long id) {
        Optional<Post> post = posts.stream()
                .filter(p -> p.getId() != null && p.getId().equals(id))
                .findFirst();
        return ResponseEntity.of(post);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> update(@PathVariable Long id, @Valid @RequestBody Post post) {
        Optional<Post> maybePost = posts.stream()
                .filter(p -> p.getId() != null && p.getId().equals(id))
                .findFirst();
        if (maybePost.isPresent()) {
            Post findedPost = maybePost.get();
            findedPost.setTitle(post.getTitle());
            findedPost.setAuthor(post.getAuthor());
            findedPost.setContent(post.getContent());
            return ResponseEntity.ok(findedPost);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> destroy(@PathVariable Long id) {
        posts.removeIf(p -> p.getId().equals(id));
        return ResponseEntity.noContent().build();
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