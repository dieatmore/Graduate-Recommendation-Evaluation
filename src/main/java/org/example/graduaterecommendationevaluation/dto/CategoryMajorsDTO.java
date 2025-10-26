package org.example.graduaterecommendationevaluation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.graduaterecommendationevaluation.dox.Major;
import org.example.graduaterecommendationevaluation.dox.User;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryMajorsDTO {
    private Long id;
    private String categoryName;
    private List<Major> majors;
}
