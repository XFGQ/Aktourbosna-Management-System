package org.example.service;

import org.example.model.Guide;

import java.util.List;

public class GuideService {

    private final ApiService apiService = new ApiService();

    public List<Guide> getAllGuides() throws Exception {
        return apiService.fetchGuides();
    }

    public String getDisplayName(Guide guide) {
        if (guide == null) return "";
        return guide.getFullName() != null ? guide.getFullName() : guide.getUsername();
    }

    public long countGuides(List<Guide> guides) {
        return guides.size();
    }
}