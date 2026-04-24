package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.application.dto.UserDTO;
import org.example.application.dto.UserRequest;
import org.example.application.mapper.UserMapper;
import org.example.model.User;
import org.example.model.UserRole;
import org.example.model.Admin;
import org.example.model.Guide;
import org.example.repository.UserRepository;
import org.example.repository.AdminRepository;
import org.example.repository.GuideRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final GuideRepository guideRepository;
    private final UserMapper userMapper;

    public List<UserDTO> getAll() {
        return userRepository.findAll().stream().map(userMapper::toDto).toList();
    }

    public UserDTO getById(Long id) {
        return userMapper.toDto(findOrThrow(id));
    }

    @Transactional
    public UserDTO create(UserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already in use: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use: " + request.getEmail());
        }
        
        User user = userMapper.toEntity(request);
        user = userRepository.save(user);

        // Auto-create profile based on role
        if (user.getRole() == UserRole.ADMIN) {
            Admin admin = new Admin();
            admin.setUser(user);
            admin = adminRepository.save(admin);
            user.setAdmin(admin);
            userRepository.save(user);
        } else if (user.getRole() == UserRole.GUIDE) {
            Guide guide = new Guide();
            guide.setUser(user);
            guide = guideRepository.save(guide);
            user.setGuide(guide);
            userRepository.save(user);
        }

        return userMapper.toDto(user);
    }

    @Transactional
    public UserDTO update(Long id, UserRequest request) {
        User existing = findOrThrow(id);

        if (!existing.getUsername().equals(request.getUsername())
                && userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already in use: " + request.getUsername());
        }
        if (!existing.getEmail().equals(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use: " + request.getEmail());
        }

        String currentPassword = existing.getPassword();
        userMapper.updateEntity(request, existing);
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            existing.setPassword(currentPassword);
        }
        return userMapper.toDto(userRepository.save(existing));
    }

    @Transactional
    public void delete(Long id) {
        findOrThrow(id);
        userRepository.deleteById(id);
    }

    private User findOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
    }
}
