package org.example.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "guides")
@PrimaryKeyJoinColumn(name = "user_id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Guide extends User {

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

    @ElementCollection
    @CollectionTable(name = "guide_languages", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "language")
    private List<String> languages = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "guide_countries", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "country")
    private List<String> countries = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "guide_skills", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "skill")
    private List<String> skills = new ArrayList<>();

    @OneToMany(mappedBy = "guide", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Tour> tours = new ArrayList<>();
}