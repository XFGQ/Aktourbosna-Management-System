package org.example.controller;

import org.example.application.dto.GuideDTO;
import org.example.application.dto.GuideRequest;
import org.example.service.GuideService;
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
    public List<GuideDTO> getAll() {
        return guideService.getAll();
    }

    @GetMapping("/{id}")
    public GuideDTO getById(@PathVariable Long id) {
        return guideService.getById(id);
    }

    // No POST - guide profile is auto-created when user is created with role=GUIDE
    // Only PUT to update guide details (e.g., fullName, phone, etc.)

    @PutMapping("/{id}")
    public GuideDTO update(@PathVariable Long id, @RequestBody GuideRequest request) {
        return guideService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        guideService.delete(id);
    }
}