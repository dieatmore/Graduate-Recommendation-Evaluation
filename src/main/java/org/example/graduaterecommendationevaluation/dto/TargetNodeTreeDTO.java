package org.example.graduaterecommendationevaluation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TargetNodeTreeDTO {
    private Long id;
    private Long parentId;
    private String name;
    private Long categoryId;
    private Double maxMark;
    private Short maxNumber;
    private String comment;
    private List<TargetNodeTreeDTO> children;
}
