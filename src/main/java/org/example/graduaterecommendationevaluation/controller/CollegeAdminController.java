package org.example.graduaterecommendationevaluation.controller;

import lombok.RequiredArgsConstructor;
import org.example.graduaterecommendationevaluation.dox.Category;
import org.example.graduaterecommendationevaluation.dox.Major;
import org.example.graduaterecommendationevaluation.dox.User;
import org.example.graduaterecommendationevaluation.service.CollegeService;
import org.example.graduaterecommendationevaluation.service.UserService;
import org.example.graduaterecommendationevaluation.vo.ResultVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/collegeadmin/")
public class CollegeAdminController {
    private final CollegeService collegeService;
    private final UserService userService;

    // 查看所有类别
    @GetMapping("categorys")
    public ResultVO listCategories(@RequestAttribute("collegeId") Long collegeId){
        return ResultVO.success(collegeService.listCategories(collegeId));
    }

    // 添加类别
    @PostMapping("categorys")
    public ResultVO addCategory(@RequestBody Category category,
                                @RequestAttribute("collegeId") Long collegeId,
                                @RequestAttribute("uid") Long uid) {
        category.setCollegeId(collegeId);
        // 同时添加用户类别关系
        collegeService.addCategory(category, uid);
        return ResultVO.ok();
    }

    // 修改类别
    @PatchMapping("categorys/{categoryId}")
    public ResultVO updateCategory(@PathVariable Long categoryId,
                                   @RequestBody Category category,
                                   @RequestAttribute("collegeId") Long collegeId) {
        collegeService.updateCategory(categoryId, category ,collegeId);
        return ResultVO.ok();
    }

    // 删除类别
    @DeleteMapping("categorys/{categoryId}")
    public ResultVO deleteCategory(@PathVariable Long categoryId,
                                   @RequestAttribute("collegeId") Long collegeId) {
        // 同时删除所有用户与该类别关系
        collegeService.deleteCategory(categoryId, collegeId);
        return ResultVO.ok();
    }

    // 查看所有专业
    @GetMapping("categorys/{catId}/majors")
    public ResultVO listMajors(@RequestAttribute("categoryId")List<Long> categoryId,
                               @PathVariable Long catId) {
        return ResultVO.success(collegeService.listMajors(catId, categoryId));
    }

    // 添加专业
    @PostMapping("categorys/{catId}/majors")
    public ResultVO addMajor(@RequestBody Major major,
                             @PathVariable Long catId,
                             @RequestAttribute("categoryId")List<Long> categoryId) {
        collegeService.addMajor(major, catId, categoryId);
        return ResultVO.ok();
    }

    // 修改专业
    @PatchMapping("categorys/{catId}/majors/{majorId}")
    public ResultVO updateMajor(@PathVariable Long majorId,
                                @PathVariable Long catId,
                                @RequestAttribute("categoryId")List<Long> categoryId,
                                @RequestBody Major major) {
        collegeService.updateMajor(majorId, major, catId, categoryId);
        return ResultVO.ok();
    }

    // 删除专业
    @DeleteMapping("categorys/{catId}/majors/{majorId}")
    public ResultVO deleteMajor(@PathVariable Long majorId,
                                @PathVariable Long catId,
                                @RequestAttribute("categoryId")List<Long> categoryId) {
        collegeService.deleteMajor(majorId, catId, categoryId);
        return ResultVO.ok();
    }

    // 添加导师
    @PostMapping("teachers")
    public ResultVO addUser(@RequestAttribute("collegeId") Long collegeId,
                            @RequestBody User user,
                            @RequestAttribute("role") String role) {
        userService.addUser(collegeId, user, role);
        return ResultVO.ok();
    }

    // 给指定导师分配类别
    @PatchMapping("teachers/{teacherId}")
    public ResultVO shareCategorys(@PathVariable Long teacherId,
                                   @RequestBody List<Long> catsId,
                                   @RequestAttribute("categoryId") List<Long> categoryId) {
        userService.shareCategorys(teacherId, catsId, categoryId);
        return ResultVO.ok();
    }
}
