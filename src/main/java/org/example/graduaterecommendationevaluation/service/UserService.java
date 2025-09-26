package org.example.graduaterecommendationevaluation.service;

import lombok.RequiredArgsConstructor;
import org.example.graduaterecommendationevaluation.dox.Category;
import org.example.graduaterecommendationevaluation.dox.User;
import org.example.graduaterecommendationevaluation.dox.UserCategory;
import org.example.graduaterecommendationevaluation.exception.Code;
import org.example.graduaterecommendationevaluation.exception.XException;
import org.example.graduaterecommendationevaluation.repository.CategoryRepository;
import org.example.graduaterecommendationevaluation.repository.MajorRepository;
import org.example.graduaterecommendationevaluation.repository.UserCategoryRepository;
import org.example.graduaterecommendationevaluation.repository.UserRepository;
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

    // 根据account查找用户
    public User getUser(String account) {
        return userRepository.findByAccount(account);
    }

    // 根据用户id查看对应类别
    public List<Long> getCategoryIdByUserId(Long userId) {
        return userCategoryRepository.findCategoryIdByUserId(userId);
    }

    // 添加用户
    @Transactional
    public void  addUser(Long collegeId, User user, String role) {
        if(userRepository.findByAccount(user.getAccount()) != null) {
            throw XException.builder()
                    .number(Code.ERROR)
                    .message("该用户已存在")
                    .build();
        }

        // 判断添加角色
        String role1 = null;
        if(role.equals(User.ADMIN)) {role1 = User.COLLAGE_ADMIN;}
        else if(role.equals(User.COLLAGE_ADMIN)){role1 = User.TEACHER;}
        else {
            throw XException.builder()
                    .code(Code.BAD_REQUEST)
                    .build();
        }
        User user1 = User.builder()
                .account(user.getAccount())
                .password(passwordEncoder.encode(user.getAccount()))
                .name(user.getName())
                .role(role1)
                .collegeId(collegeId)
                .build();

        userRepository.save(user1);
    }

    // 给指定导师分配类别
    @Transactional
    public void shareCategorys(Long teacherId, List<Long> catsId, List<Long> categoryId) {
        // 判断类别操作权限
        List<Long> notContain = catsId.stream()
                .filter(catId -> !categoryId.contains(catId))
                .toList();
        if (!notContain.isEmpty()) {
            throw XException.builder()
                    .code(Code.FORBIDDEN)
                    .build();
        }

        // 已经有的类别id
        List<Long> haveCategorysId = userCategoryRepository.findCategoryIdByUserId(teacherId);

        // 没有的类别id
        List<Long> addCategorysId = catsId.stream()
                .filter(catId -> !haveCategorysId.contains(catId))
                .toList();

        // 添加没有的类别id
        List<UserCategory> userCategories = addCategorysId.stream()
                .map(catId -> UserCategory.builder()
                        .userId(teacherId)
                        .categoryId(catId)
                        .build())
                .toList();
        userCategoryRepository.saveAll(userCategories);
    }

    @Transactional
    public void  addStudent(User user) {
        if(userRepository.findByAccount(user.getAccount()) != null) {
            throw XException.builder()
                    .number(Code.ERROR)
                    .message("该用户已存在")
                    .build();
        }

        Long catId = majorRepository.findCategoryIdById(user.getMajorId());
        User user1 = User.builder()
                .account(user.getAccount())
                .password(passwordEncoder.encode(user.getPassword()))
                .name(user.getName())
                .role(User.STUDENT)
                .phone(user.getPhone())
                .collegeId(user.getCollegeId())
                .categoryId(catId)
                .majorId(user.getMajorId())
                .build();
        userRepository.save(user1);
        UserCategory uc = UserCategory.builder()
                .userId(userRepository.findIdByAccount(user.getAccount()))
                .categoryId(catId)
                .build();
        userCategoryRepository.save(uc);
    }

}
