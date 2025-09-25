package org.example.graduaterecommendationevaluation.dox;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    public static final String ADMIN = "uQ7i"; // 超级管理员
    public static final String COLLAGE_ADMIN = "6fT4"; // 学院管理员
    public static final  String TEACHER = "Dp4L"; // 导师
    public static final  String STUDENT = "po8V"; // 学生

    @Id
    @CreatedBy
    private Long id;
    private String account;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String name;
    private String phone;
    private String role;
    private Long collegeId;
    private Long categoryId;
    private Long majorId;

    @ReadOnlyProperty
    private LocalDateTime createTime;
    @ReadOnlyProperty
    private LocalDateTime updateTime;
}
