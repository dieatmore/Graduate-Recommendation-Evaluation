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

    // 查找学院
    public College getCollege(Long collegeId) {
        return collegeRepository.findById(collegeId)
                .orElseThrow(()-> XException.builder()
                        .number(Code.ERROR)
                        .message("不存在该学院！")
                        .build());
    }

    // 修改学院
    @Transactional
    public void updateCollege(College c,College college) {
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
    public void addCategory(Category category) {
        categoryRepository.save(category);
    }

    // 根据学院id获取类别组
    public List<Category> getCategorysBycolId(Long collegeId) {
        return categoryRepository.findByCollegeId(collegeId);
    }

    // 根据学院id和类别id获取类别
    public Category getCatsBycolIdAndCatId(Long categoryId , Long collegeId) {
        return categoryRepository.findByIdAndCollegeId(categoryId, collegeId);
    }

    // 修改类别
    @Transactional
    public void updateCategory(Category c,Category category) {
        categoryRepository.save(c);
    }

    // 删除类别
    @Transactional
    public void deleteCategory(Long categoryId) {
        if(majorRepository.existsByCategoryId(categoryId)) {
            throw XException.builder()
                    .number(Code.ERROR)
                    .message("该类别有专业，不可删除！")
                    .build();
        }
        if(userCategoryRepository.existsByCategoryId(categoryId)) {
            throw XException.builder()
                    .number(Code.ERROR)
                    .message("该类别有导师管理，不可删除！")
                    .build();
        }
        categoryRepository.deleteById(categoryId);
    }

    // 创建专业
    @Transactional
    public void addMajor(Major major) {
        majorRepository.save(major);
    }

    // 查看所有专业
    public List<Major> listMajors(Long catId) {
        return majorRepository.findByCategoryId(catId);
    }

    // 判断专业存在
    public Major findMajorByMidAndCatId(Long majorId, Long catId) {
        Major m = majorRepository.findByIdAndCategoryId(majorId, catId);
        if(m==null){
            throw XException.builder()
                    .number(Code.ERROR)
                    .message("该专业不存在！")
                    .build();
        }
        return m;
    }

    // 修改专业
    @Transactional
    public void updateMajor(Major major) {
        majorRepository.save(major);
    }

    // 删除专业
    @Transactional
    public void deleteMajor(Long majorId) {
        if(userRepository.existsByMajorId(majorId)) {
            throw XException.builder()
                    .number(Code.ERROR)
                    .message("该专业有学生，不可删除！")
                    .build();
        }
        majorRepository.deleteById(majorId);
    }

    // 根据类别id组查找类别组
    public List<Category> findCatsId(List<Long> catsId) {
        return categoryRepository.findAllById(catsId);
    }
}
