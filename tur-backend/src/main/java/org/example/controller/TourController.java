package org.example.controller;

import org.example.repository.TourRepository;
import org.example.model.Tour;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tours")
public class TourController {
    private final TourRepository tourRepository;
    public TourController(TourRepository tourRepository){
        this.tourRepository=tourRepository;
    }
    @GetMapping
    public List<Tour> getAllTours(){
        return tourRepository.findAll();
    }
}
