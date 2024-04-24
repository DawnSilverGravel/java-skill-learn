drop table if exists t_user;
drop table if exists t_role;
drop table if exists t_user_role;
create table if not exists t_user
(
    id       int primary key,
    name     varchar(20) not null,
    password varchar(32) not null default '123456'
);

create table if not exists t_role
(
    id int primary key,
    role_name varchar(45) not null
);

create table if not exists t_user_role
(
    id      int primary key,
    user_id int not null,
    role_id int not null
)