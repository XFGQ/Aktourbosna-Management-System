package org.example.model;

public class Customer {

    private Long id;
    private Tour tour;
    private String fullName;
    private String passportNumber;
    private String phone;
    private String nationality;

    public Customer() {
     }

    public Customer(Long id, String fullName, String passportNumber,
                    String phone, String nationality) {
        this.id = id;
        this.fullName = fullName;
        this.passportNumber = passportNumber;
        this.phone = phone;
        this.nationality = nationality;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Tour getTour() { return tour; }
    public void setTour(Tour tour) { this.tour = tour; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPassportNumber() { return passportNumber; }
    public void setPassportNumber(String passportNumber) { this.passportNumber = passportNumber; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }
}