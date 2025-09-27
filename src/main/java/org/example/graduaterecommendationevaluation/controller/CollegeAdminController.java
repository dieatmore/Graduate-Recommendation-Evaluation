package org.example.graduaterecommendationevaluation.controller;

import lombok.RequiredArgsConstructor;
import org.example.graduaterecommendationevaluation.dox.Category;
import org.example.graduaterecommendationevaluation.dox.Major;
import org.example.graduaterecommendationevaluation.dox.TargetNode;
import org.example.graduaterecommendationevaluation.dox.User;
import org.example.graduaterecommendationevaluation.exception.Code;
import org.example.graduaterecommendationevaluation.exception.XException;
import org.example.graduaterecommendationevaluation.service.CollegeService;
import org.example.graduaterecommendationevaluation.service.TargetService;
import org.example.graduaterecommendationevaluation.service.UserService;
import org.example.graduaterecommendationevaluation.vo.ResultVO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/collegeadmin/")
public class CollegeAdminController {
    private final CollegeService collegeService;
    private final UserService userService;
    private final TargetService targetService;
    private final PasswordEncoder passwordEncoder;

    // 判断类别存在(操作权限)
    public Category catExist(Long categoryId, Long collegeId) {
        Category c = collegeService.getCatsBycolIdAndCatId(categoryId, collegeId);
        if(c==null){
            throw XException.builder()
                    .number(Code.ERROR)
                    .message("该类别不存在！")
                    .build();
        }
        return c;
    }

    // 添加类别
    @PostMapping("categorys")
    public ResultVO addCategory(@RequestBody Category category,
                                @RequestAttribute("collegeId") Long collegeId) {
        category.setCollegeId(collegeId);
        collegeService.addCategory(category);
        return ResultVO.ok();
    }

    // 查看所有类别
    @GetMapping("categorys")
    public ResultVO listCategories(@RequestAttribute("collegeId") Long collegeId){
        List<Category> ca = collegeService.getCategorysBycolId(collegeId);
        if(ca.isEmpty()) {
            throw XException.builder()
                    .number(Code.ERROR)
                    .message("该用户没有管理的类别！")
                    .build();
        }
        return ResultVO.success(ca);
    }

    // 修改类别
    @PatchMapping("categorys/{categoryId}")
    public ResultVO updateCategory(@PathVariable Long categoryId,
                                   @RequestBody Category category,
                                   @RequestAttribute("collegeId") Long collegeId) {
        Category c = catExist(categoryId, collegeId);
        c.setName(category.getName());
        c.setWeight(category.getWeight());
        collegeService.updateCategory(c, category);
        return ResultVO.ok();
    }

    // 删除类别
    @DeleteMapping("categorys/{categoryId}")
    public ResultVO deleteCategory(@PathVariable Long categoryId,
                                   @RequestAttribute("collegeId") Long collegeId) {
        Category c = catExist(categoryId, collegeId);
        collegeService.deleteCategory(c.getId());
        return ResultVO.ok();
    }

    // 添加专业
    @PostMapping("categorys/{catId}/majors")
    public ResultVO addMajor(@RequestBody Major major,
                             @PathVariable Long catId,
                             @RequestAttribute("collegeId") Long collegeId) {

        catExist(catId, collegeId);
        major.setCategoryId(catId);
        collegeService.addMajor(major);
        return ResultVO.ok();
    }

    // 查看所有专业
    @GetMapping("categorys/{catId}/majors")
    public ResultVO listMajors(@RequestAttribute("collegeId") Long collegeId,
                               @PathVariable Long catId) {
        catExist(catId, collegeId);
        return ResultVO.success(collegeService.listMajors(catId));
    }

    // 修改专业
    @PatchMapping("categorys/{catId}/majors/{majorId}")
    public ResultVO updateMajor(@PathVariable Long majorId,
                                @PathVariable Long catId,
                                @RequestAttribute("collegeId") Long collegeId,
                                @RequestBody Major major) {
        catExist(catId, collegeId);
        Major m = collegeService.findMajorByMidAndCatId(majorId, catId);
        m.setName(major.getName());
        collegeService.updateMajor(m);
        return ResultVO.ok();
    }

    // 删除专业
    @DeleteMapping("categorys/{catId}/majors/{majorId}")
    public ResultVO deleteMajor(@PathVariable Long majorId,
                                @PathVariable Long catId,
                                @RequestAttribute("collegeId") Long collegeId) {
        catExist(catId, collegeId);
        collegeService.findMajorByMidAndCatId(majorId, catId);
        collegeService.deleteMajor(majorId);
        return ResultVO.ok();
    }

    // 添加导师
    @PostMapping("teachers")
    public ResultVO addUser(@RequestAttribute("collegeId") Long collegeId,
                            @RequestBody User user,
                            @RequestAttribute("role") String role) {
        if(userService.getUser(user.getAccount())!=null) {
            throw XException.builder()
                    .number(Code.ERROR)
                    .message("该用户已存在")
                    .build();
        }
        User u = User.builder()
                .account(user.getAccount())
                .password(passwordEncoder.encode(user.getAccount()))
                .name(user.getName())
                .role(User.TEACHER)
                .collegeId(collegeId)
                .build();
        userService.addUser(u);
        return ResultVO.ok();
    }

    // 给指定导师分配类别
    @PatchMapping("teachers/{teacherId}")
    public ResultVO shareCategorys(@PathVariable Long teacherId,
                                   @RequestBody List<Long> catsId,
                                   @RequestAttribute("collegeId") Long collegeId) {
        userService.judgeUser(teacherId);
        if (!catsId.isEmpty()) {
            List<Category> categories = userService.findCatsId(catsId);
            if (categories.size() != catsId.size()) {
                throw XException.builder()
                        .number(Code.ERROR)
                        .message("不存在该类别！")
                        .build();
            }
            List<Long> invalidIds = categories.stream()
                    .filter(cat -> !cat.getCollegeId().equals(collegeId)) // 学院id不匹配
                    .map(Category::getId)
                    .toList();
            if (!invalidIds.isEmpty()) {
                throw XException.builder()
                        .code(Code.FORBIDDEN)
                        .build();
            }
        }
        userService.shareCategorys(teacherId, catsId);
        return ResultVO.ok();
    }

    // 添加指标节点
    @PostMapping("targetnode/{catId}")
    public ResultVO addTargetNode(@PathVariable Long catId,
                                  @RequestBody TargetNode targetNode,
                                  @RequestAttribute("collegeId") Long collegeId) {
        catExist(catId, collegeId);
        targetNode.setCategoryId(catId);
        targetService.addTargetNode(targetNode);
        return ResultVO.ok();
    }

    // 查看指标节点
//    @GetMapping("targetnode/{catId}")
//    public ResultVO listTargetNodes(@PathVariable Long catId,
//                                    @RequestAttribute("collegeId") Long collegeId) {
//        catExist(catId, collegeId);
//        targetService.
//    }
}
