create table file_lover
(
    user_id int    not null,
    file_id bigint not null,
    primary key (user_id, file_id)
);

create table file_record
(
    file_id     bigint auto_increment comment '图像的所属id'
        primary key,
    upload_user int                                not null comment '上传的用户id',
    upload_date datetime default CURRENT_TIMESTAMP not null comment '上传的时间',
    title       char(30)                           null comment '标题',
    introduce   varchar(300)                       null comment '文件的相关介绍',
    lover_num   bigint   default 0                 not null,
    keywords    varchar(300)                       not null comment '图片的标签',
    has_checked bit      default b'0'              not null comment '是否已经确认图片无违规，已确认 1 未确认 0',
    file_path   varchar(400)                       not null,
    constraint file_record_pk
        unique (file_path)
);

create table keyword
(
    id      bigint auto_increment
        primary key,
    keyword char(10) not null,
    constraint keyword_pk_2
        unique (keyword)
);

create table user
(
    user_id       int auto_increment comment '用户唯一id'
        primary key,
    user_name     char(30)  default 'owo'  not null comment '用户名',
    user_password char(255)                not null comment '用户密码，使用应使用加密算法加密',
    user_email    char(50)                 not null comment '用户邮箱',
    register_date date                     not null comment '注册日期',
    banned        bit       default b'0'   not null comment '账号是否被封禁 0 未被封建 1 被封禁',
    roles         char(200) default 'user' not null,
    constraint user_pk_2
        unique (user_email)
);

