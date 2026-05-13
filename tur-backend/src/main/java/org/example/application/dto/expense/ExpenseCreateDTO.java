package org.example.application.dto.expense;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ExpenseCreateDTO {

    @NotBlank
    private String category;

    @NotNull
    private Float amount;

    private LocalDate date;
    private String receiptPath;
}
