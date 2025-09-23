# 学院管理员查看所有导师的信息(添加record表) --dto,一对多
explain
select *
from user u
left join record r on u.id = r.user_id
left join target_submit ts on r.target_submit_id = ts.id
where u.college_id = 1766700582275433691 and u.role = 2;

# 学院管理员查看某类别下的所有学生信息--dto，一对多
explain
select *
from user u
left join score s on u.id = s.user_id
left join target_submit ts on u.id = ts.user_id
where u.category_id = 1266750582271434695 and u.role = 3;

# 学院管理员添加推免规则（前端填写name,max_mark,max_number,comment）
insert into target_node (id, parent_id, name, category_id, max_mark, max_number, comment)
values (
           1310954517271434695 ,
           1342672131274220115 ,
           '论文' ,
           1266750582271434695 ,
           20 ,
           2,
           '高质量论文，限两项'
       )