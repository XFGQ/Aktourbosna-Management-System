package org.example.controller;

import org.example.repository.GuideRepository;
import org.example.model.Guide;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/guides")
public class GuideController {

    private final GuideRepository guideRepository;

    public GuideController(GuideRepository guideRepository) {
        this.guideRepository = guideRepository;
    }

    @GetMapping
    public List<Guide> getAllGuides() {
        return guideRepository.findAll();
    }
}