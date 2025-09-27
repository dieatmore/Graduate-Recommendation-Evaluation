package org.example.graduaterecommendationevaluation.repository;

import org.example.graduaterecommendationevaluation.dox.TargetNode;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface TargetNodeRepository extends ListCrudRepository<TargetNode,Long> {

    @Query("""
            with recursive cte as (
               select * from target_node tn
                        where tn.category_id = 1421433267587706880
                              and tn.parent_id is null
               union all
               select tn.* from target_node tn
                   join cte where tn.parent_id = cte.id
           )
           select * from cte;
           """)
    List<TargetNode> findAllByCategoryIdRecursive(Long categoryId);
}
