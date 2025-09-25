package org.example.graduaterecommendationevaluation.dox;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TargetSubmit {
    public static final String SUBMIT = "y02Q"; // 已提交
    public static final String REVIEW = "P5eR"; // 待修改
    public static final  String REJECT = "b7Yz"; // 被驳回
    public static final  String CONFIRM = "59G7"; // 已认定

    @Id
    @CreatedBy
    private Long id;
    private Long userId;
    private Long targetNodeId;
    private Long rootNodeId;
    private BigDecimal mark;
    private String name;
    private String comment;
    private String status;
    private String record;

    @ReadOnlyProperty
    private LocalDateTime createTime;
    @ReadOnlyProperty
    private LocalDateTime updateTime;
}
