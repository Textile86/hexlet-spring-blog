package io.hexlet.spring.controller;

import java.util.*;

import io.hexlet.spring.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import io.hexlet.spring.model.User;

@RestController
@RequestMapping("/api/users")

public class UserController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<User>> index() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(users.size()))
                .body(users);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid @RequestBody User user) {
        return userRepository.save(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> show(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        return ResponseEntity.of(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @Valid @RequestBody User user) {
        Optional<User> maybeUser = userRepository.findById(id);
        if (maybeUser.isPresent()) {
            User findedUser = maybeUser.get();
            findedUser.setFirstName(user.getFirstName());
            findedUser.setLastName(user.getLastName());
            findedUser.setBirthday(user.getBirthday());
            findedUser.setEmail(user.getEmail());
            userRepository.save(findedUser);
            return ResponseEntity.ok(findedUser);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable Long id) {
        userRepository.deleteById(id);
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