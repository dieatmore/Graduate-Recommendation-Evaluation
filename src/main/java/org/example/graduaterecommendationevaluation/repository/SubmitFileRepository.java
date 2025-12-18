package org.example.graduaterecommendationevaluation.repository;

import org.example.graduaterecommendationevaluation.dox.SubmitFile;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;


public interface SubmitFileRepository extends ListCrudRepository<SubmitFile,Long> {
    @Query("""
           select *
           from submit_file sf
           where sf.target_submit_id =:targetSubmitId
           and sf.filename =:validFilename
           """)
    SubmitFile findByTargetSubmitIdAndFilename(Long targetSubmitId, String validFilename);

    List<SubmitFile> findByTargetSubmitId(Long targetSubmitId);
}
