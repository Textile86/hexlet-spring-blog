package io.hexlet.spring.controller;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import io.hexlet.spring.model.User;

@RestController
@RequestMapping("/api/users")

public class UserController {
    private List<User> users = new ArrayList<User>();

    private AtomicLong nextId = new AtomicLong(1);

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(@RequestParam(required = false) Integer limit) {
        List<User> result = limit != null && limit > 0 ?
                users.stream().limit(limit).toList()
                : users;
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(users.size()))
                .body(result);
    }

    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody User user) {
        user.setId(nextId.getAndIncrement());
        users.add(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> show(@PathVariable Long id) {
        Optional<User> user = users.stream()
                .filter(u -> u.getId() != null && u.getId().equals(id))
                .findFirst();
        return ResponseEntity.of(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @Valid @RequestBody User user) {
        Optional<User> maybeUser = users.stream()
                .filter(u -> u.getId() != null && u.getId().equals(id))
                .findFirst();
        if (maybeUser.isPresent()) {
            User findedUser = maybeUser.get();
            findedUser.setName(user.getName());
            findedUser.setEmail(user.getEmail());
            return ResponseEntity.ok(findedUser);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> destroy(@PathVariable Long id) {
        users.removeIf(u -> u.getId().equals(id));
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