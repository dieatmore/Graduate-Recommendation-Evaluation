# 导师获取自己管理的类别
explain
select *
from user_category uc
join category c on uc.category_id = c.id
where uc.user_id = 1341271381272930616;

# 导师获取某专业下的所有学生的信息以及提交项(学生一对多提交节点，应该dto)
# score表数据量过小时是全表扫描，添加数据后走索引
explain
select m.name, account, u.name, scorex, ranking, ts.name, comment, mark, ts.status, file
from user u
left join target_submit ts on u.id = ts.user_id
left join score s on u.id = s.user_id
left join major m on u.major_id = m.id
where u.major_id = 1069900462271431694 and u.role = 3;

# 导师审批
update target_submit ts
set
    ts.mark = 20 ,
    ts.status = 3
where ts.user_id = 1716251082372431693
  and ts.target_node_id = 1354612135214278117;

# 审批产生记录
insert into record (id, target_submit_id, user_id, user_name, comment)
values (
        1305403482971431694 ,
        1354612135214278117 ,
        1341271381272930616 ,
        '王波' ,
        '蓝桥杯比赛，得分20'
       )