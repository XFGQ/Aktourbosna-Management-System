package org.example;

import org.example.model.Tour;
import org.example.model.Vehicle;
import org.example.repository.TourRepository;
import org.example.repository.VehicleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.time.LocalDate;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    CommandLineRunner initData(TourRepository tourRepo, VehicleRepository vehicleRepo) {
        return args -> {
            // Eğer veritabanı boşsa örnek verileri ekle
            if (tourRepo.count() == 0) {
                Tour sampleTour = new Tour();
                sampleTour.setTourName("Mostar Köprüsü Gezisi");
                sampleTour.setDestination("Mostar");
                sampleTour.setStartDate(LocalDate.now().plusDays(5));
                sampleTour.setTotalPrice(1500.0);
                sampleTour.setStatus("PLANLANAN");

                tourRepo.save(sampleTour);
                System.out.println(">> Sistem Başlatıldı: Örnek tur verisi eklendi.");
            }

            if (vehicleRepo.count() == 0) {
                Vehicle sampleVehicle = new Vehicle();
                sampleVehicle.setPlateNumber("33 AKT 01");
                sampleVehicle.setModel("Mercedes Sprinter");
                sampleVehicle.setSeatCapacity(16);
                sampleVehicle.setIsAvailable(true);

                vehicleRepo.save(sampleVehicle);
                System.out.println(">> Sistem Başlatıldı: Örnek araç verisi eklendi.");
            }
        };
    }
}