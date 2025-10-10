package org.example.graduaterecommendationevaluation.repository;

import org.example.graduaterecommendationevaluation.dox.College;
import org.example.graduaterecommendationevaluation.dto.CollegeAdminDTO;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface CollegeRepository extends ListCrudRepository<College,Long> {
    List<College> findByNameContaining(String name);
}
