package org.example.graduaterecommendationevaluation.repository;

import org.example.graduaterecommendationevaluation.dox.Score;
import org.springframework.data.repository.ListCrudRepository;

public interface ScoreRepository extends ListCrudRepository<Score,Long> {
    Score findByUserId(Long userId);
}
