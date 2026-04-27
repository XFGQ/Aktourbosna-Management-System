package org.example.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;

    @Column(name = "full_name", nullable = false, length = 200)
    private String fullName;

    @Column(name = "passport_number", length = 50, nullable = true)
    private String passportNumber;

    @Column(name = "phone", length = 30, nullable = true)
    private String phone;

    @Column(name = "nationality", length = 100, nullable = true)
    private String nationality;
}