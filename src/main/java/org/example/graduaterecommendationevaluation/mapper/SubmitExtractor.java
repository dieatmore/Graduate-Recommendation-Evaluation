package org.example.graduaterecommendationevaluation.mapper;

import org.example.graduaterecommendationevaluation.dto.FileDTO;
import org.example.graduaterecommendationevaluation.dto.SubmitDTO;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubmitExtractor implements ResultSetExtractor<List<SubmitDTO>> {
    @Override
    public List<SubmitDTO> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Long, SubmitDTO> submitDtoMap = new HashMap<>();
        while (rs.next()) {
            Long submitId = rs.getLong("ts_id");
            String submitName = rs.getString("ts_name");
            String status = rs.getString("ts_status");
            Double mark = rs.getDouble("ts_mark");
            Double maxMark = rs.getDouble("max_mark");
            if (rs.wasNull()) {
                mark = null;
            }
            String comment = rs.getString("ts_comment");
            String record = rs.getString("ts_record");

            long fileId = rs.getLong("sf_id");
            String fileName = rs.getString("sf_filename");

            SubmitDTO submitDTO = submitDtoMap.get(submitId);
            if (submitDTO == null) {
                SubmitDTO sd = SubmitDTO.builder()
                        .id(submitId)
                        .name(submitName)
                        .status(status)
                        .mark(mark)
                        .maxMark(maxMark)
                        .files(new ArrayList<>())
                        .comment(comment)
                        .record(record)
                        .build();
                submitDtoMap.put(submitId, sd);
            }
            if(fileId != 0 ) {
                FileDTO fileDTO = FileDTO.builder()
                        .id(fileId)
                        .fileName(fileName)
                        .build();
                if (submitDTO != null) {
                    submitDTO.getFiles().add(fileDTO);
                }
            }
        }
        return new ArrayList<>(submitDtoMap.values());
    }
}
