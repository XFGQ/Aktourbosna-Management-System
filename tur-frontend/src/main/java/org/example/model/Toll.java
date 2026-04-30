package org.example.model;

import java.util.ArrayList;
import java.util.List;

public class Toll {

    private Long tollId;
    private String name;
    private String location;
    private Float costCat1 = 0.0f;
    private Float costCat2 = 0.0f;
    private Float costCat3 = 0.0f;
    private List<Route> routes = new ArrayList<>();

    public Toll() {}

    public Long getTollId() { return tollId; }
    public void setTollId(Long tollId) { this.tollId = tollId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public Float getCostCat1() { return costCat1; }
    public void setCostCat1(Float costCat1) { this.costCat1 = costCat1; }
    public Float getCostCat2() { return costCat2; }
    public void setCostCat2(Float costCat2) { this.costCat2 = costCat2; }
    public Float getCostCat3() { return costCat3; }
    public void setCostCat3(Float costCat3) { this.costCat3 = costCat3; }
    public List<Route> getRoutes() { return routes; }
    public void setRoutes(List<Route> routes) { this.routes = routes; }
}