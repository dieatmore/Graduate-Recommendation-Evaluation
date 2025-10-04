package org.example.graduaterecommendationevaluation.repository;

import org.example.graduaterecommendationevaluation.dox.User;
import org.example.graduaterecommendationevaluation.dto.StudentsDTO;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface UserRepository extends ListCrudRepository<User, Long> {
    User findByAccount(String account);

    boolean existsByMajorId(Long majorId);

    @Query("""
           select id from user where account=:account
           """)
    Long findIdByAccount(String account);

    @Query("""
            select
               u.id,
               u.name,
               u.account,
               s.scorex as scorex,
               s.ranking as ranking,
           #     已认定成绩
               sum(case when ts.status = 3 then ts.mark else 0 end) as confirmed_score,
           #     已认定项数
               sum(case when ts.status = 3 then 1 else 0 end) as confirmed_items,
           #     待审核项数
               sum(case when ts.status = 0 then 1 else 0 end) as pending_items,
           #     待修改项数
               sum(case when ts.status = 1 then 1 else 0 end) as modify_items,
           #     已驳回项数
               sum(case when ts.status = 2 then 1 else 0 end) as rejected_items,
           #     总提交项数
               count(ts.id) as total_items
           from user u
           left join target_submit ts on ts.user_id = u.id
           left join score s on s.user_id = u.id
           where u.major_id = :majorId
             and u.role = :role
           group by u.id, u.name, u.account, s.scorex, s.ranking, s.status
           order by
               case when s.status = 1 then 0 else 1 end,
               s.ranking
           """)
    List<StudentsDTO> listStudents(Long majorId, String role);
}
