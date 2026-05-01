package org.example.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WaypointDTO {
    private Long id;
    private String city;
    private String country;
    private Double extraFee;
}
