package org.example.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, columnDefinition = "VARCHAR(20)")
    private UserRole role;

    // User owns the FK → guides table
    @OneToOne
    @JoinColumn(name = "guide_id", referencedColumnName = "guide_id")
    private Guide guide;

    // User owns the FK → admins table
    @OneToOne
    @JoinColumn(name = "admin_id", referencedColumnName = "admin_id")
    private Admin admin;
}
