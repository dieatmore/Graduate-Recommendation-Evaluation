package org.example.graduaterecommendationevaluation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentInfoDTO {
    private Long id;
    private String account;
    private String name;
    private String phone;
    private Double scorex;
    private Short ranking;
    private String status;
}
