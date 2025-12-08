package org.example.graduaterecommendationevaluation.repository;

import org.example.graduaterecommendationevaluation.dox.SubmitFile;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;


public interface SubmitFileRepository extends ListCrudRepository<SubmitFile,Long> {
    @Query("""
           select *
           from submit_file sf
           where sf.target_submit_id =:targetSubmitId
           and sf.filename =:validFilename
           """)
    SubmitFile findByTargetSubmitIdAndFilename(Long targetSubmitId, String validFilename);
}
