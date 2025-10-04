package org.example.graduaterecommendationevaluation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentsDTO {
    private Long id;
    private String account;
    private String name;
    private Double scorex;
    private Short ranking;
    private Double confirmed_score; // 已认定成绩
    private Short confirmed_items; // 已认定项数
    private Short pending_items; // 待审核项数
    private Short modify_items; // 待修改项数
    private Short rejected_items; // 已驳回项数
    private Short total_items; // 总提交项数
}
