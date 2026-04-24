package org.example;

import org.example.model.*;
import org.example.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Seeds initial data on first startup.
 *
 * Flow for guide / admin:
 *   1. Create User (auth account)
 *   2. Create Guide / Admin profile
 *   3. Link them: user.setGuide(profile) → save user
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository    userRepo;
    private final GuideRepository   guideRepo;
    private final AdminRepository   adminRepo;
    private final VehicleRepository vehicleRepo;

    public DataInitializer(UserRepository userRepo,
                           GuideRepository guideRepo,
                           AdminRepository adminRepo,
                           VehicleRepository vehicleRepo) {
        this.userRepo    = userRepo;
        this.guideRepo   = guideRepo;
        this.adminRepo   = adminRepo;
        this.vehicleRepo = vehicleRepo;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // seedVehicles(); // temporarily disabled
        seedAdmins();
        seedGuides();
    }

    // ── Vehicles ─────────────────────────────────────────────────────────────

    private void seedVehicles() {
        if (vehicleRepo.count() > 0) return;

        Vehicle v1 = new Vehicle();
        v1.setVehicleId("VW-PAS-001");
        v1.setBrand("Volkswagen");
        v1.setModel("Passat");
        v1.setPlateNumber("A26-T-626");
        v1.setSeatCapacity(5);
        v1.setIsAvailable(true);
        vehicleRepo.save(v1);

        Vehicle v2 = new Vehicle();
        v2.setVehicleId("FOR-TRA-003");
        v2.setBrand("Ford");
        v2.setModel("Transit");
        v2.setPlateNumber("E45-M-908");
        v2.setSeatCapacity(8);
        v2.setIsAvailable(true);
        vehicleRepo.save(v2);
    }

    // ── Admin ─────────────────────────────────────────────────────────────────

    private void seedAdmins() {
        if (adminRepo.count() > 0) return;

        // Step 1 — auth account
        User u = new User();
        u.setUsername("admin");
        u.setEmail("admin@aktourbosna.com");
        u.setPassword("admin123");
        u.setRole(UserRole.ADMIN);
        u = userRepo.save(u);

        // Step 2 — profile
        Admin profile = new Admin();
        profile.setDepartment("Management");
        profile = adminRepo.save(profile);

        // Step 3 — link
        u.setAdmin(profile);
        userRepo.save(u);
        profile.setUser(u);  // set inverse side so mapper can read user fields
    }

    // ── Guides ────────────────────────────────────────────────────────────────

    private void seedGuides() {
        if (guideRepo.count() > 0) return;

        // ── Guide 1
        User u1 = new User();
        u1.setUsername("hasan.akcakil");
        u1.setEmail("info@rinnesoft.com");
        u1.setPassword("changeme");
        u1.setRole(UserRole.GUIDE);
        u1 = userRepo.save(u1);

        Guide g1 = new Guide();
        g1.setFullName("Hasan Talha Akçakıl");
        g1.setPartnerCode("PARTNER-001");
        g1.setLicenseNo("123123");
        g1.setBaseCity("Sarajevo");
        g1.setDailyFee(100.0);
        g1.setCurrency("EUR");
        g1.setRating(5.0);
        g1.setPhone("+387600000001");
        g1.setLanguages(List.of("Turkish", "Bosnian", "English"));
        g1 = guideRepo.save(g1);

        u1.setGuide(g1);
        userRepo.save(u1);
        g1.setUser(u1);  // set inverse side so mapper can read user fields

        // ── Guide 2
        User u2 = new User();
        u2.setUsername("ahmed.akcakil");
        u2.setEmail("ahmedsamilakcakil@gmail.com");
        u2.setPassword("changeme");
        u2.setRole(UserRole.GUIDE);
        u2 = userRepo.save(u2);

        Guide g2 = new Guide();
        g2.setFullName("Ahmed Şamil Akçakıl");
        g2.setPartnerCode("PARTNER-002");
        g2.setLicenseNo("123124");
        g2.setBaseCity("Sarajevo");
        g2.setDailyFee(100.0);
        g2.setCurrency("EUR");
        g2.setRating(4.8);
        g2.setPhone("+387600000002");
        g2.setLanguages(List.of("Turkish", "Bosnian"));
        g2 = guideRepo.save(g2);

        u2.setGuide(g2);
        userRepo.save(u2);
        g2.setUser(u2);  // set inverse side so mapper can read user fields
    }
}
