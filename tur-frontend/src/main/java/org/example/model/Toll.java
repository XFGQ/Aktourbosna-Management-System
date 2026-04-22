package org.example.model
public class Toll {

    private int tollId;
    private String name;
    private String location;
    private float costCat1;
    private float costCat2;
    private float costCat3;

    public Toll() {}

    public Toll(int tollId, String name, String location,
                float costCat1, float costCat2, float costCat3) {
        this.tollId = tollId;
        this.name = name;
        this.location = location;
        this.costCat1 = costCat1;
        this.costCat2 = costCat2;
        this.costCat3 = costCat3;
    }

    public float getCostForType(String vehicleType) {
        switch (vehicleType.toUpperCase()) {
            case "CAT1": return costCat1;
            case "CAT2": return costCat2;
            case "CAT3": return costCat3;
            default: return costCat1;
        }
    }

    public String getDetails() {
        return "Toll: " + name + " | Location: " + location
                + " | Cat1: " + costCat1
                + " | Cat2: " + costCat2
                + " | Cat3: " + costCat3;
    }

    public int getTollId() { return tollId; }
    public void setTollId(int tollId) { this.tollId = tollId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public float getCostCat1() { return costCat1; }
    public void setCostCat1(float costCat1) { this.costCat1 = costCat1; }

    public float getCostCat2() { return costCat2; }
    public void setCostCat2(float costCat2) { this.costCat2 = costCat2; }

    public float getCostCat3() { return costCat3; }
    public void setCostCat3(float costCat3) { this.costCat3 = costCat3; }
}
