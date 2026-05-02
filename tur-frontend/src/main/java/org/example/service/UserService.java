package org.example.service;

import org.example.model.User;
import org.example.model.UserRole;

public class UserService {

    public boolean isAdmin(User user) {
        return user != null && UserRole.ADMIN.equals(user.getRole());
    }

    public boolean isGuide(User user) {
        return user != null && UserRole.GUIDE.equals(user.getRole());
    }

    public String getDisplayName(User user) {
        return user != null ? user.getUsername() : "";
    }
}