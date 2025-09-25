package org.example.graduaterecommendationevaluation.repository;

import org.example.graduaterecommendationevaluation.dox.Category;
import org.springframework.data.repository.ListCrudRepository;

public interface CategoryRepository extends ListCrudRepository<Category,Long> {
}
