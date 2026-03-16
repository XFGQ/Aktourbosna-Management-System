package org.example.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "tours")
@Data
public class Tour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tourName; // Örn: Saraybosna Kültür Turu
    private String destination;
    private LocalDate startDate;
    private Double totalPrice;
    private String status; // PLANLANAN, DEVAM_EDEN, TAMAMLANDI
}