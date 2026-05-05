package org.example;

import org.example.model.User;
import org.example.model.UserRole;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin1.username}") private String admin1Username;
    @Value("${admin1.password}") private String admin1Password;
    @Value("${admin1.email}")    private String admin1Email;

    @Value("${admin2.username}") private String admin2Username;
    @Value("${admin2.password}") private String admin2Password;
    @Value("${admin2.email}")    private String admin2Email;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        System.out.println("DataInitializer running");
        System.out.println("localhost:8080/swagger");

        seedAdmin(admin1Username, admin1Password, admin1Email);
        seedAdmin(admin2Username, admin2Password, admin2Email);
    }

    private void seedAdmin(String username, String password, String email) {
        if (!userRepository.existsByUsername(username)) {
            userRepository.save(User.builder()
                    .username(username)
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .role(UserRole.ADMIN)
                    .build());
            System.out.println("Admin created: " + username);
        }
    }
}
