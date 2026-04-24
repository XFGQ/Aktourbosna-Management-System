package org.example.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "guides")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Guide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guide_id")
    private Long id;

    @Column(name = "full_name")
    private String fullName;

    private String phone;

    @Column(name = "job_title")
    private String jobTitle;

    @Column(name = "partner_code", unique = true)
    private String partnerCode;

    @Column(name = "base_city")
    private String baseCity;

    @Column(name = "license_no")
    private String licenseNo;

    @Column(name = "daily_fee")
    private Double dailyFee;

    private Integer experience;
    private Double rating;
    private String currency;

    // Inverse side — User owns the FK
    @OneToOne(mappedBy = "guide")
    private User user;

    @ElementCollection
    @CollectionTable(name = "guide_languages", joinColumns = @JoinColumn(name = "guide_id"))
    @Column(name = "language")
    @Builder.Default
    private List<String> languages = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "guide_countries", joinColumns = @JoinColumn(name = "guide_id"))
    @Column(name = "country")
    @Builder.Default
    private List<String> countries = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "guide_skills", joinColumns = @JoinColumn(name = "guide_id"))
    @Column(name = "skill")
    @Builder.Default
    private List<String> skills = new ArrayList<>();

    @OneToMany(mappedBy = "guide", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Tour> tours = new ArrayList<>();
}
