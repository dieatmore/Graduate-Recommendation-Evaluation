package org.example.graduaterecommendationevaluation.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.graduaterecommendationevaluation.component.JWTComponent;
import org.example.graduaterecommendationevaluation.dox.Category;
import org.example.graduaterecommendationevaluation.dox.College;
import org.example.graduaterecommendationevaluation.dox.Major;
import org.example.graduaterecommendationevaluation.dox.User;
import org.example.graduaterecommendationevaluation.dto.CollegeMajorDTO;
import org.example.graduaterecommendationevaluation.exception.Code;
import org.example.graduaterecommendationevaluation.service.CollegeService;
import org.example.graduaterecommendationevaluation.service.UserService;
import org.example.graduaterecommendationevaluation.vo.ResultVO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/open/")
public class OpenController {

    private final CollegeService collegeService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JWTComponent jwtComponent;
    @PostMapping("login")
    public ResultVO login(@RequestBody User user, HttpServletResponse response) {
        User userR = userService.getUser(user.getAccount());
        if(userR == null || !passwordEncoder.matches(user.getPassword(), userR.getPassword())) {
            return ResultVO.error(Code.LOGIN_ERROR);
        }
        Map<String, Object> token1 = new HashMap<>();
        token1.put("uid",userR.getId());
        token1.put("role",userR.getRole());
        if(userR.getCollegeId() != null) {
            token1.put("collegeId",userR.getCollegeId());
        }
        if(userService.getCategoryIdByUserId(userR.getId()) != null) {
            if(userR.getRole().equals(User.TEACHER)) {
                token1.put("catsId",userService.getCategoryIdByUserId(userR.getId()));
            }
            if(userR.getRole().equals(User.STUDENT)) {
                token1.put("catId",userR.getCategoryId());
            }
        }
        if(userR.getMajorId() != null) {
            token1.put("majorId",userR.getMajorId());
        }
        String token = jwtComponent.encode(token1);
        response.setHeader("token",token);
        response.setHeader("role", userR.getRole());
        return ResultVO.success(userR);
    }

    // 学生注册
    @PostMapping("register")
    public ResultVO addStudent(@RequestBody User user) {
        userService.addStudent(user);
        return ResultVO.ok();
    }

    // 获取学院
    @GetMapping("register/collegesmajors")
    public ResultVO listCollegesAndMajors() {
        List<College> allColleges = collegeService.listColleges();
        List<CollegeMajorDTO> resultList = new ArrayList<>();
        for (College college : allColleges) {
            CollegeMajorDTO dto = CollegeMajorDTO.builder()
                    .id(college.getId())
                    .name(college.getName())
                    .build();

            List<Category> categories = collegeService.getCategorysBycolId(college.getId());
            List<Major> majorsByCol = new ArrayList<>();
            for (Category category : categories) {
                List<Major> majorsByCat =  collegeService.listMajors(category.getId());
                if(!majorsByCat.isEmpty()) {
                    majorsByCol.addAll(majorsByCat);
                }
            }
            dto.setMajors(majorsByCol);
            resultList.add(dto);
        }
        return ResultVO.success(resultList);
    }
}