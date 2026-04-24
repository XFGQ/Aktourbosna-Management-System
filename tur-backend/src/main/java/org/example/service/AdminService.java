package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.application.dto.AdminDTO;
import org.example.application.dto.AdminRequest;
import org.example.application.mapper.AdminMapper;
import org.example.model.Admin;
import org.example.model.User;
import org.example.repository.AdminRepository;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final AdminMapper adminMapper;

    public List<AdminDTO> getAll() {
        return adminRepository.findAll().stream().map(adminMapper::toDto).toList();
    }

    public AdminDTO getById(Long id) {
        return adminMapper.toDto(findOrThrow(id));
    }

    /**
     * Flow:
     * 1. Find User by userId
     * 2. Guard: user must not already have an admin profile
     * 3. Save bare Admin entity
     * 4. Set admin_id FK on User → save User
     * 5. Set inverse pointer for immediate DTO mapping
     */
    @Transactional
    public AdminDTO create(AdminRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found: " + request.getUserId()));

        if (user.getAdmin() != null) {
            throw new IllegalArgumentException(
                    "User already has an admin profile (userId: " + request.getUserId() + ")");
        }

        Admin admin = adminMapper.toEntity(request);
        admin = adminRepository.save(admin);

        user.setAdmin(admin);
        userRepository.save(user);

        admin.setUser(user); // set inverse side so toDto can read user fields
        return adminMapper.toDto(admin);
    }

    @Transactional
    public AdminDTO update(Long id, AdminRequest request) {
        Admin existing = findOrThrow(id);
        adminMapper.updateEntity(request, existing);
        return adminMapper.toDto(adminRepository.save(existing));
    }

    @Transactional
    public void delete(Long id) {
        Admin admin = findOrThrow(id);
        if (admin.getUser() != null) {
            admin.getUser().setAdmin(null);
            userRepository.save(admin.getUser());
        }
        adminRepository.deleteById(id);
    }

    private Admin findOrThrow(Long id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found: " + id));
    }
}
