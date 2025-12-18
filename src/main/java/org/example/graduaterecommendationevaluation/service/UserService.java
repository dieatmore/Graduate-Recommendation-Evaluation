package org.example.graduaterecommendationevaluation.service;

import lombok.RequiredArgsConstructor;
import org.example.graduaterecommendationevaluation.dox.*;
import org.example.graduaterecommendationevaluation.dto.StudentInfoDTO;
import org.example.graduaterecommendationevaluation.dto.StudentsDTO;
import org.example.graduaterecommendationevaluation.dto.SubmitDTO;
import org.example.graduaterecommendationevaluation.dto.TeacherCatDTO;
import org.example.graduaterecommendationevaluation.exception.Code;
import org.example.graduaterecommendationevaluation.exception.XException;
import org.example.graduaterecommendationevaluation.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserCategoryRepository userCategoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final MajorRepository majorRepository;
    private final ScoreRepository scoreRepository;
    private final CategoryRepository categoryRepository;

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

    // 学生获取个人统计信息
    public StudentsDTO getDetail(Long uid) {
        return userRepository.getDetail(uid);
    }

    // 查看导师及其管理的类别
    public List<TeacherCatDTO> getTeacherCats(Long collegeId) {
        List<User> teachers = userRepository.findByCollegeIdAndRole(collegeId, User.TEACHER);
        List<TeacherCatDTO> teaCats = new ArrayList<>();
        teachers.forEach(teacher -> {
            List<Long> catIds = userCategoryRepository.findCategoryIdByUserId(teacher.getId());
            List<Category> cats = categoryRepository.findAllById(catIds);
            TeacherCatDTO dto = TeacherCatDTO.builder()
                    .id(teacher.getId())
                    .account(teacher.getAccount())
                    .name(teacher.getName())
                    .categorys(cats)
                    .build();
            teaCats.add(dto);
        });
        return teaCats;
    }

    // 查看某个学生具体信息
    public List<SubmitDTO> getStudentDetail(Long uid) {
        return userRepository.studentDetail(uid);
    }

    // 查看某个学生info信息
    public StudentInfoDTO getUserInfo(Long uid) {
        return userRepository.getInfoById(uid);
    }
}
