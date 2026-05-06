package org.example.service;

import org.example.model.Guide;

import java.util.List;

public class GuideService {

    private final ApiService apiService = new ApiService();

    public List<Guide> getAllGuides() throws Exception {
        return apiService.fetchGuides();
    }

    public Guide addGuide(Guide guide) throws Exception {
        return apiService.createGuide(guide);
    }

    public String getDisplayName(Guide guide) {
        if (guide == null) return "";
        return guide.getFullName() != null ? guide.getFullName() : "";
    }

    public long countGuides(List<Guide> guides) {
        return guides.size();
    }
}