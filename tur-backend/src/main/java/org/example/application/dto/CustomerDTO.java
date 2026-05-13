package org.example.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CustomerDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private String fullName;
    private String passportNumber;
    private String phone;
    private String nationality;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long tourId;
}
