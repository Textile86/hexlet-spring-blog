package io.hexlet.spring.controller;

import java.util.*;

import io.hexlet.spring.dto.UserPatchDTO;
import io.hexlet.spring.dto.UserCreateDTO;
import io.hexlet.spring.dto.UserDTO;
import io.hexlet.spring.dto.UserUpdateDTO;
import io.hexlet.spring.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDTO>> index() {
        List<UserDTO> userDTOs = userService.index();

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(userDTOs.size()))
                .body(userDTOs);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO create(@Valid @RequestBody UserCreateDTO dto) {
        return userService.create(dto);
    }

    @GetMapping("/{id}")
    public UserDTO show(@PathVariable Long id) {
        return userService.show(id);
    }

    @PutMapping("/{id}")
    public UserDTO update(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO dto) {
        return userService.update(id, dto);
    }

    @PatchMapping("/{id}")
    public UserDTO patch(@PathVariable Long id, @Valid @RequestBody UserPatchDTO dto) {
        return userService.patch(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable Long id) {
        userService.destroy(id);
    }
}
