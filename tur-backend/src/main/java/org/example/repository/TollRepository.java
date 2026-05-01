package org.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.model.Toll;
import org.springframework.stereotype.Repository;

@Repository
public interface TollRepository extends JpaRepository<Toll, Long> {
}
