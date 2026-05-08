package org.example.service;

import org.example.application.dto.GuideDTO;
import org.example.application.dto.GuideSummaryDTO;
import org.example.application.exception.ResourceNotFoundException;
import org.example.application.mapper.GuideMapper;
import org.example.model.Guide;
import org.example.model.User;
import org.example.model.UserRole;
import org.example.repository.GuideRepository;
import org.example.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GuideService {

    private final GuideRepository guideRepository;
    private final GuideMapper guideMapper;
    private final UserRepository userRepository;

    public GuideService(GuideRepository guideRepository, GuideMapper guideMapper, UserRepository userRepository) {
        this.guideRepository = guideRepository;
        this.guideMapper = guideMapper;
        this.userRepository = userRepository;
    }

    public List<GuideDTO> getAllGuides() {
        return guideRepository.findAll().stream()
                .map(guideMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<GuideSummaryDTO> getAllGuidesSummary() {
        return guideRepository.findAll().stream()
                .map(guideMapper::toSummaryDto)
                .collect(Collectors.toList());
    }

    public GuideDTO createGuide(GuideDTO guideDTO) {
        Guide guide = guideMapper.toEntity(guideDTO);
        User user = userRepository.findById(guideDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + guideDTO.getUserId()));
        guide.setUser(user);
        return guideMapper.toDto(guideRepository.save(guide));
    }

    public GuideDTO getGuideById(Long id) {
        Guide guide = guideRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guide not found: " + id));
        return guideMapper.toDto(guide);
    }

    public GuideDTO updateGuide(Long id, GuideDTO guideDTO) {
        Guide existingGuide = guideRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guide not found: " + id));
        checkOwnership(existingGuide);
        guideMapper.updateEntityFromDto(guideDTO, existingGuide);
        return guideMapper.toDto(guideRepository.save(existingGuide));
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
