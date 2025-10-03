package org.example.graduaterecommendationevaluation.repository;

import org.example.graduaterecommendationevaluation.dox.TargetSubmit;
import org.example.graduaterecommendationevaluation.dto.SubmitDTO;
import org.example.graduaterecommendationevaluation.mapper.SubmitExtractor;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface TargetSubmitRepository extends ListCrudRepository<TargetSubmit,Long> {

    @Query(value = """
           select ts.id as ts_id,
                  ts.name as ts_name,
                  ts.status as ts_status,
                  ts.mark as ts_mark,
                  ts.comment as ts_comment,
                  ts.record as ts_record,
                  sf.id as sf_id,
                  sf.filename as sf_filename
           from target_submit ts
               left join submit_file sf on ts.id = sf.target_submit_id
           where ts.user_id = :uid
             and ts.root_node_id = :rootId
           """,
           resultSetExtractorClass = SubmitExtractor.class)
    List<SubmitDTO> listSubmitAndFiles(Long rootId, Long uid);
}
