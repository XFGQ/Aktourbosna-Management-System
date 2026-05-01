package org.example.model
public class Admin extends User {

    public Admin() {}

    public Admin(int userId, String username, String email) {
        super(userId, username, email, Role.ADMIN);
    }

    @Override
    public boolean login() { return true; }

    @Override
    public void logout() {}

    @Override
    public void updateProfile() {}
}
