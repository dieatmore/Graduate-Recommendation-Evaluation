package org.example.graduaterecommendationevaluation.controller;

import lombok.RequiredArgsConstructor;
import org.example.graduaterecommendationevaluation.dox.Category;
import org.example.graduaterecommendationevaluation.dox.User;
import org.example.graduaterecommendationevaluation.exception.Code;
import org.example.graduaterecommendationevaluation.repository.CategoryRepository;
import org.example.graduaterecommendationevaluation.service.CollegeService;
import org.example.graduaterecommendationevaluation.service.UserService;
import org.example.graduaterecommendationevaluation.vo.ResultVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/teacher/")
public class TeacherController {
    private final CollegeService collegeService;

    // 查看自己管理的类别
    @GetMapping("categorys")
    public ResultVO listCategories(@RequestAttribute(value = "catsId", required = false) List<Long> catsId,
                                   @RequestAttribute("role") String role,
                                   @RequestAttribute("collegeId") Long collegeId) {
        if(role.equals(User.TEACHER)){
            return ResultVO.success(collegeService.findCatsId(catsId));
        } else {   // 学院管理员
            List<Category> ca = collegeService.getCategorysBycolId(collegeId);
            if(ca.isEmpty()) {
                return ResultVO.error(Code.ERROR, "该用户没有管理的类别！");
            }
            return ResultVO.success(ca);
        }
    }
}
