package org.example.model;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "guides")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
public class Guide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    private String phone;
    private String jobTitle;

    @Column(unique = true)
    private String partnerCode;

    private String baseCity;
    private String licenseNo;
    private Double dailyFee;
    private Integer experience;
    private Double rating;
    private String currency;

    @ElementCollection
    @CollectionTable(name = "guide_languages", joinColumns = @JoinColumn(name = "guide_id")) // user_id yerine guide_id yaptık
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

    @JsonIgnore
    @OneToMany(mappedBy = "guide", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Tour> tours = new ArrayList<>();
}