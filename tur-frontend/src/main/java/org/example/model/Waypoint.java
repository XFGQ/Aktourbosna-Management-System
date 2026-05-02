package org.example.model;

public class Waypoint {

    private Long id;
    private String city;
    private String country;
    private Double extraFee = 0.0;

    public Waypoint() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public Double getExtraFee() { return extraFee; }
    public void setExtraFee(Double extraFee) { this.extraFee = extraFee; }
}