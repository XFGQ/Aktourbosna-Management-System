package org.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.model.Route;
import org.springframework.stereotype.Repository;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
}
