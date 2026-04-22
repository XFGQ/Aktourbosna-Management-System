package org.example.model
import java.util.List;

public class Guide extends User {

    private String phone;
    private String licenseNo;
    private List<String> languages;
    private List<String> countries;
    private List<String> skills;
    private int experience;
    private float rating;

    public Guide() {}

    public Guide(int userId, String username, String email,
                 String phone, String licenseNo,
                 List<String> languages, List<String> countries,
                 List<String> skills, int experience, float rating) {
        super(userId, username, email, Role.GUIDE);
        this.phone = phone;
        this.licenseNo = licenseNo;
        this.languages = languages;
        this.countries = countries;
        this.skills = skills;
        this.experience = experience;
        this.rating = rating;
    }

    @Override
    public boolean login() { return true; }

    @Override
    public void logout() {}

    @Override
    public void updateProfile() {}

    public void uploadReceipt() {}

    public void updateTourInfo() {}

    public List<Tour> getAssignedTours() { return null; }

    public List<String> getLanguages() { return languages; }
    public void setLanguages(List<String> languages) { this.languages = languages; }

    public List<String> getCountries() { return countries; }
    public void setCountries(List<String> countries) { this.countries = countries; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getLicenseNo() { return licenseNo; }
    public void setLicenseNo(String licenseNo) { this.licenseNo = licenseNo; }

    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }

    public int getExperience() { return experience; }
    public void setExperience(int experience) { this.experience = experience; }
}
