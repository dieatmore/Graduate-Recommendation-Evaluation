package org.example.graduaterecommendationevaluation.controller;

import lombok.RequiredArgsConstructor;
import org.example.graduaterecommendationevaluation.dox.Category;
import org.example.graduaterecommendationevaluation.dox.TargetSubmit;
import org.example.graduaterecommendationevaluation.dox.User;
import org.example.graduaterecommendationevaluation.exception.Code;
import org.example.graduaterecommendationevaluation.exception.XException;
import org.example.graduaterecommendationevaluation.repository.CategoryRepository;
import org.example.graduaterecommendationevaluation.service.CollegeService;
import org.example.graduaterecommendationevaluation.service.TargetService;
import org.example.graduaterecommendationevaluation.service.UserService;
import org.example.graduaterecommendationevaluation.vo.ResultVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/teacher/")
public class TeacherController {
    private final CollegeService collegeService;
    private final UserService userService;
    private final TargetService targetService;

    // 学院管理员判断类别存在(操作权限)
    public void catExist(Long categoryId, Long collegeId) {
        Category c = collegeService.getCatsBycolIdAndCatId(categoryId, collegeId);
        if(c==null){
            throw XException.builder()
                    .number(Code.ERROR)
                    .message("该类别不存在！")
                    .build();
        }
    }

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

    // 查看某专业下所有学生的统计信息
    @GetMapping("categorys/{categoryId}/majors/{majorId}/students")
    public ResultVO listStudents(@PathVariable("categoryId") Long categoryId,
                                 @PathVariable("majorId") Long majorId,
                                 @RequestAttribute(value = "catsId", required = false) List<Long> catsId,
                                 @RequestAttribute("role") String role,
                                 @RequestAttribute("collegeId") Long collegeId) {
        if(role.equals(User.TEACHER)){
            if (!catsId.contains(categoryId)) {
                return ResultVO.error(Code.ERROR, "该类别不存在！");
            }
            collegeService.findMajorByMidAndCatId(majorId, categoryId);
            return ResultVO.success(userService.getStudents(majorId));
        } else {   // 学院管理员
            catExist(categoryId, collegeId);
            collegeService.findMajorByMidAndCatId(majorId, categoryId);
            return ResultVO.success(userService.getStudents(majorId));
        }
    }

    // 查看某个学生的个人信息
    @GetMapping("students/{studentId}/info")
    private ResultVO studentInfo(@PathVariable("studentId") Long studentId,
                                 @RequestAttribute(value = "catsId", required = false) List<Long> catsId,
                                 @RequestAttribute("collegeId")  Long collegeId,
                                 @RequestAttribute("role") String role) {
        if(role.equals(User.TEACHER)){
            User student = userService.getUserById(studentId);
            if(!catsId.contains(student.getCategoryId())) {
                return ResultVO.error(Code.FORBIDDEN);
            }
        } else {
            User student = userService.getUserById(studentId);
            if(!collegeId.equals(student.getCollegeId())) {
                return ResultVO.error(Code.FORBIDDEN);
            }
        }
        return ResultVO.success(userService.getUserInfo(studentId));
    }

    // 查看某个学生的具体信息
    @GetMapping("students/{studentId}")
    public ResultVO studentDetail(@PathVariable("studentId") Long studentId,
                                  @RequestAttribute(value = "catsId", required = false) List<Long> catsId,
                                  @RequestAttribute("collegeId")  Long collegeId,
                                  @RequestAttribute("role") String role) {
        if(role.equals(User.TEACHER)){
            User student = userService.getUserById(studentId);
            if(!catsId.contains(student.getCategoryId())) {
                return ResultVO.error(Code.FORBIDDEN);
            }
        } else {
            User student = userService.getUserById(studentId);
            if(!collegeId.equals(student.getCollegeId())) {
                return ResultVO.error(Code.FORBIDDEN);
            }
        }
        return ResultVO.success(userService.getStudentDetail(studentId));
    }

    // 给学生提交项评分
    @PatchMapping("students/{studentId}/submits/{submitId}")
    public ResultVO submitMark(@PathVariable("studentId") Long studentId,
                               @PathVariable("submitId") Long submitId,
                               @RequestAttribute(value = "catsId", required = false) List<Long> catsId,
                               @RequestAttribute("collegeId")  Long collegeId,
                               @RequestAttribute("uid") Long uid,
                               @RequestAttribute("role") String role,
                               @RequestBody TargetSubmit targetSubmit) {
        if(role.equals(User.TEACHER)){
            User student = userService.getUserById(studentId);
            if(!catsId.contains(student.getCategoryId())) {
                return ResultVO.error(Code.FORBIDDEN);
            }
        } else {    // 学院管理员
            User student = userService.getUserById(studentId);
            if(!collegeId.equals(student.getCollegeId())) {
                return ResultVO.error(Code.FORBIDDEN);
            }
        }
        TargetSubmit ts = targetService.getSubmitById(submitId);
        if(!ts.getUserId().equals(studentId)) {
            return ResultVO.error(Code.FORBIDDEN);
        }
        targetService.submitMark(uid, targetSubmit,submitId);
        return ResultVO.ok();
    }


}
