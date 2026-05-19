package org.example.service;

import org.example.model.Guide;

import java.util.List;

public class GuideService {

    private final ApiService apiService = new ApiService();

    // --- Static cache shared across all controller instances ---
    private static volatile List<Guide> cachedGuides = null;
    private static volatile long cacheTs = 0;
    private static final long TTL_MS = 300_000;
    private static final Object LOCK = new Object();

    public List<Guide> getAllGuides() throws Exception {
        if (cachedGuides != null && System.currentTimeMillis() - cacheTs < TTL_MS) {
            return cachedGuides;
        }
        synchronized (LOCK) {
            if (cachedGuides != null && System.currentTimeMillis() - cacheTs < TTL_MS) {
                return cachedGuides;
            }
            List<Guide> fresh = apiService.fetchGuides();
            cachedGuides = fresh;
            cacheTs = System.currentTimeMillis();
            return fresh;
        }
    }

    public static void invalidateCache() {
        cachedGuides = null;
    }

    public Guide getMyProfile() throws Exception {
        return apiService.getGuideMe();
    }

    public Guide updateMyProfile(Guide guide) throws Exception {
        return apiService.updateGuideMe(guide);
    }

    public Guide addGuide(Guide guide) throws Exception {
        Guide result = apiService.createGuide(guide);
        invalidateCache();
        return result;
    }

    public Guide updateGuide(Long id, Guide guide) throws Exception {
        Guide result = apiService.updateGuide(id, guide);
        invalidateCache();
        return result;
    }

    public void deleteGuide(Long id) throws Exception {
        apiService.deleteGuide(id);
        invalidateCache();
    }

    public String getDisplayName(Guide guide) {
        if (guide == null) return "";
        return guide.getUsername() != null ? guide.getUsername() : "";
    }

    public long countGuides(List<Guide> guides) {
        return guides.size();
    }

    public Guide getMe() throws Exception {
        java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
        java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create("http://localhost:8080/api/guides/me"))
                .header("Authorization", "Bearer " + SessionManager.getInstance().getToken())
                .GET()
                .build();
        java.net.http.HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            String body = response.body();
            Guide guide = new Guide();
            String searchId = "\"id\":";
            int idIndex = body.indexOf(searchId);
            if (idIndex != -1) {
                int start = idIndex + searchId.length();
                int end = body.indexOf(",", start);
                if (end == -1) end = body.indexOf("}", start);
                guide.setId(Long.parseLong(body.substring(start, end).trim()));
            }
            return guide;
        }
        return null;
    }
}
