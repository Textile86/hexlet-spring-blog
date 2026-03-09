package io.hexlet.spring.service;

import io.hexlet.spring.dto.UserCreateDTO;
import io.hexlet.spring.dto.UserDTO;
import io.hexlet.spring.dto.UserPatchDTO;
import io.hexlet.spring.dto.UserUpdateDTO;
import io.hexlet.spring.exception.ResourceNotFoundException;
import io.hexlet.spring.mapper.UserMapper;
import io.hexlet.spring.model.User;
import io.hexlet.spring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    private static final String ACTION_USER = "User with id ";
    private static final String ACTION_NOT_FOUND = " not found";

    public List<UserDTO> index() {
        List<User> users = userRepository.findAll();
        return  users.stream()
                .map(userMapper::toDTO)
                .toList();
    }

    public UserDTO create(UserCreateDTO dto) {
        User user = userMapper.toEntity(dto);

        String hashedPassword = passwordEncoder.encode(dto.getPassword());
        user.setPasswordDigest(hashedPassword);

        User savedUser = userRepository.save(user);
        return userMapper.toDTO(savedUser);
    }

    public UserDTO show(Long id) {
        User findedUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ACTION_USER + id + ACTION_NOT_FOUND));
        return userMapper.toDTO(findedUser);
    }

    public UserDTO update(Long id, UserUpdateDTO dto) {
        User findedUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ACTION_USER + id + ACTION_NOT_FOUND));
        userMapper.updateEntity(dto, findedUser);
        User savedUser = userRepository.save(findedUser);
        return userMapper.toDTO(savedUser);
    }

    public UserDTO patch(Long id, UserPatchDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ACTION_USER + id + ACTION_NOT_FOUND));

        userMapper.updateEntityFromPatch(dto, user);
        User savedUser = userRepository.save(user);
        return userMapper.toDTO(savedUser);
    }

    public void destroy(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ACTION_USER + id + ACTION_NOT_FOUND));
        userRepository.delete(user);
    }
}
