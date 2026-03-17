package org.example.model;

public class Tour {
    private String tourName;
    private String destination;
    private Double totalPrice;
    //startDate
    //status
    //id eklenecek
    //variables must be same with backend
    // empty  constructor for GSON/Jackson  needed
    public Tour() {}

     public Tour(String tourName, String route, Double price) {
        this.tourName = tourName;
        this.destination = route;
        this.totalPrice= price;
    }

    public String getTourName() { return tourName; }
    public void setTourName(String tourName) { this.tourName = tourName; }

    public String getRoute() { return destination; }
    public void setRoute(String route) { this.destination = route; }

    public Double getPrice() { return totalPrice; }
    public void setPrice(Double price) { this.totalPrice = price; }
}