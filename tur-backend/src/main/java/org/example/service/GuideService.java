package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.application.dto.GuideDTO;
import org.example.application.dto.GuideRequest;
import org.example.application.mapper.GuideMapper;
import org.example.model.Guide;
import org.example.model.User;
import org.example.repository.GuideRepository;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GuideService {

    private final GuideRepository guideRepository;
    private final UserRepository userRepository;
    private final GuideMapper guideMapper;

    public List<GuideDTO> getAll() {
        return guideRepository.findAll().stream().map(guideMapper::toDto).toList();
    }

    public GuideDTO getById(Long id) {
        return guideMapper.toDto(findOrThrow(id));
    }

    /**
     * Flow:
     * 1. Find User by userId
     * 2. Guard: user must not already have a guide profile
     * 3. Save bare Guide entity
     * 4. Set guide_id FK on User → save User
     * 5. Set inverse pointer for immediate DTO mapping
     */
    @Transactional
    public GuideDTO create(GuideRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found: " + request.getUserId()));

        if (user.getGuide() != null) {
            throw new IllegalArgumentException(
                    "User already has a guide profile (userId: " + request.getUserId() + ")");
        }

        Guide guide = guideMapper.toEntity(request);
        guide = guideRepository.save(guide);

        user.setGuide(guide);
        userRepository.save(user);

        guide.setUser(user); // set inverse side so toDto can read user fields
        return guideMapper.toDto(guide);
    }

    @Transactional
    public GuideDTO update(Long id, GuideRequest request) {
        Guide existing = findOrThrow(id);
        guideMapper.updateEntity(request, existing);
        return guideMapper.toDto(guideRepository.save(existing));
    }

    @Transactional
    public void delete(Long id) {
        Guide guide = findOrThrow(id);
        if (guide.getUser() != null) {
            guide.getUser().setGuide(null);
            userRepository.save(guide.getUser());
        }
        guideRepository.deleteById(id);
    }

    private Guide findOrThrow(Long id) {
        return guideRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Guide not found: " + id));
    }
}
