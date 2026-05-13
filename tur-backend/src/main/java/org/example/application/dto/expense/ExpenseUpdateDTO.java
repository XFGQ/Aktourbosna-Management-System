package org.example.application.dto.expense;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ExpenseUpdateDTO {

    private String category;
    private Float amount;
    private LocalDate date;
    private String receiptPath;
}
