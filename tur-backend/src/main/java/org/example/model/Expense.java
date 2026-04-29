package org.example.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "expenses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expense_id")
    private Long expenseId;

    @Column(name = "category", nullable = false, length = 100)
    private String category;

    @Column(name = "amount", nullable = false)
    private Float amount;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "receipt_path", length = 500)
    private String receiptPath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;
}