package org.example.graduaterecommendationevaluation.service;

import lombok.RequiredArgsConstructor;
import org.example.graduaterecommendationevaluation.dox.*;
import org.example.graduaterecommendationevaluation.dto.StudentsDTO;
import org.example.graduaterecommendationevaluation.exception.Code;
import org.example.graduaterecommendationevaluation.exception.XException;
import org.example.graduaterecommendationevaluation.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserCategoryRepository userCategoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final MajorRepository majorRepository;
    private final ScoreRepository scoreRepository;

    // 根据account查找用户
    public User getUser(String account) {
        return userRepository.findByAccount(account);
    }

    // 根据id查找用户
    public User getUserById(Long uid) {
        return userRepository.findById(uid)
                .orElseThrow(()-> XException.builder()
                        .number(Code.ERROR)
                        .message("不存在该用户！")
                        .build());
    }

    // 根据用户id查看对应类别
    public List<Long> getCategoryIdByUserId(Long userId) {
        return userCategoryRepository.findCategoryIdByUserId(userId);
    }

    // 添加用户
    @Transactional
    public void  addUser(User user) {
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long uid) {
        userRepository.deleteById(uid);
    }

    // id判断用户是否存在
    public void  judgeUser(Long id) {
        if(userRepository.findById(id).isEmpty()) {
            throw XException.builder()
                    .number(Code.ERROR)
                    .message("该用户不存在！")
                    .build();
        }
    }

    // 给指定导师分配类别
    @Transactional
    public void shareCategorys(Long teacherId, List<Long> catsId) {
        userCategoryRepository.deleteByUserId(teacherId); // 删除导师所有旧关系
        if (catsId.isEmpty()) {
            return;
        }
        List<UserCategory> userCategories = catsId.stream()
                .map(catId -> UserCategory.builder()
                        .userId(teacherId)
                        .categoryId(catId)
                        .build())
                .toList();
        userCategoryRepository.saveAll(userCategories);
    }

    // 学生注册
    @Transactional
    public void  addStudent(User user) {
        if(userRepository.findByAccount(user.getAccount()) != null) {
            throw XException.builder()
                    .number(Code.ERROR)
                    .message("该用户已存在")
                    .build();
        }

        User user1 = User.builder()
                .account(user.getAccount())
                .password(passwordEncoder.encode(user.getPassword()))
                .name(user.getName())
                .role(User.STUDENT)
                .phone(user.getPhone())
                .collegeId(user.getCollegeId())
                .categoryId(user.getCategoryId())
                .majorId(user.getMajorId())
                .build();
        userRepository.save(user1);
    }

    // 根据uid查找加权成绩信息
    public Score getScoreByUid(Long uid) {
        return scoreRepository.findByUserId(uid);
    }

    // 提交加权成绩信息
    @Transactional
    public void submitScore(Score score) {
        scoreRepository.save(score);
    }

    // 获取学生统计信息
    public List<StudentsDTO> getStudents(Long majorId) {
        return userRepository.listStudents(majorId, User.STUDENT);
    }
}
