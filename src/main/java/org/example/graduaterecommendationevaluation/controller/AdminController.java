package org.example.graduaterecommendationevaluation.controller;

import lombok.RequiredArgsConstructor;
import org.example.graduaterecommendationevaluation.dox.Category;
import org.example.graduaterecommendationevaluation.dox.College;
import org.example.graduaterecommendationevaluation.dox.Major;
import org.example.graduaterecommendationevaluation.dox.User;
import org.example.graduaterecommendationevaluation.exception.Code;
import org.example.graduaterecommendationevaluation.exception.XException;
import org.example.graduaterecommendationevaluation.repository.UserRepository;
import org.example.graduaterecommendationevaluation.service.CollegeService;
import org.example.graduaterecommendationevaluation.service.UserService;
import org.example.graduaterecommendationevaluation.vo.ResultVO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/")
public class AdminController {
    private final CollegeService collegeService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    // 查看所有学院和学院管理员
    @GetMapping("collegesadmins")
    public ResultVO listCollegesadmin(){
        return ResultVO.success(collegeService.listCollegesAndColAdmin());
    }

    // 添加学院
    @PostMapping("colleges")
    public ResultVO addCollege(@RequestBody College college) {
        collegeService.addCollege(college);
        return ResultVO.ok();
    }

    // 修改学院
    @PatchMapping("colleges/{collegeId}")
    public ResultVO updateCollege(@PathVariable Long collegeId,
                                  @RequestBody College college) {
        College c = collegeService.getCollege(collegeId);
        collegeService.updateCollege(c, college);
        return ResultVO.ok();
    }

    // 删除学院
    @DeleteMapping("colleges/{collegeId}")
    public ResultVO deleteCollege(@PathVariable Long collegeId) {
        collegeService.getCollege(collegeId);
        collegeService.deleteCollege(collegeId);
        return ResultVO.ok();
    }

    // 添加学院管理员
    @PostMapping("colleges/{collegeId}")
    public ResultVO addCollegeAdmin(@PathVariable Long collegeId,
                                    @RequestBody User user) {
        collegeService.getCollege(collegeId);
        if(userService.getUser(user.getAccount()) != null) {
            return ResultVO.error(Code.ERROR, "该用户已存在！");
        }
        User u = User.builder()
                .account(user.getAccount())
                .password(passwordEncoder.encode(user.getAccount()))
                .name(user.getName())
                .role(User.COLLAGE_ADMIN)
                .collegeId(collegeId)
                .build();
        userService.addUser(u);
        return ResultVO.ok();
    }

    // 编辑学院管理员
    @PatchMapping("users/{uid}")
    public ResultVO updateCollegeAdmin(@PathVariable Long uid,
                                       @RequestBody User user) {
        User u = userService.getUserById(uid);
        if(userService.getUser(user.getAccount()) != null) {
            return ResultVO.error(Code.ERROR, "该账号已存在！");
        }
        u.setAccount(user.getAccount());
        u.setName(user.getName());
        userService.addUser(u);
        return ResultVO.ok();
    }

    // 删除学院管理员
    @DeleteMapping("users/{uid}")
    public ResultVO deleteCollegeAdmin(@PathVariable Long uid) {
        userService.getUserById(uid);
        userService.deleteUser(uid);
        return ResultVO.ok();
    }

    // 根据学院名称查找学院
    @GetMapping("collegesadmins/{name}")
    public ResultVO findCollegeByname(@PathVariable String name) {
        return ResultVO.success(collegeService.searchByName(name));
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
