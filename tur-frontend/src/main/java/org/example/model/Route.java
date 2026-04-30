package org.example.model;

import java.util.ArrayList;
import java.util.List;

public class Route {

    private Long routeId;
    private String startCity;
    private String endCity;
    private String country;
    private Float distance;
    private Float basePrice = 0.0f;
    private List<Waypoint> waypoints = new ArrayList<>();
    private List<Toll> tolls = new ArrayList<>();
    private List<Tour> tours = new ArrayList<>();

    public Route() {}

    public Long getRouteId() { return routeId; }
    public void setRouteId(Long routeId) { this.routeId = routeId; }
    public String getStartCity() { return startCity; }
    public void setStartCity(String startCity) { this.startCity = startCity; }
    public String getEndCity() { return endCity; }
    public void setEndCity(String endCity) { this.endCity = endCity; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public Float getDistance() { return distance; }
    public void setDistance(Float distance) { this.distance = distance; }
    public Float getBasePrice() { return basePrice; }
    public void setBasePrice(Float basePrice) { this.basePrice = basePrice; }
    public List<Waypoint> getWaypoints() { return waypoints; }
    public void setWaypoints(List<Waypoint> waypoints) { this.waypoints = waypoints; }
    public List<Toll> getTolls() { return tolls; }
    public void setTolls(List<Toll> tolls) { this.tolls = tolls; }
    public List<Tour> getTours() { return tours; }
    public void setTours(List<Tour> tours) { this.tours = tours; }
}