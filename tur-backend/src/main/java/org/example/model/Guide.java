package org.example.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "guides")
@Data
public class Guide {
    @Id
    @Column(name = "guide_id")
    private String guideId;
    private String fullName;
    private String role;
    private String licenseNo;
    private String baseCity;
    private Double dailyFee;
    private String currency;
    private Double rating;
    private String email;
    private String phone;
}