package org.example.graduaterecommendationevaluation.repository;

import lombok.extern.slf4j.Slf4j;
import org.example.graduaterecommendationevaluation.dox.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void getCategoryByUserId() {
        List<Category> categoryList = categoryRepository.getCategoryByUserId(2L);
        log.debug(categoryList.toString());
    }

    @Test
    void existsByCollegeId() {
        boolean result = categoryRepository.existsByCollegeId(Long.valueOf("1421038251111411712"));
        if (result) {
            log.debug("查到了查到了查到了查到了查到了");
        }
    }
}