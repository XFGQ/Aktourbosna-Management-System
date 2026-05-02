package org.example.model;

import java.util.ArrayList;
import java.util.List;

public class Route {

    private Long routeId;
    private String routeName;
    private String startCity;
    private String endCity;
    private String country;
    private Float distance;
    private Double basePrice = 0.0;
    private List<Waypoint> defaultWaypoints = new ArrayList<>();
    private List<Toll> tolls = new ArrayList<>();

    public Route() {}

    public Long getRouteId() { return routeId; }
    public void setRouteId(Long routeId) { this.routeId = routeId; }
    public String getRouteName() { return routeName; }
    public void setRouteName(String routeName) { this.routeName = routeName; }
    public String getStartCity() { return startCity; }
    public void setStartCity(String startCity) { this.startCity = startCity; }
    public String getEndCity() { return endCity; }
    public void setEndCity(String endCity) { this.endCity = endCity; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public Float getDistance() { return distance; }
    public void setDistance(Float distance) { this.distance = distance; }
    public Double getBasePrice() { return basePrice; }
    public void setBasePrice(Double basePrice) { this.basePrice = basePrice; }
    public List<Waypoint> getDefaultWaypoints() { return defaultWaypoints; }
    public void setDefaultWaypoints(List<Waypoint> defaultWaypoints) { this.defaultWaypoints = defaultWaypoints; }
    public List<Toll> getTolls() { return tolls; }
    public void setTolls(List<Toll> tolls) { this.tolls = tolls; }
}