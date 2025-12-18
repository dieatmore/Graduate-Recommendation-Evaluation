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
public class RootDTO {
    private Long id;
    private String name;
    private String submitName;
    private String status;
    private Double mark;
    private Double maxMark;
    private List<FileDTO> files;
    private String comment;
    private String record;
}
