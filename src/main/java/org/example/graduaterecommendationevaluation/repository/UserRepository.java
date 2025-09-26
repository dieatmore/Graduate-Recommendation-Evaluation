package org.example.graduaterecommendationevaluation.repository;

import org.example.graduaterecommendationevaluation.dox.User;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;

public interface UserRepository extends ListCrudRepository<User, Long> {
    User findByAccount(String account);

    boolean existsByMajorId(Long majorId);

    @Query("""
           select id from user where account=:account
           """)
    Long findIdByAccount(String account);
}
