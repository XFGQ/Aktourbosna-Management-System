package org.example.model;

import java.time.LocalDateTime;

public class Waypoint {

    private Long waypointId;
    private String city;
    private String country;
    private Float baseCost = 0.0f;
    private Float additionalCost = 0.0f;
    private Boolean isPaid = false;
    private boolean isDefault = true;
    private boolean isOptional = false;
    private boolean isRemoved = false;
    private LocalDateTime removedAt;
    private Route route;

    public Waypoint() {}

    public Long getWaypointId() { return waypointId; }
    public void setWaypointId(Long waypointId) { this.waypointId = waypointId; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public Float getBaseCost() { return baseCost; }
    public void setBaseCost(Float baseCost) { this.baseCost = baseCost; }
    public Float getAdditionalCost() { return additionalCost; }
    public void setAdditionalCost(Float additionalCost) { this.additionalCost = additionalCost; }
    public Boolean getIsPaid() { return isPaid; }
    public void setIsPaid(Boolean isPaid) { this.isPaid = isPaid; }
    public boolean isDefault() { return isDefault; }
    public void setDefault(boolean isDefault) { this.isDefault = isDefault; }
    public boolean isOptional() { return isOptional; }
    public void setOptional(boolean isOptional) { this.isOptional = isOptional; }
    public boolean isRemoved() { return isRemoved; }
    public void setRemoved(boolean isRemoved) { this.isRemoved = isRemoved; }
    public LocalDateTime getRemovedAt() { return removedAt; }
    public void setRemovedAt(LocalDateTime removedAt) { this.removedAt = removedAt; }
    public Route getRoute() { return route; }
    public void setRoute(Route route) { this.route = route; }
}