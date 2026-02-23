package io.hexlet.spring.controller.service;

import io.hexlet.spring.dto.UserCreateDTO;
import io.hexlet.spring.dto.UserDTO;
import io.hexlet.spring.dto.UserUpdateDTO;
import io.hexlet.spring.exception.ResourceNotFoundException;
import io.hexlet.spring.repository.UserRepository;
import io.hexlet.spring.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
    }

    @Test
    public void testCreateUser() {
        UserCreateDTO dto = new UserCreateDTO();
        dto.setFirstName("Alice");
        dto.setLastName("Smith");
        dto.setEmail("alice@example.com");
        dto.setPassword("password123");

        UserDTO created = userService.create(dto);

        assertThat(created).isNotNull();
        assertThat(created.getFirstName()).isEqualTo("Alice");
        assertThat(created.getEmail()).isEqualTo("alice@example.com");
    }

    @Test
    public void testIndex() {
        UserCreateDTO dto1 = new UserCreateDTO();
        dto1.setFirstName("User1");
        dto1.setLastName("Test");
        dto1.setEmail("user1@example.com");
        dto1.setPassword("pass");
        userService.create(dto1);

        UserCreateDTO dto2 = new UserCreateDTO();
        dto2.setFirstName("User2");
        dto2.setLastName("Test");
        dto2.setEmail("user2@example.com");
        dto2.setPassword("pass");
        userService.create(dto2);

        List<UserDTO> users = userService.index();

        assertThat(users).hasSize(2);
    }

    @Test
    public void testShow() {
        UserCreateDTO dto = new UserCreateDTO();
        dto.setFirstName("Bob");
        dto.setLastName("Johnson");
        dto.setEmail("bob@example.com");
        dto.setPassword("pass");
        UserDTO created = userService.create(dto);

        UserDTO found = userService.show(created.getId());

        assertThat(found.getFirstName()).isEqualTo("Bob");
    }

    @Test
    public void testShowNotFound() {
        assertThatThrownBy(() -> userService.show(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void testUpdate() {
        UserCreateDTO createDto = new UserCreateDTO();
        createDto.setFirstName("Charlie");
        createDto.setLastName("Brown");
        createDto.setEmail("charlie@example.com");
        createDto.setPassword("pass");
        UserDTO created = userService.create(createDto);

        UserUpdateDTO updateDto = new UserUpdateDTO();
        updateDto.setFirstName("Charles");
        updateDto.setLastName("Brown Jr");

        UserDTO updated = userService.update(created.getId(), updateDto);

        assertThat(updated.getFirstName()).isEqualTo("Charles");
        assertThat(updated.getLastName()).isEqualTo("Brown Jr");
    }

    @Test
    public void testDestroy() {
        UserCreateDTO dto = new UserCreateDTO();
        dto.setFirstName("ToDelete");
        dto.setLastName("User");
        dto.setEmail("delete@example.com");
        dto.setPassword("pass");
        UserDTO created = userService.create(dto);

        userService.destroy(created.getId());

        assertThat(userRepository.existsById(created.getId())).isFalse();
    }
}
