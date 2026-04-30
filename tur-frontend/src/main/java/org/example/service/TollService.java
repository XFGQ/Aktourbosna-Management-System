package org.example.service;

import org.example.model.Route;
import org.example.model.Toll;

import java.util.List;

public class TollService {

    public List<Toll> getTollsForRoute(Route route) {
        return route.getTolls();
    }

    public Float getCostForCategory(Toll toll, int category) {
        switch (category) {
            case 1: return toll.getCostCat1();
            case 2: return toll.getCostCat2();
            case 3: return toll.getCostCat3();
            default: return toll.getCostCat1();
        }
    }

    public Double calculateTotalForCategory(List<Toll> tolls, int category) {
        return tolls.stream()
                .mapToDouble(t -> getCostForCategory(t, category))
                .sum();
    }
}