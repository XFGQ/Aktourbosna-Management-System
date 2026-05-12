package org.example.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Toll {
    private Long tollId;
    private String name;
    private String location;
    private Float costCat1 = 0.0f;
    private Float costCat2 = 0.0f;
    private Float costCat3 = 0.0f;
}
