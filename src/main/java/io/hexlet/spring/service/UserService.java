package io.hexlet.spring.service;

import io.hexlet.spring.dto.UserCreateDTO;
import io.hexlet.spring.dto.UserDTO;
import io.hexlet.spring.dto.UserPatchDTO;
import io.hexlet.spring.dto.UserUpdateDTO;
import io.hexlet.spring.exception.ResourceNotFoundException;
import io.hexlet.spring.mapper.UserMapper;
import io.hexlet.spring.model.User;
import io.hexlet.spring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<UserDTO> index() {
        List<User> users = userRepository.findAll();
        return  users.stream()
                .map(u -> userMapper.toDTO(u))
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
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        return userMapper.toDTO(findedUser);
    }

    public UserDTO update(Long id, UserUpdateDTO dto) {
        User findedUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        userMapper.updateEntity(dto, findedUser);
        User savedUser = userRepository.save(findedUser);
        return userMapper.toDTO(savedUser);
    }

    public UserDTO patch(Long id, UserPatchDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));

        userMapper.updateEntityFromPatch(dto, user);
        User savedUser = userRepository.save(user);
        return userMapper.toDTO(savedUser);
    }

    public void destroy(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        userRepository.delete(user);
    }
}
