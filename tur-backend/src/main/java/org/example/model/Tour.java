package org.example.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "tours")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@SuperBuilder
public class Tour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tour_id")
    private Long tourId;

    private String tourName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String hotelName;

    @Builder.Default
    private Double calculatedPrice = 0.0;

    @Builder.Default
    private Double finalPrice = 0.0; // Rehberin manuel müdahalesi

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guide_id", nullable = false)
    private Guide guide;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id")
    private Route baseRoute;

    @ManyToMany
    @JoinTable(
            name = "tour_extra_waypoints",
            joinColumns = @JoinColumn(name = "tour_id"),
            inverseJoinColumns = @JoinColumn(name = "waypoint_id")
    )
    @Builder.Default
    private List<Waypoint> extraWaypoints = new ArrayList<>();

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Customer> customers = new ArrayList<>();

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Expense> expenses = new ArrayList<>();
    public void addCustomer(Customer customer) {
        if (this.customers == null) {
            this.customers = new ArrayList<>();
        }
        customer.setTour(this);
        this.customers.add(customer);
    }

    public void addExpense(Expense expense) {
        if (this.expenses == null) {
            this.expenses = new ArrayList<>();
        }
        expense.setTour(this);
        this.expenses.add(expense);
    }
}