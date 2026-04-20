package org.example;

import org.example.model.Guide;
import org.example.model.Vehicle;
import org.example.repository.GuideRepository;
import org.example.repository.VehicleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final VehicleRepository vehicleRepo;
    private final GuideRepository guideRepo;

    public DataInitializer(VehicleRepository vehicleRepo, GuideRepository guideRepo) {
        this.vehicleRepo = vehicleRepo;
        this.guideRepo = guideRepo;
    }

    @Override
    public void run(String... args) throws Exception {
        if (vehicleRepo.count() == 0) {
            Vehicle v1 = new Vehicle();
            v1.setVehicleId("VW-PAS-001");
            v1.setBrand("Volkswagen");
            v1.setModel("Passat");
            v1.setPlateNumber("A26-T-626");
            v1.setSeatCapacity(5);
            v1.setAvailable(true);
            vehicleRepo.save(v1);

            Vehicle v2 = new Vehicle();
            v2.setVehicleId("FOR-TRA-003");
            v2.setBrand("Ford");
            v2.setModel("Transit");
            v2.setPlateNumber("E45-M-908");
            v2.setSeatCapacity(8);
            v2.setAvailable(true);
            vehicleRepo.save(v2);
        }

        if (guideRepo.count() == 0) {
            Guide g1 = new Guide();
            g1.setGuideId("PARTNER-001");
            g1.setFullName("Hasan Talha Akçakıl");
            g1.setRole("Owner");
            g1.setLicenseNo("123123");
            g1.setBaseCity("Sarajevo");
            g1.setDailyFee(100.0);
            g1.setCurrency("EUR");
            g1.setRating(5.0);
            g1.setEmail("info@rinnesoft.com");
            g1.setPhone("+387600000001");
            guideRepo.save(g1);

            Guide g2 = new Guide();
            g2.setGuideId("PARTNER-002");
            g2.setFullName("Ahmed Şamil Akçakıl");
            g2.setRole("Partner");
            g2.setLicenseNo("123123");
            g2.setBaseCity("Sarajevo");
            g2.setDailyFee(100.0);
            g2.setCurrency("EUR");
            g2.setRating(4.8);
            g2.setEmail("ahmedsamilakcakil@gmail.com");
            g2.setPhone("+387600000002");
            guideRepo.save(g2);
        }
    }
}