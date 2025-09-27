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

    // 查看所有学院
    @GetMapping("colleges")
    public ResultVO listColleges(){
        return ResultVO.success(collegeService.listColleges());
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
                                    @RequestBody User user,
                                    @RequestAttribute("role")  String role) {
        collegeService.getCollege(collegeId);
        if(userService.getUser(user.getAccount()) != null) {
            throw XException.builder()
                    .number(Code.ERROR)
                    .message("该用户已存在")
                    .build();
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
}
