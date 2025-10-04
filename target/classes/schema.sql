# 用户表
create table if not exists `user`
(
    id          bigint        primary key ,
    account     varchar(10)   not null ,
    password    varchar(65)   not null ,
    name        varchar(10)   not null ,
    phone       varchar(11)   null ,
    role        char(4)       not null ,
    college_id  bigint        null ,
    category_id bigint        null ,
    major_id    bigint        null,

    create_time datetime         not null default current_timestamp,
    update_time datetime         not null default current_timestamp on update current_timestamp,

    unique (account),
    index (category_id) ,
    index (major_id)
);

# 学院表
create table if not exists `college`
(
    id         bigint        primary key ,
    name       varchar(10)   not null ,

    create_time datetime         not null default current_timestamp,
    update_time datetime         not null default current_timestamp on update current_timestamp
);

# 类别表
create table if not exists `category`
(
    id         bigint        primary key ,
    name       varchar(10)   not null ,
    college_id bigint        not null ,
    weight     json          not null comment '{"scoreWeight", "scoreAll"}(加权成绩，综合成绩)' ,

    create_time datetime         not null default current_timestamp,
    update_time datetime         not null default current_timestamp on update current_timestamp,

    index (college_id)
);

# 专业表
create table if not exists `major`
(
    id          bigint        primary key ,
    name        varchar(10)   not null ,
    category_id bigint        not null ,

    create_time datetime         not null default current_timestamp,
    update_time datetime         not null default current_timestamp on update current_timestamp,

    index (category_id)
);

# 用户与类别关系表(学院和导师一对多个类别)
create table if not exists `user_category` 
(
    id           bigint     primary key ,
    user_id      bigint     not null ,
    category_id  bigint     not null ,

    create_time datetime         not null default current_timestamp,
    update_time datetime         not null default current_timestamp on update current_timestamp,
    
    index (user_id),
    index (category_id)
);

# 成绩表
create table if not exists `score`
(
    id         bigint         primary key ,
    user_id    bigint         not null ,
    scorex     decimal(5, 2) unsigned not null check ( scorex > 0 and scorex <= 100 ) ,
    ranking    tinyint unsigned not null ,
    status     tinyint        not null check (status in (0,1)) default 0 comment '0未认定 1已认定',

    create_time datetime         not null default current_timestamp,
    update_time datetime         not null default current_timestamp on update current_timestamp,

    unique (user_id)
);

# 指标节点表
create table if not exists `target_node`
(
    id            bigint          primary key ,
    parent_id     bigint          null ,
    name          varchar(100)    not null ,
    category_id   bigint          not null ,
    max_mark      decimal(5, 2) unsigned not null comment '上限分数',
    max_number    tinyint unsigned null comment '最大项数' ,
    comment       text            null comment '规则说明',

    create_time datetime         not null default current_timestamp,
    update_time datetime         not null default current_timestamp on update current_timestamp,

    index (parent_id),
    index (category_id, parent_id)
);

# 指标提交表
create table if not exists `target_submit`
(
    id             bigint                 primary key ,
    user_id        bigint                 not null comment '学生id',
    target_node_id bigint                 not null ,
    root_node_id bigint                   not null ,
    mark           decimal(5, 2) unsigned        null ,
    name           varchar(200)           not null comment '提交项名称' ,
    comment        text                   null comment '提交说明',
    status         char(4)                not null comment '已提交、待修改、被驳回、已认定' ,
    record         json                   not null comment '[{"username", "mark", "comment", "time"}]' ,


    create_time datetime         not null default current_timestamp,
    update_time datetime         not null default current_timestamp on update current_timestamp,

    index (user_id, root_node_id, target_node_id ,status)
);

# 提交文件表
create table if not exists `submit_file`
(
    id               bigint         primary key,
    target_submit_id bigint         not null,
    filename         varchar(100)   not null,
    path             varchar(100)   not null,


    create_time      datetime     not null default current_timestamp,
    update_time      datetime     not null default current_timestamp on update current_timestamp,

    index (target_submit_id)
);

