package org.example.graduaterecommendationevaluation.repository;

import org.example.graduaterecommendationevaluation.dox.User;
import org.springframework.data.repository.ListCrudRepository;

public interface UserRepository extends ListCrudRepository<User, Long> {
}
