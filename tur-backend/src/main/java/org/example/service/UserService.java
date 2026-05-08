package org.example.service;

import org.example.application.dto.UserRegistrationDTO;
import org.example.application.dto.UserResponseDTO;
import org.example.application.exception.ResourceNotFoundException;
import org.example.application.mapper.UserMapper;
import org.example.model.Guide;
import org.example.model.User;
import org.example.model.UserRole;
import org.example.repository.GuideRepository;
import org.example.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final GuideRepository guideRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserMapper userMapper,
                       GuideRepository guideRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.guideRepository = guideRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserResponseDTO createUser(UserRegistrationDTO registrationDTO) {
        if (userRepository.existsByUsername(registrationDTO.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + registrationDTO.getUsername());
        }
        if (userRepository.existsByEmail(registrationDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + registrationDTO.getEmail());
        }
        User user = userMapper.toEntity(registrationDTO);
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        User savedUser = userRepository.save(user);
        if (savedUser.getRole() == UserRole.GUIDE) {
            guideRepository.save(Guide.builder().user(savedUser).build());
        }
        return userMapper.toResponseDto(savedUser);
    }
}
