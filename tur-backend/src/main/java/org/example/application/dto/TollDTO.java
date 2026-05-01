package org.example.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TollDTO {
    private Long tollId;
    private String name;
    private String location;
    private Float costCat1;
    private Float costCat2;
    private Float costCat3;
}
