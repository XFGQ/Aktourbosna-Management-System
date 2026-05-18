package org.example.service;

import org.example.application.dto.guide.GuideCreateDTO;
import org.example.application.dto.guide.GuideResponseDTO;
import org.example.application.dto.guide.GuideSummaryDTO;
import org.example.application.dto.guide.GuideUpdateDTO;
import org.example.application.exception.ResourceNotFoundException;
import org.example.application.mapper.GuideMapper;
import org.example.model.Guide;
import org.example.model.User;
import org.example.model.UserRole;
import org.example.repository.GuideRepository;
import org.example.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GuideService {

    private final GuideRepository guideRepository;
    private final GuideMapper guideMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public GuideService(GuideRepository guideRepository, GuideMapper guideMapper,
                        UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.guideRepository = guideRepository;
        this.guideMapper = guideMapper;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<GuideResponseDTO> getAllGuides() {
        return guideRepository.findAll().stream()
                .map(guideMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<GuideSummaryDTO> getAllGuidesSummary() {
        return guideRepository.findAll().stream()
                .map(guideMapper::toSummary)
                .collect(Collectors.toList());
    }

    public GuideResponseDTO getMyProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Guide guide = guideRepository.findByUser_Username(username)
                .orElseThrow(() -> new ResourceNotFoundException("Guide profile not found for user: " + username));
        return guideMapper.toResponse(guide);
    }

    public GuideResponseDTO getGuideById(Long id) {
        Guide guide = guideRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guide not found: " + id));
        return guideMapper.toResponse(guide);
    }

    @Transactional
    public GuideResponseDTO createGuide(GuideCreateDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + dto.getUsername());
        }
        User user = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(UserRole.GUIDE)
                .build();
        user = userRepository.save(user);

        Guide guide = guideMapper.toEntity(dto);
        guide.setUser(user);
        return guideMapper.toResponse(guideRepository.save(guide));
    }

    public GuideResponseDTO updateGuide(Long id, GuideUpdateDTO dto) {
        Guide existing = guideRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guide not found: " + id));
        checkOwnership(existing);
        guideMapper.updateEntityFromDto(dto, existing);
        return guideMapper.toResponse(guideRepository.save(existing));
    }

    public void deleteGuide(Long id) {
        if (!guideRepository.existsById(id)) {
            throw new ResourceNotFoundException("Guide not found: " + id);
        }
        guideRepository.deleteById(id);
    }

    private void checkOwnership(Guide guide) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        if (currentUser.getRole() == UserRole.ADMIN) return;
        if (!guide.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You can only update your own profile");
        }
    }
}
