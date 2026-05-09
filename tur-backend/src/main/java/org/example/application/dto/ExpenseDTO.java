package org.example.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ExpenseDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long expenseId;

    private String category;
    private Float amount;
    private LocalDate date;
    private String receiptPath;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long tourId;
}
