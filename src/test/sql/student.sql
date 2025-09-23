# 学生查找某个根节点下的所有的提交信息(补充root_node_id字段)
explain
select ts.name as name, mark, status, ts.comment, file, tn.comment
from target_submit ts join target_node tn
on ts.target_node_id = tn.id
where ts.user_id = 1716251082372431693 and ts.root_node_id = 1342672131274220115;

# 学生上传佐证(json属性数据库层面不能default'[]'，需要手动初始化file为json数组)
update target_submit ts
set ts.file = JSON_ARRAY_APPEND(
        file,
        '$',  -- 表示往数组末尾追加
        '{"path": "/uploads/new", "filename": "第一等级.pdf"}'
              )
where ts.id = 1323900482271434693 and ts.user_id = 1716251082372431693;

# 学生查找某个根节点的某个状态下的所有提交信息
explain
select ts.name as name, mark, status, ts.comment, file, tn.comment
from target_submit ts join target_node tn
on ts.target_node_id = tn.id
where ts.user_id = 1716251082372431693
  and ts.root_node_id = 1342672131274220115
  and ts.status = 0;

# 学生新增提交项
insert into target_submit (
                           id ,
                           user_id ,
                           target_node_id ,
                           root_node_id ,
                           name ,
                           comment ,
                           file
)
values (
           1991171284370431693 ,
           1716251082372431693 ,
           1354718135214278117 ,
           1342672131274220115 ,
           '学术专长-科技竞赛2' ,
           '蓝桥杯' ,
           '[]'
       )