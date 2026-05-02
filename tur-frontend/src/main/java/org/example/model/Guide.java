package org.example.model;

public class Guide {

    private Long id;
    private Long userId;
    private String fullName;
    private String email;
    private String phone;
    private String baseCity;
    private String licenseNo;
    private Integer experience;
    private Double dailyFee;

    public Guide() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getBaseCity() { return baseCity; }
    public void setBaseCity(String baseCity) { this.baseCity = baseCity; }
    public String getLicenseNo() { return licenseNo; }
    public void setLicenseNo(String licenseNo) { this.licenseNo = licenseNo; }
    public Integer getExperience() { return experience; }
    public void setExperience(Integer experience) { this.experience = experience; }
    public Double getDailyFee() { return dailyFee; }
    public void setDailyFee(Double dailyFee) { this.dailyFee = dailyFee; }
}