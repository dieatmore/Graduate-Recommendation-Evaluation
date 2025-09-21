# 用户表
create table if not exists `user`
(
    id          char(19)      primary key ,
    account     varchar(10)   not null ,
    password    varchar(65)   not null ,
    name        varchar(10)   not null ,
    phone       varchar(11)   null ,
    role        char(4)       not null ,
    college_id  char(19)      not null ,
    category_id char(19)      not null ,
    major_id    char(19)      not null,

    create_time datetime         not null default current_timestamp,
    update_time datetime         not null default current_timestamp on update current_timestamp,

    unique (account),
    index (major_id)
);

# 学院表
create table if not exists `college`
(
    id         char(19)      primary key ,
    name       varchar(10)   not null ,

    create_time datetime         not null default current_timestamp,
    update_time datetime         not null default current_timestamp on update current_timestamp
);

# 类别表
create table if not exists `category`
(
    id         char(19)      primary key ,
    name       varchar(10)   not null ,
    college_id char(19)      not null ,
    user_id    char(19)      null comment '导师管理' ,
    weight     json          not null comment '{"score", "weight"}' ,

    create_time datetime         not null default current_timestamp,
    update_time datetime         not null default current_timestamp on update current_timestamp,

    index (college_id),
    index (user_id)
);

# 专业表
create table if not exists `major`
(
    id          char(19)      primary key ,
    name        varchar(10)   not null ,
    category_id char(19)      not null ,

    create_time datetime         not null default current_timestamp,
    update_time datetime         not null default current_timestamp on update current_timestamp,

    index (category_id)
);

# 成绩表
create table if not exists `score`
(
    id         char(19)       primary key ,
    user_id    char(19)       not null ,
    scorex     double         not null check ( scorex > 0 and scorex <= 100 ) ,
    ranking    smallint       not null ,
    status     tinyint        not null check (status in (0,1)) default 0 comment '0未认定 1已认定',

    create_time datetime         not null default current_timestamp,
    update_time datetime         not null default current_timestamp on update current_timestamp,

    index (user_id, status)
);

# 指标节点表
create table if not exists `target_node`
(
    id            char(19)        primary key ,
    parent_id     char(19)        null ,
    name          varchar(100)    not null ,
    category_id   char(19)        not null ,
    max_mark      double          not null comment '上限分数',
    max_number    tinyint         null comment '最大项数' ,
    last_time     datetime        not null comment '截至日期',
    note          text            null comment '规则说明',

    create_time datetime         not null default current_timestamp,
    update_time datetime         not null default current_timestamp on update current_timestamp,

    index (category_id, parent_id)
);

# 指标提交表
create table if not exists `target_submit`
(
    id             char(19)               primary key ,
    user_id        char(19)               not null comment '学生id',
    target_node_id char(19)               not null ,
    mark           double                 null ,
    name           varchar(200)           not null comment '提交项名称' ,
    comment        text                   null comment '提交说明',
    status         tinyint                not null comment '0审核中、1待修改、2被驳回、3已认定' ,
    file           json                   null comment '[{"filename", "path"}]' ,
    record         json                   null comment '[{"userId", "name", "comment", "time"}](导师审批记录信息)' ,

    create_time datetime         not null default current_timestamp,
    update_time datetime         not null default current_timestamp on update current_timestamp,

    index (user_id, target_node_id, status),
    index ((cast(record ->> '$.userId' as char(19)) collate utf8mb4_bin), status)
);