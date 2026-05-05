package org.example.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TollDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long tollId;
    private String name;
    private String location;
    private Float costCat1;
    private Float costCat2;
    private Float costCat3;
}
