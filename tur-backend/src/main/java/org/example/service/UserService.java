package org.example.service;

import org.example.application.dto.UserRegistrationDTO;
import org.example.application.dto.UserResponseDTO;
import org.example.application.mapper.UserMapper;
import org.example.model.Guide;
import org.example.model.User;
import org.example.repository.GuideRepository;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final GuideRepository guideRepository;

    public UserService(UserRepository userRepository, UserMapper userMapper, GuideRepository guideRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.guideRepository = guideRepository;
    }

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public UserResponseDTO createUser(UserRegistrationDTO registrationDTO) {
        User user = userMapper.toEntity(registrationDTO);
        User savedUser = userRepository.save(user);
            Guide newGuide = Guide.builder()
                    .user(savedUser)
                    .build();
            guideRepository.save(newGuide);
        return userMapper.toResponseDto(savedUser);
    }
}