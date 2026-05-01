package org.example.model
import java.util.List;
import java.util.ArrayList;

public class Route {

    private int routeId;
    private String startCity;
    private String endCity;
    private float distance;
    private boolean isPaid; 

    private List<Waypoint> waypoints = new ArrayList<>();
    private List<Toll> tolls = new ArrayList<>();

    public Route() {}

    public Route(int routeId, String startCity, String endCity,
                 float distance, boolean isPaid) {
        this.routeId = routeId;
        this.startCity = startCity;
        this.endCity = endCity;
        this.distance = distance;
        this.isPaid = isPaid;
    }

    public float calculateTotalCost() {
        float total = 0;
        for (Toll t : tolls) {
            total += t.getCostCat1();
        }
        return total;
    }

    public void addWaypoint(Waypoint waypoint) {
        this.waypoints.add(waypoint);
    }

    public void removeWaypoint(int waypointId) {
        this.waypoints.removeIf(w -> w.getWaypointId() == waypointId);
    }

    public void addToll(Toll toll) {
        this.tolls.add(toll);
    }

  
    public static class Waypoint {

        private int waypointId;
        private String city;
        private String country;
        private float additionalCost;
        private int travelTimeFromPreviousStopMin;
        private int defaultStayMinutes;
        private int defaultFreeTimeMinutes;
        private String activity;
        private String description;

        public Waypoint() {}

        public Waypoint(int waypointId, String city, String country,
                        float additionalCost,
                        int travelTimeFromPreviousStopMin,
                        int defaultStayMinutes,
                        int defaultFreeTimeMinutes,
                        String activity,
                        String description) {
            this.waypointId = waypointId;
            this.city = city;
            this.country = country;
            this.additionalCost = additionalCost;
            this.travelTimeFromPreviousStopMin = travelTimeFromPreviousStopMin;
            this.defaultStayMinutes = defaultStayMinutes;
            this.defaultFreeTimeMinutes = defaultFreeTimeMinutes;
            this.activity = activity;
            this.description = description;
        }

        public String getDetails() {
            return city + ", " + country
                    + " | Seyahat: " + travelTimeFromPreviousStopMin + " dk"
                    + " | Kalış: " + defaultStayMinutes + " dk"
                    + " | Serbest: " + defaultFreeTimeMinutes + " dk"
                    + " | Aktivite: " + activity;
        }

        public float calcExtraCost() { return additionalCost; }

        public int getWaypointId() { return waypointId; }
        public void setWaypointId(int waypointId) { this.waypointId = waypointId; }

        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }

        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }

        public float getAdditionalCost() { return additionalCost; }
        public void setAdditionalCost(float additionalCost) { this.additionalCost = additionalCost; }

        public int getTravelTimeFromPreviousStopMin() { return travelTimeFromPreviousStopMin; }
        public void setTravelTimeFromPreviousStopMin(int v) { this.travelTimeFromPreviousStopMin = v; }

        public int getDefaultStayMinutes() { return defaultStayMinutes; }
        public void setDefaultStayMinutes(int defaultStayMinutes) { this.defaultStayMinutes = defaultStayMinutes; }

        public int getDefaultFreeTimeMinutes() { return defaultFreeTimeMinutes; }
        public void setDefaultFreeTimeMinutes(int defaultFreeTimeMinutes) { this.defaultFreeTimeMinutes = defaultFreeTimeMinutes; }

        public String getActivity() { return activity; }
        public void setActivity(String activity) { this.activity = activity; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    
    public int getRouteId() { return routeId; }
    public void setRouteId(int routeId) { this.routeId = routeId; }

    public String getStartCity() { return startCity; }
    public void setStartCity(String startCity) { this.startCity = startCity; }

    public String getEndCity() { return endCity; }
    public void setEndCity(String endCity) { this.endCity = endCity; }

    public float getDistance() { return distance; }
    public void setDistance(float distance) { this.distance = distance; }

    public boolean isPaid() { return isPaid; }
    public void setPaid(boolean isPaid) { this.isPaid = isPaid; }

    public List<Waypoint> getWaypoints() { return waypoints; }
    public List<Toll> getTolls() { return tolls; }
}
