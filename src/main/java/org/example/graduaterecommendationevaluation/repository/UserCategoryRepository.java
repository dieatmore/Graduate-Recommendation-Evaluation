package org.example.graduaterecommendationevaluation.repository;

import org.example.graduaterecommendationevaluation.dox.UserCategory;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface UserCategoryRepository extends ListCrudRepository<UserCategory,Long> {

    void deleteByCategoryId(Long categoryId);

    @Query("""
             SELECT category_id FROM user_category WHERE user_id = :userId
           """)
    List<Long> findCategoryIdByUserId(Long userId);
}
