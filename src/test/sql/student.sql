# 学生查找一级指标点下的所有信息(补充root_node_id字段)
explain
select name, status, mark, filename, comment, record
from target_submit ts
    left join submit_file sf on ts.id = sf.target_submit_id
where ts.user_id = 1421465359751114752
  and ts.root_node_id = 1421469131957137408;

# 学生对于指定指标点上传佐证拼接路径
explain
select
    concat_ws(
            '/',
            co.name,
            ca.name,
            m.name,
            concat(u.name, '-', u.account)
    ) as path
from
    user u
        join major m on u.major_id = m.id
        join category ca on m.category_id = ca.id
        join college co on ca.college_id = co.id
where u.id = 1716251082372431693;

# 学生对于指定指标点上传佐证拼接文件名
explain
select
    concat_ws(
    '-',
    ts.name,
    ts.user_id
    ) as filename
from
    target_submit ts
where ts.user_id = 1716251082372431693 and ts.target_node_id = 1374672131214278117;

# 学生新增提交项(record初始化为json数组)
insert into target_submit (
                           id ,
                           user_id ,
                           target_node_id ,
                           root_node_id ,
                           name ,
                           comment ,
                           record
)
values (
           1991171284370431693 ,
           1716251082372431693 ,
           1354718135214278117 ,
           1342672131274220115 ,
           '学术专长-科技竞赛2' ,
           '蓝桥杯' ,
           '[]'
       );

# 学生查询个人的成绩统计信息
explain
select
#     已认定成绩
    sum(case when status = 3 then ts.mark else 0 end) as confirmed_score,
#     已认定项
    sum(case when status = 3 then 1 else 0 end) as confirmed_items,
#     待审核项
    sum(case when status = 0 then 1 else 0 end) as pending_items,
#     待修改项
    sum(case when status = 1 then 1 else 0 end) as modify_items,
#     已驳回项
    sum(case when status = 2 then 1 else 0 end) as rejected_items,
#     总提交项
    count(*) as total_items
from target_submit ts
where ts.user_id = 1716251082372431693;

# 学生根据根节点id查看提交信息
select *
from target_submit ts
         left join submit_file sf on ts.id = sf.target_submit_id
where ts.user_id = 1421465359751114752
  and ts.root_node_id = 1421469131957137408