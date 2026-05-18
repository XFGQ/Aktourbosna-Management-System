package org.example.controller;

import jakarta.validation.Valid;
import org.example.application.dto.guide.GuideCreateDTO;
import org.example.application.dto.guide.GuideResponseDTO;
import org.example.application.dto.guide.GuideUpdateDTO;
import org.example.service.GuideService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/guides")
public class GuideController {

    private final GuideService guideService;

    public GuideController(GuideService guideService) {
        this.guideService = guideService;
    }

    @GetMapping
    public ResponseEntity<?> getAllGuides(Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) {
            return ResponseEntity.ok(guideService.getAllGuides());
        }
        return ResponseEntity.ok(guideService.getAllGuidesSummary());
    }

    @GetMapping("/me")
    public ResponseEntity<GuideResponseDTO> getMyProfile() {
        return ResponseEntity.ok(guideService.getMyProfile());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GuideResponseDTO> getGuideById(@PathVariable Long id) {
        return ResponseEntity.ok(guideService.getGuideById(id));
    }

    @PostMapping
    public ResponseEntity<GuideResponseDTO> createGuide(@Valid @RequestBody GuideCreateDTO dto) {
        return ResponseEntity.ok(guideService.createGuide(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GuideResponseDTO> updateGuide(@PathVariable Long id,
                                                        @RequestBody GuideUpdateDTO dto) {
        return ResponseEntity.ok(guideService.updateGuide(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGuide(@PathVariable Long id) {
        guideService.deleteGuide(id);
        return ResponseEntity.noContent().build();
    }
}
