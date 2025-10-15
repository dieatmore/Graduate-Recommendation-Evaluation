package org.example.graduaterecommendationevaluation.controller;

import lombok.RequiredArgsConstructor;
import org.example.graduaterecommendationevaluation.dox.Category;
import org.example.graduaterecommendationevaluation.dox.Major;
import org.example.graduaterecommendationevaluation.dox.TargetNode;
import org.example.graduaterecommendationevaluation.dox.User;
import org.example.graduaterecommendationevaluation.dto.TargetNodeTreeDTO;
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
    public void catExist(Long categoryId, Long collegeId) {
        Category c = collegeService.getCatsBycolIdAndCatId(categoryId, collegeId);
        if(c==null){
            throw XException.builder()
                    .number(Code.ERROR)
                    .message("该类别不存在！")
                    .build();
        }
    }

    // 获取自己的学院
    @GetMapping("college")
    public ResultVO getCollege(@RequestAttribute("collegeId") Long collegeId) {
        return ResultVO.success(collegeService.getCollege(collegeId));
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
        return ResultVO.success(ca);
    }

    // 修改类别
    @PatchMapping("categorys/{categoryId}")
    public ResultVO updateCategory(@PathVariable Long categoryId,
                                   @RequestBody Category category,
                                   @RequestAttribute("collegeId") Long collegeId) {
        catExist(categoryId, collegeId);
        Category c = collegeService.getCatById(categoryId);
        c.setName(category.getName());
        c.setWeight(category.getWeight());
        collegeService.updateCategory(c);
        return ResultVO.ok();
    }

    // 删除类别
    @DeleteMapping("categorys/{categoryId}")
    public ResultVO deleteCategory(@PathVariable Long categoryId,
                                   @RequestAttribute("collegeId") Long collegeId) {
        catExist(categoryId, collegeId);
        collegeService.deleteCategory(categoryId);
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
                            @RequestBody User user) {
        if(userService.getUser(user.getAccount())!=null) {
            return ResultVO.error(Code.ERROR, "该用户已存在！");
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
            List<Category> categories = collegeService.findCatsId(catsId);
            if (categories.size() != catsId.size()) {
                return ResultVO.error(Code.ERROR, "不存在该类别！");
            }
            List<Long> invalidIds = categories.stream()
                    .filter(cat -> !cat.getCollegeId().equals(collegeId)) // 学院id不匹配
                    .map(Category::getId)
                    .toList();
            if (!invalidIds.isEmpty()) {
                return ResultVO.error(Code.FORBIDDEN);
            }
        }
        userService.shareCategorys(teacherId, catsId);
        return ResultVO.ok();
    }

    // 添加/修改指标节点
    @PostMapping("categorys/{catId}/targetnodes")
    public ResultVO addTargetNode(@PathVariable Long catId,
                                  @RequestBody TargetNode targetNode,
                                  @RequestAttribute("collegeId") Long collegeId) {
        catExist(catId, collegeId);
        targetNode.setCategoryId(catId);
        targetService.addTargetNode(targetNode);
        return ResultVO.ok();
    }

    // 拖拽更新指标节点
    @PatchMapping("categorys/{catId}/targetnodes/{nodeId}")
    public ResultVO updateTargetNodeByDrag(@PathVariable("catId") Long catId,
                                           @PathVariable("nodeId") Long nodeId,
                                           @RequestBody Long parentNode,
                                           @RequestAttribute("collegeId") Long collegeId) {
        catExist(catId, collegeId);
        TargetNode node = targetService.getNodeById(nodeId);
        node.setParentId(parentNode);
        targetService.addTargetNode(node);
        return ResultVO.ok();
    }

    // 查看指标节点
    @GetMapping("categorys/{catId}/targetnodes")
    public ResultVO listTargetNodes(@PathVariable Long catId,
                                    @RequestAttribute("collegeId") Long collegeId) {
        catExist(catId, collegeId);
        List<TargetNodeTreeDTO> t = targetService.listTargetNodeTree(catId);
        return ResultVO.success(t);
    }

    // 删除指标节点几所有子节点
    @DeleteMapping("categorys/{catId}/targetnodes/{nodeId}")
    public ResultVO deleteTargetNode(@PathVariable("catId") Long catId,
                                     @PathVariable("nodeId") Long nodeId,
                                     @RequestAttribute("collegeId") Long collegeId) {
        catExist(catId, collegeId);
        List<TargetNode> children = targetService.listChildrenNodes(catId, nodeId);
        children.add(targetService.getNodeById(nodeId));
        targetService.deleteTargetNodes(children);
        return ResultVO.ok();
    }

    // 修改密码
    @PatchMapping("password")
    public ResultVO updatePassword(@RequestBody User user,
                                   @RequestAttribute("uid") Long uid) {
        User u = userService.getUserById(uid);
        u.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.addUser(u);
        return ResultVO.ok();
    }

    // 修改信息
    @PatchMapping("userinfo")
    public ResultVO updateUserInfo(@RequestBody User user,
                                   @RequestAttribute("uid") Long uid) {
        User u = userService.getUserById(uid);
        u.setAccount(user.getAccount());
        u.setName(user.getName());
        u.setPhone(user.getPhone());
        userService.addUser(u);
        return ResultVO.success(u);
    }
}
