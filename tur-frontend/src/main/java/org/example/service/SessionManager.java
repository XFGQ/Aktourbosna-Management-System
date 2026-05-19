package org.example.service;

public class SessionManager {

    private static SessionManager instance;

    private String token;
    private String username;
    private String role;
    private Long userId;
    private Long guideId;

    private SessionManager() {}

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void login(String token, String username, String role) {
        this.token = token;
        this.username = username;
        this.role = role != null ? role.replace("ROLE_", "") : "ADMIN";
    }

    public void logout() {
        this.token = null;
        this.username = null;
        this.role = null;
        this.userId = null;
        this.guideId = null;
    }

    public String getToken() { return token; }
    public String getUsername() { return username; }
    public String getRole() { return role; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getGuideId() { return guideId; }
    public void setGuideId(Long guideId) { this.guideId = guideId; }

    public boolean isAdmin() { return "ADMIN".equals(role); }
    public boolean isGuide() { return "GUIDE".equals(role); }
}