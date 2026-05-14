package org.example.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class Expense {
    private Long expenseId;
    private String category;
    private Float amount;
    private LocalDate date;
    private String receiptPath;
    private Long tourId;
}
