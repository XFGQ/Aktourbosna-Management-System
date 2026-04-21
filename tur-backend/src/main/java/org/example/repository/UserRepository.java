package org.example.repository;

import org.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Spring bu ismi analiz eder ve otomatik sorgu oluşturur
    User findByUsername(String username);
} 
 