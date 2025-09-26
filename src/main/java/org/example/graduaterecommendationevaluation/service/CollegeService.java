package org.example.graduaterecommendationevaluation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.graduaterecommendationevaluation.dox.*;
import org.example.graduaterecommendationevaluation.exception.Code;
import org.example.graduaterecommendationevaluation.exception.XException;
import org.example.graduaterecommendationevaluation.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CollegeService {
    private final CollegeRepository collegeRepository;
    private final CategoryRepository categoryRepository;
    private final MajorRepository majorRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserCategoryRepository userCategoryRepository;

    // 创建学院
    @Transactional
    public void addCollege(College college) {
        collegeRepository.save(college);
    }

    // 查看所有学院
    public List<College> listColleges() {
        return collegeRepository.findAll();
    }

    // 修改学院
    @Transactional
    public void updateCollege(Long collegeId,College college) {
        College c =  collegeRepository.findById(collegeId)
                .orElseThrow(()-> XException.builder()
                        .number(Code.ERROR)
                        .message("不存在该学院！")
                        .build());
        c.setName(college.getName());
        collegeRepository.save(c);
    }

    // 删除学院
    @Transactional
    public void deleteCollege(Long collegeId) {
        if(categoryRepository.existsByCollegeId(collegeId)) {
            throw XException.builder()
                    .number(Code.ERROR)
                    .message("该学院有类别，不可删除！")
                    .build();
        }
        collegeRepository.deleteById(collegeId);
    }

    // 创建类别
    @Transactional
    public void addCategory(Category category, Long uid) {
        categoryRepository.save(category);
        UserCategory uc = UserCategory.builder()
                .userId(uid)
                .categoryId(category.getId())
                .build();
        userCategoryRepository.save(uc);
    }

    // 查看所有类别
    public List<Category> listCategories(Long collegeId) {
        return categoryRepository.findByCollegeId(collegeId);
    }

    // 修改类别
    @Transactional
    public void updateCategory(Long categoryId,Category category,Long collegeId) {
        Category c = categoryRepository.findByIdAndCollegeId(categoryId, collegeId);
        if(c==null){
            throw XException.builder()
                    .number(Code.ERROR)
                    .message("该类别不存在！")
                    .build();
        }
        c.setName(category.getName());
        c.setWeight(category.getWeight());
        categoryRepository.save(c);
    }

    // 删除类别
    @Transactional
    public void deleteCategory(Long categoryId, Long collegeId) {
        if(majorRepository.existsByCategoryId(categoryId)) {
            throw XException.builder()
                    .number(Code.ERROR)
                    .message("该类别有专业，不可删除！")
                    .build();
        }
        categoryRepository.deleteByIdAndCollegeId(categoryId, collegeId);
        userCategoryRepository.deleteByCategoryId(categoryId);
    }

    // 创建专业
    @Transactional
    public void addMajor(Major major, Long catId, List<Long> categoryId) {
        if(categoryId == null){
            throw XException.builder()
                    .code(Code.BAD_REQUEST)
                    .build();
        }
        if(!categoryId.contains(catId)) {
            throw XException.builder()
                    .code(Code.FORBIDDEN)
                    .build();
        }
        log.debug("数组：{}",categoryId);
        major.setCategoryId(catId);
        majorRepository.save(major);
    }

    // 查看所有专业
    public List<Major> listMajors(Long catId, List<Long> categoryId) {
        if(categoryId == null){
            throw XException.builder()
                    .code(Code.BAD_REQUEST)
                    .build();
        }
        if(!categoryId.contains(catId)) {
            throw XException.builder()
                    .code(Code.FORBIDDEN)
                    .build();
        }
        return majorRepository.findByCategoryId(catId);
    }

    // 修改专业
    @Transactional
    public void updateMajor(Long majorId, Major major, Long catId, List<Long> categoryId) {
        if(categoryId == null){
            throw XException.builder()
                    .code(Code.BAD_REQUEST)
                    .build();
        }
        if(!categoryId.contains(catId)) {
            throw XException.builder()
                    .code(Code.FORBIDDEN)
                    .build();
        }
        Major m = majorRepository.findByIdAndCategoryId(majorId, catId);
        if(m==null){
            throw XException.builder()
                    .number(Code.ERROR)
                    .message("该专业不存在！")
                    .build();
        }
        m.setName(major.getName());
        majorRepository.save(m);
    }

    // 删除专业
    @Transactional
    public void deleteMajor(Long majorId, Long catId, List<Long> categoryId) {
        if(categoryId == null){
            throw XException.builder()
                    .code(Code.BAD_REQUEST)
                    .build();
        }
        if(!categoryId.contains(catId)) {
            throw XException.builder()
                    .code(Code.FORBIDDEN)
                    .build();
        }
        Major m = majorRepository.findByIdAndCategoryId(majorId, catId);
        if(m==null){
            throw XException.builder()
                    .number(Code.ERROR)
                    .message("该专业不存在！")
                    .build();
        }
        if(userRepository.existsByMajorId(majorId)) {
            throw XException.builder()
                    .number(Code.ERROR)
                    .message("该专业有学生，不可删除！")
                    .build();
        }
        majorRepository.delete(m);
    }
}
