package org.example.service;

import org.example.application.dto.GuideDTO;
import org.example.application.mapper.GuideMapper;
import org.example.model.Guide;
import org.example.model.User;
import org.example.repository.GuideRepository;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GuideService {

    private final GuideRepository guideRepository;
    private final GuideMapper guideMapper;
    private final UserRepository userRepository;

    public GuideService(GuideRepository guideRepository, GuideMapper guideMapper,UserRepository userRepository) {
        this.guideRepository = guideRepository;
        this.guideMapper = guideMapper;
        this.userRepository = userRepository;
    }


    public List<GuideDTO> getAllGuides() {
        return guideRepository.findAll().stream()
                .map(guideMapper::toDto)
                .collect(Collectors.toList());
    }


    public GuideDTO getGuideById(Long id) {
        Guide guide = guideRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rehber bulunamadı. ID: " + id));
        return guideMapper.toDto(guide);
    }


    public GuideDTO createGuide(GuideDTO guideDTO) {
        Guide guide = guideMapper.toEntity(guideDTO);

        User user = userRepository.findById(guideDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        guide.setUser(user);

        Guide savedGuide = guideRepository.save(guide);
        return guideMapper.toDto(savedGuide);
    }

    public GuideDTO updateGuide(Long id, GuideDTO guideDTO) {
        Guide existingGuide = guideRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Güncellenecek rehber bulunamadı. ID: " + id));

        guideMapper.updateEntityFromDto(guideDTO, existingGuide);
        Guide updatedGuide = guideRepository.save(existingGuide);
        return guideMapper.toDto(updatedGuide);
    }


    public void deleteGuide(Long id) {
        if (!guideRepository.existsById(id)) {
            throw new RuntimeException("Silinecek rehber bulunamadı. ID: " + id);
        }
        guideRepository.deleteById(id);
    }
}