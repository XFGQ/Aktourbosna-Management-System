package org.example.model;

public class TourWaypoint {

    private Long tourWaypointId;
    private Tour tour;
    private Waypoint waypoint;
    private Float appliedCost;
    private String reason;
    private boolean isOptionalOverride = false;
    private Boolean isPaid = false;

    public TourWaypoint() {}

    public Long getTourWaypointId() { return tourWaypointId; }
    public void setTourWaypointId(Long tourWaypointId) { this.tourWaypointId = tourWaypointId; }
    public Tour getTour() { return tour; }
    public void setTour(Tour tour) { this.tour = tour; }
    public Waypoint getWaypoint() { return waypoint; }
    public void setWaypoint(Waypoint waypoint) { this.waypoint = waypoint; }
    public Float getAppliedCost() { return appliedCost; }
    public void setAppliedCost(Float appliedCost) { this.appliedCost = appliedCost; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public boolean isOptionalOverride() { return isOptionalOverride; }
    public void setOptionalOverride(boolean isOptionalOverride) { this.isOptionalOverride = isOptionalOverride; }
    public Boolean getIsPaid() { return isPaid; }
    public void setIsPaid(Boolean isPaid) { this.isPaid = isPaid; }
}