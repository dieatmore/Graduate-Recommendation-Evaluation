package org.example.graduaterecommendationevaluation.repository;

import org.example.graduaterecommendationevaluation.dox.TargetNode;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface TargetNodeRepository extends ListCrudRepository<TargetNode,Long> {

    @Query("""
            with recursive cte as (
               select * from target_node tn
                        where tn.category_id = :categoryId
                              and tn.parent_id is null
               union all
               select tn.* from target_node tn
                   join cte where tn.parent_id = cte.id
           )
           select * from cte;
           """)
    List<TargetNode> findAllByCategoryIdRecursive(Long categoryId);

    @Query("""
           select * from target_node tn
           where tn.category_id = :catId
           and tn.parent_id is null
           """)
    List<TargetNode> findRootByCategoryId(Long catId);

    @Query("""
            with recursive cte as (
               select * from target_node tn
                        where tn.category_id = :catId
                              and tn.parent_id = :parentId
               union all
               select tn.* from target_node tn
                   join cte where tn.parent_id = cte.id
           )
           select * from cte;
           """)
    List<TargetNode> findChildrenByCatIdAndParentId(Long catId, Long parentId);

    @Query("""
           select * from target_node tn
           where id = :nodeId
           and category_id = :catId
           """)
    TargetNode findByIdAndCategoryId(Long nodeId, Long catId);

    @Query("""
           select count(*) from target_node tn
           where tn.parent_id = :nodeId
           """)
    int judgeLeaf(Long nodeId);

    @Query("""
           select parent_id from target_node tn
           where tn.id = :id
           """)
    Long findParentIdById(Long id);
}
