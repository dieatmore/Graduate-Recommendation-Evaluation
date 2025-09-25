package org.example.graduaterecommendationevaluation.repository;

import org.example.graduaterecommendationevaluation.dox.College;
import org.springframework.data.repository.ListCrudRepository;

public interface CollegeRepository extends ListCrudRepository<College,Long> {
}
