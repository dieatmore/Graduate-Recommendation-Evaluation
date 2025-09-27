package org.example.graduaterecommendationevaluation.controller;

import lombok.RequiredArgsConstructor;
import org.example.graduaterecommendationevaluation.dox.Category;
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
@RequestMapping("api/teacher/")
public class TeacherController {
    private final UserService userService;

    // 查看自己管理的类别
    @GetMapping("categorys")
    public ResultVO listCategories(@RequestAttribute("catsId") List<Long> catsId){
        List<Category> cats = userService.findCatsId(catsId);
        return  ResultVO.success(cats);
    }
}
