package org.example.service;

import org.example.model.Guide;

import java.util.List;

public class GuideService {

    private final ApiService apiService = new ApiService();

    public List<Guide> getAllGuides() throws Exception {
        return apiService.fetchGuides();
    }

    public Guide getMyProfile() throws Exception {
        return apiService.getGuideMe();
    }

    public Guide updateMyProfile(Guide guide) throws Exception {
        return apiService.updateGuideMe(guide);
    }

    public Guide addGuide(Guide guide) throws Exception {
        return apiService.createGuide(guide);
    }

    public Guide updateGuide(Long id, Guide guide) throws Exception {
        return apiService.updateGuide(id, guide);
    }

    public void deleteGuide(Long id) throws Exception {
        apiService.deleteGuide(id);
    }

    public String getDisplayName(Guide guide) {
        if (guide == null) return "";
        return guide.getUsername() != null ? guide.getUsername() : "";
    }

    public long countGuides(List<Guide> guides) {
        return guides.size();
    }
}