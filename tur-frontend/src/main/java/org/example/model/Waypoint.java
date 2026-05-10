package org.example.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Waypoint {
    private Long id;
    private String city;
    private String country;
    private Double extraFee = 0.0;
}
