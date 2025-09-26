package org.example.graduaterecommendationevaluation.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.graduaterecommendationevaluation.component.JWTComponent;
import org.example.graduaterecommendationevaluation.dox.User;
import org.example.graduaterecommendationevaluation.exception.Code;
import org.example.graduaterecommendationevaluation.service.UserService;
import org.example.graduaterecommendationevaluation.vo.ResultVO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/open/")
public class OpenController {

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
            token1.put("categoryId",userService.getCategoryIdByUserId(userR.getId()));
        }
//        if (userR.getCategoryId() != null) {
//            token1.put("categoryId",userR.getCategoryId());
//        }
        if(userR.getMajorId() != null) {
            token1.put("majorId",userR.getMajorId());
        }
        String token = jwtComponent.encode(token1);
        response.setHeader("token",token);
        response.setHeader("role", userR.getRole());
        return ResultVO.success(userR);
    }

    @PostMapping("register")
    public ResultVO addStudent(@RequestBody User user) {
        userService.addStudent(user);
        return ResultVO.ok();
    }
}