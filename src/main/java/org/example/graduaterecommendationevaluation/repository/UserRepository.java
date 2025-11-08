package org.example.graduaterecommendationevaluation.repository;

import org.example.graduaterecommendationevaluation.dox.User;
import org.example.graduaterecommendationevaluation.dto.StudentInfoDTO;
import org.example.graduaterecommendationevaluation.dto.StudentsDTO;
import org.example.graduaterecommendationevaluation.dto.SubmitDTO;
import org.example.graduaterecommendationevaluation.mapper.SubmitExtractor;
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
               u.phone,
               s.scorex as scorex,
               s.ranking as ranking,
           #     已认定成绩
               sum(case when ts.status = '59G7' then ts.mark else 0 end) as confirmed_score,
           #     已认定项数
               sum(case when ts.status = '59G7' then 1 else 0 end) as confirmed_items,
           #     待审核项数
               sum(case when ts.status = 'y02Q' then 1 else 0 end) as pending_items,
           #     待修改项数
               sum(case when ts.status = 'P5eR' then 1 else 0 end) as modify_items,
           #     已驳回项数
               sum(case when ts.status = 'b7Yz' then 1 else 0 end) as rejected_items,
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

    @Query("""
           select * from user u where u.role = :role and u.college_id = :id
           """)
    List<User> findByCollegeIdAndRole(Long id, String role);

    @Query(value = """
           select ts.id as ts_id,
                  ts.name as ts_name,
                  ts.status as ts_status,
                  ts.mark as ts_mark,
                  ts.comment as ts_comment,
                  ts.record as ts_record,
                  tn.max_mark as max_mark,
                  sf.id as sf_id,
                  sf.filename as sf_filename
           from target_submit ts
           left join submit_file sf on ts.id = sf.target_submit_id
           left join target_node tn on ts.target_node_id = tn.id
           where ts.user_id = :uid
           """,
            resultSetExtractorClass = SubmitExtractor.class)
    List<SubmitDTO> studentDetail(Long uid);

    @Query("""
           select u.id, u.name, u.phone, u.account,u.major_id, s.scorex, s.ranking,s.status
           from user u
           left join score s on u.id = s.user_id
           where u.id = :uid
           """)
    StudentInfoDTO getInfoById(Long uid);
}
