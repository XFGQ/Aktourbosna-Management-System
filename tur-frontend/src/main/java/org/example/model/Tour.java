package org.example.model;

public class Tour {
    private String tourName;
    private String destination;
    private Double totalPrice;
    private String hotelName;
    //startDate
    //status
    //id eklenecek
    //variables must be same with backend
    // empty  constructor for GSON/Jackson  needed
    public Tour() {}

    public Tour(String tourName, String destination, Double totalPrice) {
        this.tourName = tourName;
        this.destination = destination;
        this.totalPrice = totalPrice;
    }

    public Tour(String tourName, String destination, Double totalPrice, String hotelName) {
        this.tourName = tourName;
        this.destination = destination;
        this.totalPrice = totalPrice;
        this.hotelName = hotelName;
    }

    public String getTourName() { return tourName; }
    public void setTourName(String tourName) { this.tourName = tourName; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }

    public String getHotelName() { return hotelName; }
    public void setHotelName(String hotelName) { this.hotelName = hotelName; }
}