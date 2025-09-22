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