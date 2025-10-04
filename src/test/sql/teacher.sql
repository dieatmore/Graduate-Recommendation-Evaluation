# 导师获取自己管理的类别
explain
select *
from user_category uc
join category c on uc.category_id = c.id
where uc.user_id = 1341271381272930616;

# 导师获取指定类别下全部专业
explain
select *
from major m
join user_category uc on m.category_id = uc.category_id
where uc.user_id = 1341271381272930616
  and uc.category_id = 1266750582271434695;

# 导师获取某专业下的所有学生的信息包括提交项(学生一对多提交节点，应该dto)
# score表数据量过小时是全表扫描，添加数据后走索引
explain
select *
from user u
left join target_submit ts on u.id = ts.user_id
left join score s on u.id = s.user_id
left join major m on u.major_id = m.id
where u.major_id = 1069900462271431694
  and u.role = 3
  and u.category_id = 1266750582271434695;

# 导师审批
update target_submit ts
join user u on u.id = 1341271381272930616
set
    ts.mark = 20 ,
    ts.status = 3 ,
    ts.record = JSON_ARRAY_APPEND(
            record,
            '$',
            JSON_OBJECT(
                    'username', u.name,
                    'mark', '20',
                    'comment', '大创比赛得分20',
                    'time', '2025-9-24'
            )
        )
where ts.user_id = 1716251082372431693
  and ts.target_node_id = 1374672131214278117;

# 导师查询专业下所有学生的成绩统计信息
explain
select
    u.id,
    u.name,
    u.account,
    s.scorex as scorex,
    s.ranking as ranking,
    #     已认定成绩
    sum(case when ts.status = 3 then ts.mark else 0 end) as confirmed_score,
    #     已认定项数
    sum(case when ts.status = 3 then 1 else 0 end) as confirmed_items,
    #     待审核项数
    sum(case when ts.status = 0 then 1 else 0 end) as pending_items,
    #     待修改项数
    sum(case when ts.status = 1 then 1 else 0 end) as modify_items,
    #     已驳回项数
    sum(case when ts.status = 2 then 1 else 0 end) as rejected_items,
    #     总提交项数
    count(ts.id) as total_items
from user u
         left join target_submit ts on ts.user_id = u.id
         left join score s on s.user_id = u.id
where u.major_id = 1421438941696684032
  and u.role = 'po8V'
group by u.id, u.name, u.account, s.scorex, s.ranking, s.status
order by
    case when s.status = 1 then 0 else 1 end,
    s.ranking

