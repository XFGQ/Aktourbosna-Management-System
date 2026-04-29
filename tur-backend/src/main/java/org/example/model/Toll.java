package org.example.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "tolls")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Toll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "toll_id")
    private Long tollId;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "location", length = 200)
    private String location;

    @Column(name = "cost_cat1")
    private Float costCat1 = 0.0f;

    @Column(name = "cost_cat2")
    private Float costCat2 = 0.0f;

    @Column(name = "cost_cat3")
    private Float costCat3 = 0.0f;

    @ManyToMany(mappedBy = "tolls")
    private List<Route> routes;
}