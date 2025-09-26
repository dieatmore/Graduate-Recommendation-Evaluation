package org.example.graduaterecommendationevaluation.repository;

import org.example.graduaterecommendationevaluation.dox.Major;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface MajorRepository extends ListCrudRepository<Major,Long> {
    List<Major> findByCategoryId(Long categoryId);

    Major findByIdAndCategoryId(Long majorId, Long catId);

    boolean existsByCategoryId(Long categoryId);

    @Query("""
           select category_id from major where id=:majorId
           """)
    Long findCategoryIdById(Long majorId);
}
