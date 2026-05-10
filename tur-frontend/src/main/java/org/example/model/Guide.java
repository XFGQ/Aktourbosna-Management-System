package org.example.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
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
}
