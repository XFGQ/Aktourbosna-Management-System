package org.example.model;

public class Tour {
    private String tourName;
    private String destination;
    private Double totalPrice;
    private String hotelName;
    private String startDate;
    private String status;
    private String groupSize;
    private String vehicle;
    private String departureCity;
    private String guideName;
    //id eklenecek
    //variables must be same with backend
    // empty constructor for GSON/Jackson needed
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

    public Tour(String tourName, String destination, String startDate, String hotelName, Double totalPrice, String status) {
        this.tourName = tourName;
        this.destination = destination;
        this.startDate = startDate;
        this.hotelName = hotelName;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    public Tour(String tourName, String destination, String startDate, String hotelName, String groupSize, String status) {
        this.tourName = tourName;
        this.destination = destination;
        this.startDate = startDate;
        this.hotelName = hotelName;
        this.groupSize = groupSize;
        this.status = status;
    }

    public String getTourName()       { return tourName; }
    public void setTourName(String tourName) { this.tourName = tourName; }

    public String getDestination()    { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public Double getTotalPrice()     { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }

    public String getHotelName()      { return hotelName; }
    public void setHotelName(String hotelName) { this.hotelName = hotelName; }

    public String getStartDate()      { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getStatus()         { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getGroupSize()      { return groupSize; }
    public void setGroupSize(String groupSize) { this.groupSize = groupSize; }

    public String getVehicle()        { return vehicle; }
    public void setVehicle(String vehicle) { this.vehicle = vehicle; }

    public String getDepartureCity()  { return departureCity; }
    public void setDepartureCity(String departureCity) { this.departureCity = departureCity; }

    public String getGuideName()      { return guideName; }
    public void setGuideName(String guideName) { this.guideName = guideName; }
}
