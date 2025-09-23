# 查找根节点
explain
select *
from target_node tn
where tn.category_id = 1266750582271434695 and tn.parent_id is null;

#根据节点id查找子节点
explain
select *
from target_node tn
where tn.category_id = 1266750582271434695 and tn.parent_id = 1342672131274220115;

# 递归查询所有子节点
with recursive cte as (
    select * from target_node tn
             where tn.category_id = 1266750582271434695
                   and tn.parent_id = 1342672131274220115
    union all
    select tn.* from target_node tn
        join cte where tn.parent_id = cte.id
)
select * from cte;