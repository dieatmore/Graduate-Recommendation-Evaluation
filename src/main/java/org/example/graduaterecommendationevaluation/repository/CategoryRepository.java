package org.example.graduaterecommendationevaluation.repository;

import org.example.graduaterecommendationevaluation.dox.Category;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface CategoryRepository extends ListCrudRepository<Category,Long> {
    // 根据用户id查看类别信息
    @Query(
            """
            select * from category c, user_category uc
            where c.id = uc.category_id and uc.user_id = :userId
            """
    )
    List<Category> getCategoryByUserId(Long userId);

    List<Category> findByCollegeId(Long collegeId);

    Category findByIdAndCollegeId(Long categoryId, Long collegeId);

    void deleteByIdAndCollegeId(Long categoryId, Long collegeId);

    boolean existsByCollegeId(Long collegeId);
}
